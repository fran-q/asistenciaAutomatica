// Pagina: CRUD de horarios de clase

// Cache global de asignaciones (reutilizada por asistencias)
let _asignacionesCache = null;

// Arma opciones de asignaciones activas; cruza multiples caches para el label compuesto
// Label resultante: "Profesor - Curso / Materia"
async function getAsignacionesOptions() {
    if (!_asignacionesCache) {
        try { _asignacionesCache = await Api.get('/asignaciones'); } catch { _asignacionesCache = []; }
    }
    // Carga todas las caches necesarias para resolver nombres
    await getProfesoresOptions();
    await getUsuariosOptions();
    await getCursoMateriasOptions();
    return _asignacionesCache.filter(a => a.activo !== false).map(a => {
        const prof = _profesoresCache?.find(p => p.idProfesor === a.idProfesor);
        const user = _usuariosCache?.find(u => u.idUsuario === prof?.idUsuario);
        const cm = _cursoMateriasCache?.find(c => c.idCursoMateria === a.idCursoMateria);
        const curso = _cursosCache?.find(c => c.idCurso === cm?.idCurso);
        const materia = _materiasCache?.find(m => m.idMateria === cm?.idMateria);
        const profName = user ? `${user.nombre} ${user.apellido}` : `Prof #${a.idProfesor}`;
        const label = `${profName} - ${curso?.nombre || '?'} / ${materia?.nombre || '?'}`;
        return { value: a.idAsignacion, label };
    });
}

// --- Configuracion CRUD ---
const HorariosPage = {
    async render() {
        const asigOpts = await getAsignacionesOptions();
        const page = createCrudPage({
            title: 'Horarios',
            icon: 'bi-clock',
            endpoint: '/horarios',
            requiredDeps: [{
                check: async () => (await getAsignacionesOptions()).length > 0,
                message: 'No es posible crear un horario, primero registra al menos una asignacion'
            }],
            columns: [
                { label: 'ID', idKey: 'idHorario' },
                { label: 'Asignacion' },
                { label: 'Dia', width: 'col-md' },
                { label: 'Inicio', width: 'col-sm' },
                { label: 'Fin', width: 'col-sm' },
                { label: 'Estado', width: 'col-sm' }
            ],
            searchFields: [
                { key: 'asignacion', label: 'Asignacion', getValue: (h) => {
                    const asig = asigOpts.find(o => o.value === h.idAsignacion);
                    return asig?.label || '';
                }},
                { key: 'dia', label: 'Dia', getValue: (h) => h.diaSemana || '' }
            ],
            // Campos: asignacion (select), dia (enum), hora inicio/fin agrupadas en fila
            formFields: [
                { key: 'idAsignacion', label: 'Asignacion', type: 'select', options: asigOpts },
                { key: 'diaSemana', label: 'Dia de la semana', type: 'enum', enumName: 'diaSemana' },
                { type: 'row-start' },
                { key: 'horaInicio', label: 'Hora Inicio', type: 'time' },
                { key: 'horaFin', label: 'Hora Fin', type: 'time' },
                { type: 'row-end' }
            ],
            formToDto: (d) => ({
                idAsignacion: d.idAsignacion ? parseInt(d.idAsignacion) : null,
                diaSemana: d.diaSemana,
                horaInicio: d.horaInicio,
                horaFin: d.horaFin
            }),
            mapRow: (h) => {
                const asig = asigOpts.find(o => o.value === h.idAsignacion);
                return [
                    h.idHorario,
                    asig ? asig.label : `#${h.idAsignacion}`,
                    h.diaSemana ? `<span class="badge badge-accent">${h.diaSemana}</span>` : '-',
                    UI.formatTime(h.horaInicio),
                    UI.formatTime(h.horaFin),
                    UI.activoBadge(h.activo)
                ];
            }
        });
        page.render();
    }
};

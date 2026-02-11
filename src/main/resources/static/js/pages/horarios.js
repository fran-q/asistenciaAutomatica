let _asignacionesCache = null;

async function getAsignacionesOptions() {
    if (!_asignacionesCache) {
        try { _asignacionesCache = await Api.get('/asignaciones'); } catch { _asignacionesCache = []; }
    }
    await getProfesoresOptions();
    await getUsuariosOptions();
    await getCursoMateriasOptions();
    return _asignacionesCache.map(a => {
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

const HorariosPage = {
    async render() {
        const asigOpts = await getAsignacionesOptions();
        const page = createCrudPage({
            title: 'Horarios',
            icon: 'bi-clock',
            endpoint: '/horarios',
            columns: [
                { label: 'ID', idKey: 'idHorario' },
                { label: 'Asignacion' },
                { label: 'Dia' },
                { label: 'Inicio' },
                { label: 'Fin' },
                { label: 'Estado' }
            ],
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
            },
            searchFilter: (h, s) => {
                const asig = asigOpts.find(o => o.value === h.idAsignacion);
                return `${asig?.label} ${h.diaSemana}`.toLowerCase().includes(s);
            }
        });
        page.render();
    }
};

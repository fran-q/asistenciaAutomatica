// Pagina: Inscripciones de alumnos a cursos

// Cache global de alumnos para dropdown
let _alumnosCache = null;

// Obtiene opciones de alumnos activos; cruza con _usuariosCache para nombre completo
async function getAlumnosOptions() {
    if (!_alumnosCache) {
        try { _alumnosCache = await Api.get('/alumnos'); } catch { _alumnosCache = []; }
    }
    await getUsuariosOptions();
    return _alumnosCache.filter(a => a.activo !== false).map(a => {
        const user = _usuariosCache?.find(u => u.idUsuario === a.idUsuario);
        const label = user ? `${user.nombre} ${user.apellido}` : `Alumno #${a.idAlumno}`;
        return { value: a.idAlumno, label };
    });
}

// --- Configuracion CRUD ---
const InscripcionesPage = {
    async render() {
        const [alumnoOpts, cursoOpts] = await Promise.all([getAlumnosOptions(), getCursosOptions()]);
        const page = createCrudPage({
            title: 'Inscripciones Alumnos',
            icon: 'bi-pencil-square',
            endpoint: '/inscripciones',
            noEdit: true,
            // Requiere al menos un alumno y un curso para poder inscribir
            requiredDeps: [
                {
                    check: async () => (await getAlumnosOptions()).length > 0,
                    message: 'No es posible crear una inscripcion, primero registra al menos un alumno'
                },
                {
                    check: async () => (await getCursosOptions()).length > 0,
                    message: 'No es posible crear una inscripcion, primero registra al menos un curso'
                }
            ],
            columns: [
                { label: 'ID', idKey: 'idAlumnoCurso' }, // Nota: el ID es idAlumnoCurso, no idInscripcion
                { label: 'Alumno' },
                { label: 'Curso' },
                { label: 'Estado', width: 'col-sm' }
            ],
            searchFields: [
                { key: 'alumno', label: 'Alumno', getValue: (i) => {
                    const alumno = alumnoOpts.find(o => o.value === i.idAlumno);
                    return alumno?.label || '';
                }},
                { key: 'curso', label: 'Curso', getValue: (i) => {
                    const curso = cursoOpts.find(o => o.value === i.idCurso);
                    return curso?.label || '';
                }}
            ],
            formFields: [
                { key: 'idAlumno', label: 'Alumno', type: 'select', options: alumnoOpts },
                { key: 'idCurso', label: 'Curso', type: 'select', options: cursoOpts }
            ],
            formToDto: (d) => ({
                idAlumno: d.idAlumno ? parseInt(d.idAlumno) : null,
                idCurso: d.idCurso ? parseInt(d.idCurso) : null
            }),
            mapRow: (i) => {
                const alumno = alumnoOpts.find(o => o.value === i.idAlumno);
                const curso = cursoOpts.find(o => o.value === i.idCurso);
                return [
                    i.idAlumnoCurso,
                    alumno ? alumno.label : `#${i.idAlumno}`,
                    curso ? curso.label : `#${i.idCurso}`,
                    UI.activoBadge(i.activo)
                ];
            }
        });
        page.render();
    }
};

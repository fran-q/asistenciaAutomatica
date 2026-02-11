let _alumnosCache = null;

async function getAlumnosOptions() {
    if (!_alumnosCache) {
        try { _alumnosCache = await Api.get('/alumnos'); } catch { _alumnosCache = []; }
    }
    await getUsuariosOptions();
    return _alumnosCache.map(a => {
        const user = _usuariosCache?.find(u => u.idUsuario === a.idUsuario);
        const label = user ? `${user.nombre} ${user.apellido}` : `Alumno #${a.idAlumno}`;
        return { value: a.idAlumno, label };
    });
}

const InscripcionesPage = {
    async render() {
        const [alumnoOpts, cursoOpts] = await Promise.all([getAlumnosOptions(), getCursosOptions()]);
        const page = createCrudPage({
            title: 'Inscripciones',
            icon: 'bi-pencil-square',
            endpoint: '/inscripciones',
            columns: [
                { label: 'ID', idKey: 'idAlumnoCurso' },
                { label: 'Alumno' },
                { label: 'Curso' },
                { label: 'Estado' }
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
            },
            searchFilter: (i, s) => {
                const alumno = alumnoOpts.find(o => o.value === i.idAlumno);
                return `${alumno?.label}`.toLowerCase().includes(s);
            }
        });
        page.render();
    }
};

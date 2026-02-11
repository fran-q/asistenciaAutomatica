let _profesoresCache = null;
let _cursoMateriasCache = null;

async function getProfesoresOptions() {
    if (!_profesoresCache) {
        try { _profesoresCache = await Api.get('/profesores'); } catch { _profesoresCache = []; }
    }
    await getUsuariosOptions();
    return _profesoresCache.map(p => {
        const user = _usuariosCache?.find(u => u.idUsuario === p.idUsuario);
        const label = user ? `${user.nombre} ${user.apellido}` : `Prof #${p.idProfesor}`;
        return { value: p.idProfesor, label };
    });
}

async function getCursoMateriasOptions() {
    if (!_cursoMateriasCache) {
        try { _cursoMateriasCache = await Api.get('/curso-materias'); } catch { _cursoMateriasCache = []; }
    }
    await getCursosOptions();
    await getMateriasOptions();
    return _cursoMateriasCache.map(cm => {
        const curso = _cursosCache?.find(c => c.idCurso === cm.idCurso);
        const materia = _materiasCache?.find(m => m.idMateria === cm.idMateria);
        return { value: cm.idCursoMateria, label: `${curso?.nombre || '#' + cm.idCurso} - ${materia?.nombre || '#' + cm.idMateria}` };
    });
}

const AsignacionesPage = {
    async render() {
        const [profOpts, cmOpts] = await Promise.all([getProfesoresOptions(), getCursoMateriasOptions()]);
        const page = createCrudPage({
            title: 'Asignaciones',
            icon: 'bi-diagram-3',
            endpoint: '/asignaciones',
            columns: [
                { label: 'ID', idKey: 'idAsignacion' },
                { label: 'Profesor' },
                { label: 'Curso-Materia' },
                { label: 'Estado' }
            ],
            formFields: [
                { key: 'idProfesor', label: 'Profesor', type: 'select', options: profOpts },
                { key: 'idCursoMateria', label: 'Curso-Materia', type: 'select', options: cmOpts }
            ],
            formToDto: (d) => ({
                idProfesor: d.idProfesor ? parseInt(d.idProfesor) : null,
                idCursoMateria: d.idCursoMateria ? parseInt(d.idCursoMateria) : null
            }),
            mapRow: (a) => {
                const prof = profOpts.find(o => o.value === a.idProfesor);
                const cm = cmOpts.find(o => o.value === a.idCursoMateria);
                return [
                    a.idAsignacion,
                    prof ? prof.label : `#${a.idProfesor}`,
                    cm ? cm.label : `#${a.idCursoMateria}`,
                    UI.activoBadge(a.activo)
                ];
            },
            searchFilter: (a, s) => {
                const prof = profOpts.find(o => o.value === a.idProfesor);
                return `${prof?.label}`.toLowerCase().includes(s);
            }
        });
        page.render();
    }
};

// Pagina: Cargos por profesor (asignacion de profesor a curso-materia)

// --- Caches globales para dropdowns (reutilizadas por horarios y asistencias) ---
let _profesoresCache = null;
let _cursoMateriasCache = null;

// Obtiene opciones de profesores activos; cruza con _usuariosCache para nombre completo
async function getProfesoresOptions() {
    if (!_profesoresCache) {
        try { _profesoresCache = await Api.get('/profesores'); } catch { _profesoresCache = []; }
    }
    await getUsuariosOptions(); // Necesita la cache de usuarios para armar el label
    return _profesoresCache.filter(p => p.activo !== false).map(p => {
        const user = _usuariosCache?.find(u => u.idUsuario === p.idUsuario);
        const label = user ? `${user.nombre} ${user.apellido}` : `Prof #${p.idProfesor}`;
        return { value: p.idProfesor, label };
    });
}

// Obtiene opciones de curso-materia activos; cruza con caches de cursos y materias
async function getCursoMateriasOptions() {
    if (!_cursoMateriasCache) {
        try { _cursoMateriasCache = await Api.get('/curso-materias'); } catch { _cursoMateriasCache = []; }
    }
    await getCursosOptions();
    await getMateriasOptions();
    return _cursoMateriasCache.filter(cm => cm.activo !== false).map(cm => {
        const curso = _cursosCache?.find(c => c.idCurso === cm.idCurso);
        const materia = _materiasCache?.find(m => m.idMateria === cm.idMateria);
        return { value: cm.idCursoMateria, label: `${curso?.nombre || '#' + cm.idCurso} - ${materia?.nombre || '#' + cm.idMateria}` };
    });
}

// --- Configuracion CRUD ---
const AsignacionesPage = {
    async render() {
        const [profOpts, cmOpts] = await Promise.all([getProfesoresOptions(), getCursoMateriasOptions()]);
        const page = createCrudPage({
            title: 'Cargos por Profesor',
            icon: 'bi-diagram-3',
            endpoint: '/asignaciones',
            noEdit: true,
            // Validaciones previas: no permite crear si no hay profesores o curso-materias
            requiredDeps: [
                {
                    check: async () => (await getProfesoresOptions()).length > 0,
                    message: 'No es posible crear un cargo, primero registra al menos un profesor'
                },
                {
                    check: async () => (await getCursoMateriasOptions()).length > 0,
                    message: 'No es posible crear un cargo, primero asigna al menos una materia a un curso'
                }
            ],
            columns: [
                { label: 'ID', idKey: 'idAsignacion' },
                { label: 'Profesor' },
                { label: 'Curso' },
                { label: 'Materia a cargo' },
                { label: 'Estado', width: 'col-sm' }
            ],
            // Filtros de busqueda por nombre de profesor, curso o materia
            searchFields: [
                { key: 'profesor', label: 'Profesor', getValue: (a) => {
                    const prof = profOpts.find(o => o.value === a.idProfesor);
                    return prof?.label || '';
                }},
                { key: 'curso', label: 'Curso', getValue: (a) => {
                    const cm = _cursoMateriasCache?.find(c => c.idCursoMateria === a.idCursoMateria);
                    const curso = cm ? _cursosCache?.find(c => c.idCurso === cm.idCurso) : null;
                    return curso?.nombre || '';
                }},
                { key: 'materia', label: 'Materia', getValue: (a) => {
                    const cm = _cursoMateriasCache?.find(c => c.idCursoMateria === a.idCursoMateria);
                    const materia = cm ? _materiasCache?.find(m => m.idMateria === cm.idMateria) : null;
                    return materia?.nombre || '';
                }}
            ],
            formFields: [
                { key: 'idProfesor', label: 'Profesor', type: 'select', options: profOpts },
                { key: 'idCursoMateria', label: 'Curso-Materia', type: 'select', options: cmOpts }
            ],
            formToDto: (d) => ({
                idProfesor: d.idProfesor ? parseInt(d.idProfesor) : null,
                idCursoMateria: d.idCursoMateria ? parseInt(d.idCursoMateria) : null
            }),
            // Resuelve labels de profesor, curso y materia para la tabla
            mapRow: (a) => {
                const prof = profOpts.find(o => o.value === a.idProfesor);
                const cm = _cursoMateriasCache?.find(c => c.idCursoMateria === a.idCursoMateria);
                const curso = cm ? _cursosCache?.find(c => c.idCurso === cm.idCurso) : null;
                const materia = cm ? _materiasCache?.find(m => m.idMateria === cm.idMateria) : null;
                return [
                    a.idAsignacion,
                    prof ? prof.label : `#${a.idProfesor}`,
                    curso ? curso.nombre : `#${cm?.idCurso || '?'}`,
                    materia ? materia.nombre : `#${cm?.idMateria || '?'}`,
                    UI.activoBadge(a.activo)
                ];
            }
        });
        page.render();
    }
};

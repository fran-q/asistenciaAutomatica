// Pagina: CRUD de relaciones curso-materia

// --- Caches globales para dropdowns (reutilizadas por otros modulos) ---
let _cursosCache = null;
let _materiasCache = null;

// Obtiene opciones de cursos para selects; cachea tras la primera carga
async function getCursosOptions() {
    if (!_cursosCache) {
        try { _cursosCache = await Api.get('/cursos'); } catch { _cursosCache = []; }
    }
    return _cursosCache.map(c => ({ value: c.idCurso, label: c.nombre }));
}

// Obtiene opciones de materias para selects; cachea tras la primera carga
async function getMateriasOptions() {
    if (!_materiasCache) {
        try { _materiasCache = await Api.get('/materias'); } catch { _materiasCache = []; }
    }
    return _materiasCache.map(m => ({ value: m.idMateria, label: m.nombre }));
}

// --- Configuracion CRUD ---
const CursoMateriasPage = {
    async render() {
        // Carga ambos dropdowns en paralelo
        const [cursoOpts, materiaOpts] = await Promise.all([getCursosOptions(), getMateriasOptions()]);
        const page = createCrudPage({
            title: 'Curso-Materia',
            icon: 'bi-link-45deg',
            endpoint: '/curso-materias',
            noEdit: true, // Solo alta y baja logica, sin edicion
            columns: [
                { label: 'ID', idKey: 'idCursoMateria' },
                { label: 'Curso' },
                { label: 'Materia' },
                { label: 'Estado' }
            ],
            formFields: [
                { key: 'idCurso', label: 'Curso', type: 'select', options: cursoOpts },
                { key: 'idMateria', label: 'Materia', type: 'select', options: materiaOpts }
            ],
            // Convierte valores del formulario a DTO numerico
            formToDto: (d) => ({
                idCurso: d.idCurso ? parseInt(d.idCurso) : null,
                idMateria: d.idMateria ? parseInt(d.idMateria) : null
            }),
            // Resuelve nombres de curso y materia desde cache para mostrar en tabla
            mapRow: (cm) => {
                const curso = _cursosCache?.find(c => c.idCurso === cm.idCurso);
                const materia = _materiasCache?.find(m => m.idMateria === cm.idMateria);
                return [
                    cm.idCursoMateria,
                    curso ? curso.nombre : `#${cm.idCurso}`,
                    materia ? materia.nombre : `#${cm.idMateria}`,
                    UI.activoBadge(cm.activo)
                ];
            },
            // Busqueda por nombre de curso o materia
            searchFilter: (cm, s) => {
                const curso = _cursosCache?.find(c => c.idCurso === cm.idCurso);
                const materia = _materiasCache?.find(m => m.idMateria === cm.idMateria);
                return `${curso?.nombre} ${materia?.nombre}`.toLowerCase().includes(s);
            }
        });
        page.render();
    }
};

// Pagina: CRUD de carreras academicas

// --- Cache de instituciones (dropdown en otros modulos) ---
let _institucionesCache = null;
async function getInstitucionesOptions() {
    if (!_institucionesCache) {
        try {
            _institucionesCache = await Api.get('/instituciones');
        } catch { _institucionesCache = []; }
    }
    return _institucionesCache.filter(i => i.activo !== false).map(i => ({ value: i.idInstitucion, label: i.nombre }));
}

// --- Cache de carreras (usado por materias.js, cursos.js y otros) ---
let _carrerasCache = null;
async function getCarrerasOptions() {
    if (!_carrerasCache) {
        try {
            _carrerasCache = await Api.get('/carreras');
        } catch { _carrerasCache = []; }
    }
    return _carrerasCache.filter(c => c.activo !== false).map(c => ({ value: c.idCarrera, label: c.nombre }));
}

// --- Configuracion CRUD de carreras ---
const CarrerasPage = {
    async render() {
        const page = createCrudPage({
            title: 'Carreras',
            icon: 'bi-journal-bookmark',
            endpoint: '/carreras',
            columns: [
                { label: 'ID', idKey: 'idCarrera' },
                { label: 'Nombre' },
                { label: 'Titulo' },
                { label: 'Duracion', width: 'col-sm' },
                { label: 'Estado', width: 'col-sm' }
            ],
            searchFields: [
                { key: 'nombre', label: 'Nombre', getValue: (c) => c.nombre },
                { key: 'titulo', label: 'Titulo', getValue: (c) => c.titulo }
            ],
            formFields: [
                { key: 'nombre', label: 'Nombre', placeholder: 'Nombre de la carrera', maxlength: 100 },
                { key: 'titulo', label: 'Titulo que otorga', placeholder: 'Ej: Tecnico Superior en Informatica', maxlength: 255 },
                { key: 'descripcion', label: 'Descripcion', type: 'textarea', maxlength: 1000 },
                { key: 'duracionAnios', label: 'Duracion (anios)', type: 'number', placeholder: '4' }
            ],
            formToDto: (d) => ({
                nombre: d.nombre,
                titulo: d.titulo,
                descripcion: d.descripcion || null,
                duracionAnios: d.duracionAnios ? parseInt(d.duracionAnios) : null
            }),
            mapRow: (c) => [
                c.idCarrera,
                c.nombre,
                c.titulo || '-',
                c.duracionAnios ? `${c.duracionAnios} anios` : '-',
                UI.activoBadge(c.activo)
            ]
        });
        page.render();
    }
};

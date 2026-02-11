// Cache for institutions
let _institucionesCache = null;
async function getInstitucionesOptions() {
    if (!_institucionesCache) {
        try {
            _institucionesCache = await Api.get('/instituciones');
        } catch { _institucionesCache = []; }
    }
    return _institucionesCache.map(i => ({ value: i.idInstitucion, label: i.nombre }));
}

const CarrerasPage = {
    async render() {
        const opts = await getInstitucionesOptions();
        const page = createCrudPage({
            title: 'Carreras',
            icon: 'bi-journal-bookmark',
            endpoint: '/carreras',
            columns: [
                { label: 'ID', idKey: 'idCarrera' },
                { label: 'Nombre' },
                { label: 'Titulo' },
                { label: 'Duracion' },
                { label: 'Institucion' },
                { label: 'Estado' }
            ],
            formFields: [
                { key: 'nombre', label: 'Nombre', placeholder: 'Nombre de la carrera' },
                { key: 'titulo', label: 'Titulo que otorga', placeholder: 'Ej: Licenciado en Informatica' },
                { key: 'descripcion', label: 'Descripcion', type: 'textarea' },
                { type: 'row-start' },
                { key: 'duracionAnios', label: 'Duracion (anios)', type: 'number', placeholder: '4' },
                { key: 'idInstitucion', label: 'Institucion', type: 'select', options: opts },
                { type: 'row-end' }
            ],
            formToDto: (d) => ({
                nombre: d.nombre,
                titulo: d.titulo,
                descripcion: d.descripcion || null,
                duracionAnios: d.duracionAnios ? parseInt(d.duracionAnios) : null,
                idInstitucion: d.idInstitucion ? parseInt(d.idInstitucion) : null
            }),
            mapRow: (c) => {
                const inst = _institucionesCache?.find(i => i.idInstitucion === c.idInstitucion);
                return [
                    c.idCarrera,
                    c.nombre,
                    c.titulo || '-',
                    c.duracionAnios ? `${c.duracionAnios} anios` : '-',
                    inst ? inst.nombre : `#${c.idInstitucion}`,
                    UI.activoBadge(c.activo)
                ];
            },
            searchFilter: (c, s) => `${c.nombre} ${c.titulo || ''}`.toLowerCase().includes(s)
        });
        page.render();
    }
};

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
                { label: 'Duracion' },
                { label: 'Institucion' },
                { label: 'Estado' }
            ],
            formFields: [
                { key: 'nombre', label: 'Nombre', placeholder: 'Nombre de la carrera' },
                { type: 'row-start' },
                { key: 'duracionAnios', label: 'Duracion (anios)', type: 'number', placeholder: '4' },
                { key: 'idInstitucion', label: 'Institucion', type: 'select', options: opts },
                { type: 'row-end' }
            ],
            formToDto: (d) => ({
                nombre: d.nombre,
                duracionAnios: d.duracionAnios ? parseInt(d.duracionAnios) : null,
                idInstitucion: d.idInstitucion ? parseInt(d.idInstitucion) : null
            }),
            mapRow: (c) => {
                const inst = _institucionesCache?.find(i => i.idInstitucion === c.idInstitucion);
                return [
                    c.idCarrera,
                    c.nombre,
                    c.duracionAnios ? `${c.duracionAnios} anios` : '-',
                    inst ? inst.nombre : `#${c.idInstitucion}`,
                    UI.activoBadge(c.activo)
                ];
            },
            searchFilter: (c, s) => c.nombre.toLowerCase().includes(s)
        });
        page.render();
    }
};

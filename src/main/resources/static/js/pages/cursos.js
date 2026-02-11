let _carrerasCache = null;
async function getCarrerasOptions() {
    if (!_carrerasCache) {
        try { _carrerasCache = await Api.get('/carreras'); } catch { _carrerasCache = []; }
    }
    return _carrerasCache.map(c => ({ value: c.idCarrera, label: c.nombre }));
}

const CursosPage = {
    async render() {
        const carrOpts = await getCarrerasOptions();
        const page = createCrudPage({
            title: 'Cursos',
            icon: 'bi-collection',
            endpoint: '/cursos',
            columns: [
                { label: 'ID', idKey: 'idCurso' },
                { label: 'Nombre' },
                { label: 'Anio' },
                { label: 'Comision' },
                { label: 'Turno' },
                { label: 'Carrera' },
                { label: 'Anio Lectivo' },
                { label: 'Estado' }
            ],
            formFields: [
                { key: 'nombre', label: 'Nombre', placeholder: 'Ej: 1A, 2B' },
                { type: 'row-start' },
                { key: 'anioCarrera', label: 'Anio Carrera (1-10)', type: 'number', placeholder: '1' },
                { key: 'comision', label: 'Comision', placeholder: 'Ej: A, B, C' },
                { type: 'row-end' },
                { type: 'row-start' },
                { key: 'turno', label: 'Turno', type: 'enum', enumName: 'turno' },
                { key: 'anioLectivo', label: 'Anio Lectivo', type: 'number', placeholder: '2025' },
                { type: 'row-end' },
                { key: 'idCarrera', label: 'Carrera', type: 'select', options: carrOpts }
            ],
            formToDto: (d) => ({
                nombre: d.nombre,
                anioCarrera: d.anioCarrera ? parseInt(d.anioCarrera) : null,
                comision: d.comision,
                turno: d.turno,
                idCarrera: d.idCarrera ? parseInt(d.idCarrera) : null,
                anioLectivo: d.anioLectivo ? parseInt(d.anioLectivo) : null
            }),
            mapRow: (c) => {
                const carr = _carrerasCache?.find(ca => ca.idCarrera === c.idCarrera);
                return [
                    c.idCurso,
                    c.nombre,
                    c.anioCarrera || '-',
                    c.comision || '-',
                    c.turno ? `<span class="badge badge-info">${c.turno}</span>` : '-',
                    carr ? carr.nombre : `#${c.idCarrera}`,
                    c.anioLectivo || '-',
                    UI.activoBadge(c.activo)
                ];
            },
            searchFilter: (c, s) => `${c.nombre} ${c.comision}`.toLowerCase().includes(s)
        });
        page.render();
    }
};

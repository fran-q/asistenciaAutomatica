const MateriasPage = {
    async render() {
        const carrOpts = await getCarrerasOptions();
        const page = createCrudPage({
            title: 'Materias',
            icon: 'bi-book',
            endpoint: '/materias',
            columns: [
                { label: 'ID', idKey: 'idMateria' },
                { label: 'Nombre' },
                { label: 'Hs. Semanales' },
                { label: 'Carrera' },
                { label: 'Estado' }
            ],
            formFields: [
                { key: 'nombre', label: 'Nombre', placeholder: 'Nombre de la materia' },
                { key: 'descripcion', label: 'Descripcion', type: 'textarea' },
                { type: 'row-start' },
                { key: 'horasSemanales', label: 'Horas Semanales', type: 'number', placeholder: '4' },
                { key: 'idCarrera', label: 'Carrera', type: 'select', options: carrOpts },
                { type: 'row-end' }
            ],
            formToDto: (d) => ({
                nombre: d.nombre,
                descripcion: d.descripcion,
                horasSemanales: d.horasSemanales ? parseInt(d.horasSemanales) : null,
                idCarrera: d.idCarrera ? parseInt(d.idCarrera) : null
            }),
            mapRow: (m) => {
                const carr = _carrerasCache?.find(c => c.idCarrera === m.idCarrera);
                return [
                    m.idMateria,
                    m.nombre,
                    m.horasSemanales || '-',
                    carr ? carr.nombre : `#${m.idCarrera}`,
                    UI.activoBadge(m.activo)
                ];
            },
            searchFilter: (m, s) => m.nombre.toLowerCase().includes(s)
        });
        page.render();
    }
};

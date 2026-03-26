// Pagina: CRUD de materias (asignaturas)
const MateriasPage = {
    async render() {
        // Carga opciones de carreras para el dropdown del formulario
        const carrOpts = await getCarrerasOptions();
        const page = createCrudPage({
            title: 'Materias',
            icon: 'bi-book',
            endpoint: '/materias',
            // Requiere al menos una carrera para poder crear materias
            requiredDeps: [{
                check: async () => (await getCarrerasOptions()).length > 0,
                message: 'No es posible crear una materia, primero registra al menos una carrera'
            }],
            columns: [
                { label: 'ID', idKey: 'idMateria' },
                { label: 'Nombre' },
                { label: 'Hs. Semanales', width: 'col-md' },
                { label: 'Carrera' },
                { label: 'Estado', width: 'col-sm' }
            ],
            searchFields: [
                { key: 'nombre', label: 'Nombre', getValue: (m) => m.nombre },
                { key: 'carrera', label: 'Carrera', getValue: (m) => {
                    const carr = _carrerasCache?.find(c => c.idCarrera === m.idCarrera);
                    return carr?.nombre || '';
                }},
                { key: 'horasSemanales', label: 'Hs. Semanales', getValue: (m) => m.horasSemanales ? String(m.horasSemanales) : '' }
            ],
            formFields: [
                { key: 'nombre', label: 'Nombre', placeholder: 'Nombre de la materia', maxlength: 100 },
                { key: 'descripcion', label: 'Descripcion', type: 'textarea', maxlength: 1000 },
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
            // Resuelve nombre de carrera desde el cache
            mapRow: (m) => {
                const carr = _carrerasCache?.find(c => c.idCarrera === m.idCarrera);
                return [
                    m.idMateria,
                    m.nombre,
                    m.horasSemanales || '-',
                    carr ? carr.nombre : `#${m.idCarrera}`,
                    UI.activoBadge(m.activo)
                ];
            }
        });
        page.render();
    }
};

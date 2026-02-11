const InstitucionesPage = createCrudPage({
    title: 'Instituciones',
    icon: 'bi-building',
    endpoint: '/instituciones',
    columns: [
        { label: 'ID', idKey: 'idInstitucion' },
        { label: 'Nombre' },
        { label: 'Direccion' },
        { label: 'Telefono' },
        { label: 'Estado' }
    ],
    formFields: [
        { key: 'nombre', label: 'Nombre', placeholder: 'Nombre de la institucion' },
        { key: 'direccion', label: 'Direccion', placeholder: 'Direccion' },
        { type: 'row-start' },
        { key: 'telefono', label: 'Telefono', placeholder: 'Telefono' },
        { key: 'email', label: 'Email', type: 'email', placeholder: 'email@institucion.com' },
        { type: 'row-end' }
    ],
    formToDto: (d) => ({
        nombre: d.nombre,
        direccion: d.direccion,
        telefono: d.telefono,
        email: d.email
    }),
    mapRow: (i) => [
        i.idInstitucion,
        i.nombre,
        i.direccion || '-',
        i.telefono || '-',
        UI.activoBadge(i.activo)
    ],
    searchFilter: (i, s) =>
        `${i.nombre} ${i.direccion}`.toLowerCase().includes(s)
});

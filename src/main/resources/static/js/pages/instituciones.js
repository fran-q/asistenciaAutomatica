// Pagina: Institucion del administrador (solo su propia institucion)
// No permite crear ni eliminar, solo ver y editar
const InstitucionesPage = createCrudPage({
    title: 'Mi Institucion',
    icon: 'bi-building',
    endpoint: '/instituciones',
    noCreate: true,
    noDelete: true,
    columns: [
        { label: 'ID', idKey: 'idInstitucion' },
        { label: 'Nombre' },
        { label: 'Direccion' },
        { label: 'Telefono', width: 'col-md' },
        { label: 'Email', width: 'col-md' }
    ],
    searchFields: [
        { key: 'nombre', label: 'Nombre', getValue: (i) => i.nombre },
        { key: 'direccion', label: 'Direccion', getValue: (i) => i.direccion },
        { key: 'telefono', label: 'Telefono', getValue: (i) => i.telefono },
        { key: 'email', label: 'Email', getValue: (i) => i.email }
    ],
    formFields: [
        { key: 'nombre', label: 'Nombre', placeholder: 'Nombre de la institucion', maxlength: 255 },
        { key: 'direccion', label: 'Direccion', placeholder: 'Direccion', maxlength: 255 },
        { type: 'row-start' },
        { key: 'telefono', label: 'Telefono', placeholder: 'Telefono', maxlength: 10 },
        { key: 'email', label: 'Email', type: 'email', placeholder: 'email@institucion.com', maxlength: 254 },
        { type: 'row-end' }
    ],
    // Convierte campos del formulario al DTO esperado por el backend
    formToDto: (d) => ({
        nombre: d.nombre,
        direccion: d.direccion,
        telefono: d.telefono,
        email: d.email
    }),
    // Mapea cada registro a las columnas de la tabla
    mapRow: (i) => [
        i.idInstitucion,
        i.nombre,
        i.direccion || '-',
        i.telefono || '-',
        i.email || '-'
    ]
});

const UsuariosPage = createCrudPage({
    title: 'Usuarios',
    icon: 'bi-people',
    endpoint: '/usuarios',
    columns: [
        { label: 'ID', idKey: 'idUsuario' },
        { label: 'Nombre' },
        { label: 'Email' },
        { label: 'Documento' },
        { label: 'Rol' },
        { label: 'Estado' }
    ],
    formFields: [
        { type: 'row-start' },
        { key: 'nombre', label: 'Nombre', placeholder: 'Nombre' },
        { key: 'apellido', label: 'Apellido', placeholder: 'Apellido' },
        { type: 'row-end' },
        { type: 'row-start' },
        { key: 'email', label: 'Email', type: 'email', placeholder: 'email@ejemplo.com' },
        { key: 'contrasena', label: 'Contrasena', type: 'password', placeholder: '******' },
        { type: 'row-end' },
        { type: 'row-start' },
        { key: 'tipoDocumento', label: 'Tipo Documento', type: 'enum', enumName: 'tipoDocumento' },
        { key: 'numeroDocumento', label: 'Nro. Documento', placeholder: '12345678' },
        { type: 'row-end' },
        { type: 'row-start' },
        { key: 'telefono', label: 'Telefono', placeholder: 'Telefono' },
        { key: 'genero', label: 'Genero', type: 'enum', enumName: 'genero' },
        { type: 'row-end' },
        { key: 'direccion', label: 'Direccion', placeholder: 'Direccion' },
        { key: 'rol', label: 'Rol', type: 'enum', enumName: 'rol' }
    ],
    formToDto: (d) => ({
        nombre: d.nombre,
        apellido: d.apellido,
        email: d.email,
        contrasena: d.contrasena || undefined,
        tipoDocumento: d.tipoDocumento,
        numeroDocumento: d.numeroDocumento,
        telefono: d.telefono,
        direccion: d.direccion,
        genero: d.genero,
        rol: d.rol
    }),
    mapRow: (u) => [
        u.idUsuario,
        `${u.nombre} ${u.apellido}`,
        u.email,
        u.numeroDocumento ? `${u.tipoDocumento} ${u.numeroDocumento}` : '-',
        UI.rolBadge(u.rol),
        UI.activoBadge(u.activo)
    ],
    searchFilter: (u, s) =>
        `${u.nombre} ${u.apellido} ${u.email} ${u.numeroDocumento}`.toLowerCase().includes(s)
});

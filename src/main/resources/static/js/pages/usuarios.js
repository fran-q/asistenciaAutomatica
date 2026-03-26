// Pagina: Listado general de usuarios del sistema
const UsuariosPage = {
    async render() {
        const page = createCrudPage({
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
            // Formulario con campos agrupados en filas de 2
            formFields: [
                { type: 'row-start' },
                { key: 'nombre', label: 'Nombre', placeholder: 'Nombre', maxlength: 50 },
                { key: 'apellido', label: 'Apellido', placeholder: 'Apellido', maxlength: 64 },
                { type: 'row-end' },
                { type: 'row-start' },
                { key: 'email', label: 'Email', type: 'email', placeholder: 'email@ejemplo.com', maxlength: 254 },
                { key: 'contrasena', label: 'Contrasena', type: 'password', placeholder: '******', maxlength: 64 },
                { type: 'row-end' },
                { type: 'row-start' },
                { key: 'tipoDocumento', label: 'Tipo Documento', type: 'enum', enumName: 'tipoDocumento' },
                { key: 'numeroDocumento', label: 'Nro. Documento', placeholder: '12345678', maxlength: 20 },
                { type: 'row-end' },
                { type: 'row-start' },
                { key: 'genero', label: 'Genero', type: 'enum', enumName: 'genero' },
                { key: 'rol', label: 'Rol', type: 'enum', enumName: 'rol' },
                { type: 'row-end' },
                { key: 'telefono', label: 'Telefono', placeholder: 'Telefono', maxlength: 10 },
                { key: 'direccion', label: 'Direccion', placeholder: 'Direccion', maxlength: 255 }
            ],
            // Contrasena se omite del DTO si queda vacia (no la sobreescribe al editar)
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
            // Muestra nombre completo concatenado y badges de rol y estado
            mapRow: (u) => [
                u.idUsuario,
                `${u.nombre} ${u.apellido}`,
                u.email,
                u.numeroDocumento ? `${u.tipoDocumento} ${u.numeroDocumento}` : '-',
                UI.rolBadge(u.rol),
                UI.activoBadge(u.activo)
            ],
            // Busqueda por nombre, email o documento
            searchFilter: (u, s) =>
                `${u.nombre} ${u.apellido} ${u.email} ${u.numeroDocumento}`.toLowerCase().includes(s)
        });
        page.render();
    }
};

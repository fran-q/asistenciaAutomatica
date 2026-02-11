// Alumnos are profiles linked to a Usuario (idUsuario, legajo, promedio)
// Response: idAlumno, idUsuario, legajo, promedio, fechaCreacion, activo

const AlumnosPage = {
    async render() {
        const usuarioOpts = await getUsuariosOptions();
        const page = createCrudPage({
            title: 'Alumnos',
            icon: 'bi-mortarboard',
            endpoint: '/alumnos',
            columns: [
                { label: 'ID', idKey: 'idAlumno' },
                { label: 'Usuario' },
                { label: 'Legajo' },
                { label: 'Promedio' },
                { label: 'Estado' }
            ],
            formFields: [
                { key: 'idUsuario', label: 'Usuario', type: 'select', options: usuarioOpts },
                { type: 'row-start' },
                { key: 'legajo', label: 'Legajo', placeholder: 'Nro. legajo' },
                { key: 'promedio', label: 'Promedio (0-10)', type: 'number', placeholder: '0.0' },
                { type: 'row-end' }
            ],
            formToDto: (d) => ({
                idUsuario: d.idUsuario ? parseInt(d.idUsuario) : null,
                legajo: d.legajo,
                promedio: d.promedio ? parseFloat(d.promedio) : null
            }),
            mapRow: (a) => {
                const user = _usuariosCache?.find(u => u.idUsuario === a.idUsuario);
                return [
                    a.idAlumno,
                    user ? `${user.nombre} ${user.apellido}` : `Usuario #${a.idUsuario}`,
                    a.legajo || '-',
                    a.promedio != null ? Number(a.promedio).toFixed(1) : '-',
                    UI.activoBadge(a.activo)
                ];
            },
            searchFilter: (a, s) => {
                const user = _usuariosCache?.find(u => u.idUsuario === a.idUsuario);
                return `${user?.nombre} ${user?.apellido} ${a.legajo}`.toLowerCase().includes(s);
            }
        });
        page.render();
    }
};

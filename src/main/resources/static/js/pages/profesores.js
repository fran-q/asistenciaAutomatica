// Profesores are profiles linked to a Usuario (idUsuario, legajo, titulo, categoria)
// Response: idProfesor, idUsuario, legajo, titulo, categoria, fechaCreacion, activo

let _usuariosCache = null;
async function getUsuariosOptions() {
    if (!_usuariosCache) {
        try { _usuariosCache = await Api.get('/usuarios'); } catch { _usuariosCache = []; }
    }
    return _usuariosCache.map(u => ({ value: u.idUsuario, label: `${u.nombre} ${u.apellido} (${u.email})` }));
}

const ProfesoresPage = {
    async render() {
        const usuarioOpts = await getUsuariosOptions();
        const categorias = [
            { value: 'TITULAR', label: 'Titular' },
            { value: 'ADJUNTO', label: 'Adjunto' },
            { value: 'SUPLENTE', label: 'Suplente' },
            { value: 'INTERINO', label: 'Interino' }
        ];
        const page = createCrudPage({
            title: 'Profesores',
            icon: 'bi-person-badge',
            endpoint: '/profesores',
            columns: [
                { label: 'ID', idKey: 'idProfesor' },
                { label: 'Usuario' },
                { label: 'Legajo' },
                { label: 'Titulo' },
                { label: 'Categoria' },
                { label: 'Estado' }
            ],
            formFields: [
                { key: 'idUsuario', label: 'Usuario', type: 'select', options: usuarioOpts },
                { type: 'row-start' },
                { key: 'legajo', label: 'Legajo', placeholder: 'Nro. legajo' },
                { key: 'categoria', label: 'Categoria', type: 'select', options: categorias },
                { type: 'row-end' },
                { key: 'titulo', label: 'Titulo', placeholder: 'Ej: Licenciado en Informatica' }
            ],
            formToDto: (d) => ({
                idUsuario: d.idUsuario ? parseInt(d.idUsuario) : null,
                legajo: d.legajo,
                titulo: d.titulo,
                categoria: d.categoria
            }),
            mapRow: (p) => {
                const user = _usuariosCache?.find(u => u.idUsuario === p.idUsuario);
                return [
                    p.idProfesor,
                    user ? `${user.nombre} ${user.apellido}` : `Usuario #${p.idUsuario}`,
                    p.legajo || '-',
                    p.titulo || '-',
                    p.categoria ? `<span class="badge badge-accent">${p.categoria}</span>` : '-',
                    UI.activoBadge(p.activo)
                ];
            },
            searchFilter: (p, s) => {
                const user = _usuariosCache?.find(u => u.idUsuario === p.idUsuario);
                return `${user?.nombre} ${user?.apellido} ${p.legajo} ${p.titulo}`.toLowerCase().includes(s);
            }
        });
        page.render();
    }
};

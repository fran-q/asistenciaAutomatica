// Pagina: Gestion de profesores (crea Usuario + perfil UsuarioProfesor)
// Creacion dual: POST /usuarios -> obtiene idUsuario -> POST /profesores
// Edicion dual: PUT /usuarios/{id} + PUT /profesores/{id}
// Eliminacion: DELETE /profesores/{id} (soft delete en ambos)

// --- Cache de usuarios (compartido con alumnos.js y administradores.js) ---
let _usuariosCache = null;
async function getUsuariosOptions() {
    if (!_usuariosCache) {
        try { _usuariosCache = await Api.get('/usuarios'); } catch { _usuariosCache = []; }
    }
    return _usuariosCache.filter(u => u.activo !== false).map(u => ({ value: u.idUsuario, label: `${u.nombre} ${u.apellido} (${u.email})` }));
}

const ProfesoresPage = {
    allData: [],
    _currentFilter: 0,
    _showInactivos: false,
    // Busqueda: resuelve datos del usuario vinculado via _usuariosCache
    _searchFields: [
        { label: 'Nombre', getValue: (p) => { const u = _usuariosCache?.find(u => u.idUsuario === p.idUsuario); return u ? `${u.nombre} ${u.apellido}` : ''; }},
        { label: 'Email', getValue: (p) => { const u = _usuariosCache?.find(u => u.idUsuario === p.idUsuario); return u?.email || ''; }},
        { label: 'Documento', getValue: (p) => { const u = _usuariosCache?.find(u => u.idUsuario === p.idUsuario); return u?.numeroDocumento || ''; }},
        { label: 'Legajo', getValue: (p) => p.legajo || '' },
        { label: 'Categoria', getValue: (p) => p.categoria || '' },
        { label: 'Titulo', getValue: (p) => p.titulo || '' }
    ],

    async render() {
        await getUsuariosOptions();

        const filterItems = this._searchFields.map((f, i) =>
            `<button data-filter="${i}" class="${i === 0 ? 'active' : ''}">${f.label}</button>`
        ).join('');

        const content = document.getElementById('page-content');
        content.innerHTML = `
            <div class="page-header">
                <h2 class="page-title"><i class="bi bi-person-badge"></i> Profesores</h2>
                <button class="btn btn-primary" id="btn-new-prof"><i class="bi bi-plus-lg"></i> Nuevo</button>
            </div>
            <div class="table-wrapper">
                <div class="table-toolbar">
                    <div class="search-group">
                        <div class="table-search">
                            <i class="bi bi-search"></i>
                            <input type="text" id="prof-search" placeholder="Buscar por nombre...">
                        </div>
                        <div class="filter-dropdown">
                            <button class="filter-dropdown-btn" id="filter-btn">
                                <i class="bi bi-funnel"></i> <span id="filter-label">Nombre</span> <i class="bi bi-chevron-down" style="font-size:0.65rem;"></i>
                            </button>
                            <div class="filter-dropdown-menu" id="filter-menu">
                                ${filterItems}
                                <div class="filter-divider"></div>
                                <button data-filter="inactivos">Inactivos</button>
                            </div>
                        </div>
                    </div>
                    <span id="prof-count"></span>
                </div>
                <div class="table-scroll-body">
                    <table>
                        <thead>
                            <tr>
                                <th>Nombre</th>
                                <th>Email</th>
                                <th>Documento</th>
                                <th class="col-sm">Legajo</th>
                                <th class="col-md">Categoria</th>
                                <th class="col-sm">Estado</th>
                                <th class="col-actions">Acciones</th>
                            </tr>
                        </thead>
                        <tbody id="prof-table"></tbody>
                    </table>
                </div>
            </div>`;

        document.getElementById('btn-new-prof').addEventListener('click', () => this.openForm(null));
        document.getElementById('prof-search').addEventListener('input', (e) => this.renderTable(e.target.value));

        // Filter dropdown
        const filterBtn = document.getElementById('filter-btn');
        const filterMenu = document.getElementById('filter-menu');
        filterBtn.addEventListener('click', (e) => { e.stopPropagation(); filterMenu.classList.toggle('open'); });
        document.addEventListener('click', () => filterMenu.classList.remove('open'));
        filterMenu.querySelectorAll('button').forEach(btn => {
            btn.addEventListener('click', () => {
                const val = btn.dataset.filter;
                if (val === 'inactivos') {
                    this._showInactivos = !this._showInactivos;
                    btn.classList.toggle('active', this._showInactivos);
                } else {
                    this._currentFilter = parseInt(val);
                    // Reset inactivos toggle when selecting a normal filter
                    this._showInactivos = false;
                    filterMenu.querySelector('[data-filter="inactivos"]')?.classList.remove('active');
                    filterMenu.querySelectorAll('button:not([data-filter="inactivos"])').forEach(b => b.classList.remove('active'));
                    btn.classList.add('active');
                    const label = this._searchFields[this._currentFilter].label;
                    document.getElementById('filter-label').textContent = label;
                    document.getElementById('prof-search').placeholder = `Buscar por ${label.toLowerCase()}...`;
                }
                filterMenu.classList.remove('open');
                this.renderTable(document.getElementById('prof-search').value);
            });
        });

        this.loadData();
    },

    // Recarga profesores y refresca cache de usuarios
    async loadData() {
        try {
            this.allData = await Api.get('/profesores');
            _usuariosCache = null;  // Invalida cache para obtener datos actualizados
            await getUsuariosOptions();
            this.renderTable('');
        } catch (err) {
            UI.toast(err.mensaje || 'Error al cargar profesores', 'error');
        }
    },

    renderTable(search) {
        const tbody = document.getElementById('prof-table');
        let filtered = this.allData;

        // Activo/inactivo filter
        if (!this._showInactivos) {
            filtered = filtered.filter(p => p.activo !== false);
        } else {
            filtered = filtered.filter(p => p.activo === false);
        }

        // Search by selected field
        if (search) {
            const s = search.toLowerCase();
            const field = this._searchFields[this._currentFilter];
            filtered = filtered.filter(p => {
                const val = field.getValue(p);
                return val && val.toLowerCase().includes(s);
            });
        }
        document.getElementById('prof-count').textContent = `${filtered.length} registro(s)`;

        if (filtered.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="table-empty">No se encontraron registros</td></tr>';
            return;
        }

        tbody.innerHTML = filtered.map(p => {
            const user = _usuariosCache?.find(u => u.idUsuario === p.idUsuario);
            return `<tr>
                <td>${user ? `${user.nombre} ${user.apellido}` : '-'}</td>
                <td>${user?.email || '-'}</td>
                <td>${user?.numeroDocumento ? `${user.tipoDocumento} ${user.numeroDocumento}` : '-'}</td>
                <td>${p.legajo || '-'}</td>
                <td>${p.categoria ? `<span class="badge badge-accent">${p.categoria}</span>` : '-'}</td>
                <td>${UI.activoBadge(p.activo)}</td>
                <td class="table-actions">
                    ${p.activo !== false ? `<button class="btn-icon edit" data-id="${p.idProfesor}" data-uid="${p.idUsuario}"><i class="bi bi-pencil"></i></button>` : ''}
                    ${p.activo === false
                        ? `<button class="btn-icon reactivate" data-id="${p.idProfesor}"><i class="bi bi-arrow-counterclockwise"></i></button>`
                        : `<button class="btn-icon delete" data-id="${p.idProfesor}"><i class="bi bi-trash"></i></button>`
                    }
                </td>
            </tr>`;
        }).join('');

        tbody.querySelectorAll('.edit').forEach(btn =>
            btn.addEventListener('click', () => {
                const prof = this.allData.find(p => String(p.idProfesor) === btn.dataset.id);
                const user = _usuariosCache?.find(u => u.idUsuario === prof?.idUsuario);
                if (prof) this.openForm(prof, user);
            })
        );
        tbody.querySelectorAll('.delete').forEach(btn =>
            btn.addEventListener('click', () => {
                UI.confirm('Eliminar este profesor?', async () => {
                    try {
                        await Api.delete(`/profesores/${btn.dataset.id}`);
                        UI.toast('Profesor eliminado', 'success');
                        _usuariosCache = null;
                        this.loadData();
                    } catch (err) { UI.toast(err.mensaje || 'Error', 'error'); }
                });
            })
        );
        tbody.querySelectorAll('.reactivate').forEach(btn =>
            btn.addEventListener('click', () => {
                UI.confirm('Reactivar este profesor?', async () => {
                    try {
                        await Api.patch(`/profesores/${btn.dataset.id}/reactivar`);
                        UI.toast('Profesor reactivado', 'success');
                        _usuariosCache = null;
                        this.loadData();
                    } catch (err) { UI.toast(err.mensaje || 'Error', 'error'); }
                });
            })
        );
    },

    // --- Formulario modal: datos personales (Usuario) + datos academicos (Profesor) ---
    openForm(prof, user) {
        const isEdit = !!prof;
        const html = `
            <h6 style="color:var(--accent); margin-bottom:12px; font-size:0.8rem; text-transform:uppercase; letter-spacing:1px;">Datos personales</h6>
            <div class="form-row">
                <div class="form-group">
                    <label>Nombre *</label>
                    <input type="text" class="form-control" id="f-nombre" value="${user?.nombre || ''}" placeholder="Ej: Juan Carlos" minlength="2" maxlength="50">
                </div>
                <div class="form-group">
                    <label>Apellido *</label>
                    <input type="text" class="form-control" id="f-apellido" value="${user?.apellido || ''}" placeholder="Ej: Garcia Lopez" minlength="2" maxlength="64">
                </div>
            </div>
            <div class="form-group">
                <label>Email *</label>
                <input type="email" class="form-control" id="f-email" value="${user?.email || ''}" placeholder="ejemplo@correo.com" maxlength="254">
            </div>
            <div class="form-row">
                <div class="form-group">
                    <label>Contrasena ${isEdit ? '' : '*'}</label>
                    <div class="password-wrapper">
                        <input type="password" class="form-control" id="f-contrasena" placeholder="${isEdit ? 'Dejar vacio para no cambiar' : 'Min. 8 caracteres'}" minlength="8" maxlength="64">
                        <button type="button" class="password-toggle" tabindex="-1"><i class="bi bi-eye-slash"></i></button>
                    </div>
                    <small class="form-hint">Min. 8, 1 mayuscula, 1 minuscula, 1 numero, 1 especial (@$!%*?&#+-._ )</small>
                </div>
                <div class="form-group">
                    <label>Confirmar Contrasena ${isEdit ? '' : '*'}</label>
                    <div class="password-wrapper">
                        <input type="password" class="form-control" id="f-confirmarContrasena" placeholder="Repetir contrasena" maxlength="64">
                        <button type="button" class="password-toggle" tabindex="-1"><i class="bi bi-eye-slash"></i></button>
                    </div>
                </div>
            </div>
            <div class="form-row">
                <div class="form-group">
                    <label>Tipo Documento *</label>
                    <select class="form-control" id="f-tipoDocumento">
                        <option value="">-- Seleccionar --</option>
                        ${UI.selectOptions('tipoDocumento', user?.tipoDocumento || '')}
                    </select>
                </div>
                <div class="form-group">
                    <label>Nro. Documento *</label>
                    <input type="text" class="form-control" id="f-numeroDocumento" value="${user?.numeroDocumento || ''}" placeholder="12345678" maxlength="20">
                </div>
            </div>
            <div class="form-row">
                <div class="form-group">
                    <label>Genero *</label>
                    <select class="form-control" id="f-genero">
                        <option value="">-- Seleccionar --</option>
                        ${UI.selectOptions('genero', user?.genero || '')}
                    </select>
                </div>
                <div class="form-group">
                    <label>Telefono</label>
                    <input type="text" class="form-control" id="f-telefono" value="${user?.telefono || ''}" placeholder="Ej: 1134567890" maxlength="10">
                    <small class="form-hint">10 digitos sin 0 ni 15 (ej: 1134567890)</small>
                </div>
            </div>
            <div class="form-group">
                <label>Direccion</label>
                <input type="text" class="form-control" id="f-direccion" value="${user?.direccion || ''}" placeholder="Ej: Av. Corrientes 1234" maxlength="255">
            </div>
            <hr style="border-color:var(--border); margin:16px 0;">
            <h6 style="color:var(--accent); margin-bottom:12px; font-size:0.8rem; text-transform:uppercase; letter-spacing:1px;">Datos del profesor</h6>
            <div class="form-row">
                <div class="form-group">
                    <label>Legajo *</label>
                    <input type="text" class="form-control" id="f-legajo" value="${prof?.legajo || ''}" placeholder="Ej: 12345" minlength="4" maxlength="10">
                    <small class="form-hint">4-10 caracteres (ej: 12345, S12345, 59296/6)</small>
                </div>
                <div class="form-group">
                    <label>Categoria *</label>
                    <select class="form-control" id="f-categoria">
                        <option value="">-- Seleccionar --</option>
                        ${UI.selectOptions('categoriaProfesor', prof?.categoria || '')}
                    </select>
                </div>
            </div>
            <div class="form-group">
                <label>Titulo *</label>
                <input type="text" class="form-control" id="f-titulo" value="${prof?.titulo || ''}" placeholder="Ej: Licenciado en Informatica" minlength="3" maxlength="100">
            </div>`;

        UI.openModal(isEdit ? 'Editar Profesor' : 'Nuevo Profesor', html, async () => {
            // Validacion frontend de campos personales + academicos
            const errors = UI.validatePersonForm({
                nombre: 'f-nombre', apellido: 'f-apellido', email: 'f-email',
                contrasena: 'f-contrasena', confirmarContrasena: 'f-confirmarContrasena', requirePass: !isEdit,
                tipoDocumento: 'f-tipoDocumento', numeroDocumento: 'f-numeroDocumento',
                genero: 'f-genero', telefono: 'f-telefono'
            });
            if (!document.getElementById('f-legajo').value.trim())
                errors.push('El legajo es obligatorio');
            if (!document.getElementById('f-categoria').value)
                errors.push('La categoria es obligatoria');
            if (!document.getElementById('f-titulo').value.trim() || document.getElementById('f-titulo').value.trim().length < 3)
                errors.push('El titulo es obligatorio (min. 3 caracteres)');

            if (errors.length > 0) {
                UI.showValidationErrors(errors);
                return;
            }

            const usuarioDto = {
                nombre: document.getElementById('f-nombre').value.trim(),
                apellido: document.getElementById('f-apellido').value.trim(),
                email: document.getElementById('f-email').value.trim(),
                contrasena: document.getElementById('f-contrasena').value || undefined,
                tipoDocumento: document.getElementById('f-tipoDocumento').value,
                numeroDocumento: document.getElementById('f-numeroDocumento').value.trim(),
                genero: document.getElementById('f-genero').value,
                telefono: document.getElementById('f-telefono').value.trim() || null,
                direccion: document.getElementById('f-direccion').value.trim() || null,
                rol: 'PROFESOR'
            };

            const profesorDto = {
                legajo: document.getElementById('f-legajo').value.trim(),
                titulo: document.getElementById('f-titulo').value.trim(),
                categoria: document.getElementById('f-categoria').value
            };

            try {
                if (isEdit) {
                    // Edicion: actualiza usuario y luego perfil profesor
                    await Api.put(`/usuarios/${prof.idUsuario}`, usuarioDto);
                    profesorDto.idUsuario = prof.idUsuario;
                    await Api.put(`/profesores/${prof.idProfesor}`, profesorDto);
                    UI.toast('Profesor actualizado con exito', 'success');
                } else {
                    // Creacion: primero crea usuario, luego vincula perfil profesor
                    const nuevoUsuario = await Api.post('/usuarios', usuarioDto);
                    profesorDto.idUsuario = nuevoUsuario.idUsuario;
                    await Api.post('/profesores', profesorDto);
                    UI.toast('Profesor registrado con exito', 'success');
                }
                UI.closeModal();
                _usuariosCache = null;
                this.loadData();
            } catch (err) {
                const msg = err.detalles?.length ? err.detalles.join(', ') : err.mensaje || 'Error';
                UI.toast(msg, 'error');
            }
        });

        // Vincula validaciones dinamicas de documento y toggles de contrasena
        UI.bindDocTypeChange('f-tipoDocumento', 'f-numeroDocumento');
        UI.bindNumericOnly('f-numeroDocumento', 'f-tipoDocumento');
        UI.bindNumericOnly('f-telefono');
        UI.bindPasswordToggles();
    }
};

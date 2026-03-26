// Pagina: Gestion de administradores (usuarios con rol ADMIN)
// CRUD directo sobre /usuarios, filtrando solo rol=ADMIN

const AdministradoresPage = {
    allData: [],
    _currentFilter: 0,
    _showInactivos: false,
    _searchFields: [
        { label: 'Nombre', getValue: (u) => `${u.nombre} ${u.apellido}` },
        { label: 'Email', getValue: (u) => u.email || '' },
        { label: 'Documento', getValue: (u) => u.numeroDocumento || '' },
        { label: 'Telefono', getValue: (u) => u.telefono || '' }
    ],

    async render() {

        const filterItems = this._searchFields.map((f, i) =>
            `<button data-filter="${i}" class="${i === 0 ? 'active' : ''}">${f.label}</button>`
        ).join('');

        const content = document.getElementById('page-content');
        content.innerHTML = `
            <div class="page-header">
                <h2 class="page-title"><i class="bi bi-shield-lock"></i> Administradores</h2>
                <button class="btn btn-primary" id="btn-new-admin"><i class="bi bi-plus-lg"></i> Nuevo</button>
            </div>
            <div class="table-wrapper">
                <div class="table-toolbar">
                    <div class="search-group">
                        <div class="table-search">
                            <i class="bi bi-search"></i>
                            <input type="text" id="admin-search" placeholder="Buscar por nombre...">
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
                    <span id="admin-count"></span>
                </div>
                <div class="table-scroll-body">
                    <table>
                        <thead>
                            <tr>
                                <th>Nombre</th>
                                <th>Email</th>
                                <th>Documento</th>
                                <th class="col-md">Telefono</th>
                                <th class="col-sm">Estado</th>
                                <th class="col-actions">Acciones</th>
                            </tr>
                        </thead>
                        <tbody id="admin-table"></tbody>
                    </table>
                </div>
            </div>`;

        document.getElementById('btn-new-admin').addEventListener('click', () => this.openForm(null));
        document.getElementById('admin-search').addEventListener('input', (e) => this.renderTable(e.target.value));

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
                    document.getElementById('admin-search').placeholder = `Buscar por ${label.toLowerCase()}...`;
                }
                filterMenu.classList.remove('open');
                this.renderTable(document.getElementById('admin-search').value);
            });
        });

        this.loadData();
    },

    // Carga todos los usuarios y filtra solo los ADMIN
    async loadData() {
        try {
            const all = await Api.get('/usuarios');
            this.allData = all.filter(u => u.rol === 'ADMIN');
            this.renderTable('');
        } catch (err) {
            UI.toast(err.mensaje || 'Error al cargar administradores', 'error');
        }
    },

    renderTable(search) {
        const tbody = document.getElementById('admin-table');
        let filtered = this.allData;

        // Activo/inactivo filter
        if (!this._showInactivos) {
            filtered = filtered.filter(u => u.activo !== false);
        } else {
            filtered = filtered.filter(u => u.activo === false);
        }

        // Search by selected field
        if (search) {
            const s = search.toLowerCase();
            const field = this._searchFields[this._currentFilter];
            filtered = filtered.filter(u => {
                const val = field.getValue(u);
                return val && val.toLowerCase().includes(s);
            });
        }
        document.getElementById('admin-count').textContent = `${filtered.length} registro(s)`;

        if (filtered.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" class="table-empty">No se encontraron registros</td></tr>';
            return;
        }

        tbody.innerHTML = filtered.map(u => {
            const isInactive = u.activo === false;
            return `<tr>
                <td>${u.nombre} ${u.apellido}</td>
                <td>${u.email}</td>
                <td>${u.numeroDocumento ? `${u.tipoDocumento} ${u.numeroDocumento}` : '-'}</td>
                <td>${u.telefono || '-'}</td>
                <td>${UI.activoBadge(u.activo)}</td>
                <td class="table-actions">
                    ${!isInactive ? `<button class="btn-icon edit" title="Editar" data-id="${u.idUsuario}"><i class="bi bi-pencil"></i></button>` : ''}
                    ${isInactive
                        ? `<button class="btn-icon reactivate" title="Reactivar" data-id="${u.idUsuario}"><i class="bi bi-arrow-counterclockwise"></i></button>`
                        : `<button class="btn-icon delete" title="Eliminar" data-id="${u.idUsuario}"><i class="bi bi-trash"></i></button>`
                    }
                </td>
            </tr>`;
        }).join('');

        tbody.querySelectorAll('.edit').forEach(btn =>
            btn.addEventListener('click', () => {
                const item = this.allData.find(u => String(u.idUsuario) === btn.dataset.id);
                if (item) this.openForm(item);
            })
        );
        tbody.querySelectorAll('.delete').forEach(btn =>
            btn.addEventListener('click', () => {
                UI.confirm('Eliminar este administrador?', async () => {
                    try {
                        await Api.delete(`/usuarios/${btn.dataset.id}`);
                        UI.toast('Administrador eliminado', 'success');
                        _usuariosCache = null;
                        this.loadData();
                    } catch (err) { UI.toast(err.mensaje || 'Error', 'error'); }
                });
            })
        );
        tbody.querySelectorAll('.reactivate').forEach(btn =>
            btn.addEventListener('click', () => {
                UI.confirm('Reactivar este administrador?', async () => {
                    try {
                        await Api.patch(`/usuarios/${btn.dataset.id}/reactivar`);
                        UI.toast('Administrador reactivado', 'success');
                        _usuariosCache = null;
                        this.loadData();
                    } catch (err) { UI.toast(err.mensaje || 'Error al reactivar', 'error'); }
                });
            })
        );
    },

    // --- Formulario modal: solo datos personales (no tiene perfil adicional) ---
    openForm(item) {
        const isEdit = !!item;
        const html = `
            <div class="form-row">
                <div class="form-group">
                    <label>Nombre *</label>
                    <input type="text" class="form-control" id="f-nombre" value="${item?.nombre || ''}" placeholder="Ej: Juan Carlos" minlength="2" maxlength="50">
                </div>
                <div class="form-group">
                    <label>Apellido *</label>
                    <input type="text" class="form-control" id="f-apellido" value="${item?.apellido || ''}" placeholder="Ej: Garcia Lopez" minlength="2" maxlength="64">
                </div>
            </div>
            <div class="form-group">
                <label>Email *</label>
                <input type="email" class="form-control" id="f-email" value="${item?.email || ''}" placeholder="ejemplo@correo.com" maxlength="254">
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
                        ${UI.selectOptions('tipoDocumento', item?.tipoDocumento || '')}
                    </select>
                </div>
                <div class="form-group">
                    <label>Nro. Documento *</label>
                    <input type="text" class="form-control" id="f-numeroDocumento" value="${item?.numeroDocumento || ''}" placeholder="12345678" maxlength="20">
                </div>
            </div>
            <div class="form-row">
                <div class="form-group">
                    <label>Genero *</label>
                    <select class="form-control" id="f-genero">
                        <option value="">-- Seleccionar --</option>
                        ${UI.selectOptions('genero', item?.genero || '')}
                    </select>
                </div>
                <div class="form-group">
                    <label>Telefono</label>
                    <input type="text" class="form-control" id="f-telefono" value="${item?.telefono || ''}" placeholder="Ej: 1134567890" maxlength="10">
                    <small class="form-hint">10 digitos sin 0 ni 15 (ej: 1134567890)</small>
                </div>
            </div>
            <div class="form-group">
                <label>Direccion</label>
                <input type="text" class="form-control" id="f-direccion" value="${item?.direccion || ''}" placeholder="Ej: Av. Corrientes 1234" maxlength="255">
            </div>`;

        UI.openModal(isEdit ? 'Editar Administrador' : 'Nuevo Administrador', html, async () => {
            // Validacion frontend de campos personales
            const errors = UI.validatePersonForm({
                nombre: 'f-nombre', apellido: 'f-apellido', email: 'f-email',
                contrasena: 'f-contrasena', confirmarContrasena: 'f-confirmarContrasena', requirePass: !isEdit,
                tipoDocumento: 'f-tipoDocumento', numeroDocumento: 'f-numeroDocumento',
                genero: 'f-genero', telefono: 'f-telefono'
            });

            if (errors.length > 0) {
                UI.showValidationErrors(errors);
                return;
            }

            const dto = {
                nombre: document.getElementById('f-nombre').value.trim(),
                apellido: document.getElementById('f-apellido').value.trim(),
                email: document.getElementById('f-email').value.trim(),
                contrasena: document.getElementById('f-contrasena').value || undefined,
                tipoDocumento: document.getElementById('f-tipoDocumento').value,
                numeroDocumento: document.getElementById('f-numeroDocumento').value.trim(),
                genero: document.getElementById('f-genero').value,
                telefono: document.getElementById('f-telefono').value.trim() || null,
                direccion: document.getElementById('f-direccion').value.trim() || null,
                rol: 'ADMIN'  // Siempre fija el rol como ADMIN
            };

            try {
                if (isEdit) {
                    await Api.put(`/usuarios/${item.idUsuario}`, dto);
                    UI.toast('Administrador actualizado con exito', 'success');
                } else {
                    await Api.post('/usuarios', dto);
                    UI.toast('Administrador registrado con exito', 'success');
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

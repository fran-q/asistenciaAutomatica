// Pagina: Gestion de alumnos (crea Usuario + perfil UsuarioAlumno)
// Creacion dual: POST /usuarios -> obtiene idUsuario -> POST /alumnos
// Edicion dual: PUT /usuarios/{id} + PUT /alumnos/{id}

const AlumnosPage = {
    allData: [],
    _currentFilter: 0,
    _showInactivos: false,
    // Busqueda: resuelve datos del usuario vinculado via _usuariosCache
    _searchFields: [
        { label: 'Nombre', getValue: (a) => { const u = _usuariosCache?.find(u => u.idUsuario === a.idUsuario); return u ? `${u.nombre} ${u.apellido}` : ''; }},
        { label: 'Email', getValue: (a) => { const u = _usuariosCache?.find(u => u.idUsuario === a.idUsuario); return u?.email || ''; }},
        { label: 'Documento', getValue: (a) => { const u = _usuariosCache?.find(u => u.idUsuario === a.idUsuario); return u?.numeroDocumento || ''; }},
        { label: 'Legajo', getValue: (a) => a.legajo || '' },
        { label: 'Promedio', getValue: (a) => a.promedio != null ? String(a.promedio) : '' }
    ],

    async render() {
        await getUsuariosOptions();

        const filterItems = this._searchFields.map((f, i) =>
            `<button data-filter="${i}" class="${i === 0 ? 'active' : ''}">${f.label}</button>`
        ).join('');

        const content = document.getElementById('page-content');
        content.innerHTML = `
            <div class="page-header">
                <h2 class="page-title"><i class="bi bi-mortarboard"></i> Alumnos</h2>
                <button class="btn btn-primary" id="btn-new-alum"><i class="bi bi-plus-lg"></i> Nuevo</button>
            </div>
            <div class="table-wrapper">
                <div class="table-toolbar">
                    <div class="search-group">
                        <div class="table-search">
                            <i class="bi bi-search"></i>
                            <input type="text" id="alum-search" placeholder="Buscar por nombre...">
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
                    <span id="alum-count"></span>
                </div>
                <div class="table-scroll-body">
                    <table>
                        <thead>
                            <tr>
                                <th>Nombre</th>
                                <th>Email</th>
                                <th>Documento</th>
                                <th class="col-sm">Legajo</th>
                                <th class="col-md">Promedio</th>
                                <th class="col-sm">Estado</th>
                                <th class="col-actions">Acciones</th>
                            </tr>
                        </thead>
                        <tbody id="alum-table"></tbody>
                    </table>
                </div>
            </div>`;

        document.getElementById('btn-new-alum').addEventListener('click', () => this.openForm(null));
        document.getElementById('alum-search').addEventListener('input', (e) => this.renderTable(e.target.value));

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
                    document.getElementById('alum-search').placeholder = `Buscar por ${label.toLowerCase()}...`;
                }
                filterMenu.classList.remove('open');
                this.renderTable(document.getElementById('alum-search').value);
            });
        });

        this.loadData();
    },

    // Recarga alumnos y refresca cache de usuarios
    async loadData() {
        try {
            this.allData = await Api.get('/alumnos');
            _usuariosCache = null;  // Invalida cache para obtener datos actualizados
            await getUsuariosOptions();
            this.renderTable('');
        } catch (err) {
            UI.toast(err.mensaje || 'Error al cargar alumnos', 'error');
        }
    },

    renderTable(search) {
        const tbody = document.getElementById('alum-table');
        let filtered = this.allData;

        // Activo/inactivo filter
        if (!this._showInactivos) {
            filtered = filtered.filter(a => a.activo !== false);
        } else {
            filtered = filtered.filter(a => a.activo === false);
        }

        // Search by selected field
        if (search) {
            const s = search.toLowerCase();
            const field = this._searchFields[this._currentFilter];
            filtered = filtered.filter(a => {
                const val = field.getValue(a);
                return val && val.toLowerCase().includes(s);
            });
        }
        document.getElementById('alum-count').textContent = `${filtered.length} registro(s)`;

        if (filtered.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="table-empty">No se encontraron registros</td></tr>';
            return;
        }

        tbody.innerHTML = filtered.map(a => {
            const user = _usuariosCache?.find(u => u.idUsuario === a.idUsuario);
            return `<tr>
                <td>${user ? `${user.nombre} ${user.apellido}` : '-'}</td>
                <td>${user?.email || '-'}</td>
                <td>${user?.numeroDocumento ? `${user.tipoDocumento} ${user.numeroDocumento}` : '-'}</td>
                <td>${a.legajo || '-'}</td>
                <td>${a.promedio != null ? Number(a.promedio).toFixed(1) : '-'}</td>
                <td>${UI.activoBadge(a.activo)}</td>
                <td class="table-actions">
                    ${a.activo !== false ? `<button class="btn-icon edit" data-id="${a.idAlumno}" data-uid="${a.idUsuario}"><i class="bi bi-pencil"></i></button>` : ''}
                    ${a.activo === false
                        ? `<button class="btn-icon reactivate" data-id="${a.idAlumno}"><i class="bi bi-arrow-counterclockwise"></i></button>`
                        : `<button class="btn-icon delete" data-id="${a.idAlumno}"><i class="bi bi-trash"></i></button>`
                    }
                </td>
            </tr>`;
        }).join('');

        tbody.querySelectorAll('.edit').forEach(btn =>
            btn.addEventListener('click', () => {
                const alum = this.allData.find(a => String(a.idAlumno) === btn.dataset.id);
                const user = _usuariosCache?.find(u => u.idUsuario === alum?.idUsuario);
                if (alum) this.openForm(alum, user);
            })
        );
        tbody.querySelectorAll('.delete').forEach(btn =>
            btn.addEventListener('click', () => {
                UI.confirm('Eliminar este alumno?', async () => {
                    try {
                        await Api.delete(`/alumnos/${btn.dataset.id}`);
                        UI.toast('Alumno eliminado', 'success');
                        _usuariosCache = null;
                        this.loadData();
                    } catch (err) { UI.toast(err.mensaje || 'Error', 'error'); }
                });
            })
        );
        tbody.querySelectorAll('.reactivate').forEach(btn =>
            btn.addEventListener('click', () => {
                UI.confirm('Reactivar este alumno?', async () => {
                    try {
                        await Api.patch(`/alumnos/${btn.dataset.id}/reactivar`);
                        UI.toast('Alumno reactivado', 'success');
                        _usuariosCache = null;
                        this.loadData();
                    } catch (err) { UI.toast(err.mensaje || 'Error', 'error'); }
                });
            })
        );
    },

    // --- Formulario modal: datos personales (Usuario) + datos academicos (Alumno) ---
    openForm(alum, user) {
        const isEdit = !!alum;
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
            <h6 style="color:var(--accent); margin-bottom:12px; font-size:0.8rem; text-transform:uppercase; letter-spacing:1px;">Datos del alumno</h6>
            <div class="form-row">
                <div class="form-group">
                    <label>Legajo *</label>
                    <input type="text" class="form-control" id="f-legajo" value="${alum?.legajo || ''}" placeholder="Ej: 12345" minlength="4" maxlength="10">
                    <small class="form-hint">4-10 caracteres (ej: 12345, S12345, 59296/6)</small>
                </div>
                <div class="form-group">
                    <label>Promedio (0-10)</label>
                    <input type="number" class="form-control" id="f-promedio" value="${alum?.promedio ?? ''}" placeholder="0.0" step="0.1" min="0" max="10">
                </div>
            </div>`;

        UI.openModal(isEdit ? 'Editar Alumno' : 'Nuevo Alumno', html, async () => {
            // Validacion frontend de campos personales + academicos
            const errors = UI.validatePersonForm({
                nombre: 'f-nombre', apellido: 'f-apellido', email: 'f-email',
                contrasena: 'f-contrasena', confirmarContrasena: 'f-confirmarContrasena', requirePass: !isEdit,
                tipoDocumento: 'f-tipoDocumento', numeroDocumento: 'f-numeroDocumento',
                genero: 'f-genero', telefono: 'f-telefono'
            });
            if (!document.getElementById('f-legajo').value.trim())
                errors.push('El legajo es obligatorio');

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
                rol: 'ALUMNO'
            };

            const alumnoDto = {
                legajo: document.getElementById('f-legajo').value.trim(),
                promedio: document.getElementById('f-promedio').value ? parseFloat(document.getElementById('f-promedio').value) : null
            };

            try {
                if (isEdit) {
                    // Edicion: actualiza usuario y luego perfil alumno
                    await Api.put(`/usuarios/${alum.idUsuario}`, usuarioDto);
                    alumnoDto.idUsuario = alum.idUsuario;
                    await Api.put(`/alumnos/${alum.idAlumno}`, alumnoDto);
                    UI.toast('Alumno actualizado con exito', 'success');
                } else {
                    // Creacion: primero crea usuario, luego vincula perfil alumno
                    const nuevoUsuario = await Api.post('/usuarios', usuarioDto);
                    alumnoDto.idUsuario = nuevoUsuario.idUsuario;
                    await Api.post('/alumnos', alumnoDto);
                    UI.toast('Alumno registrado con exito', 'success');
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

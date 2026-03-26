// Pagina: CRUD de cursos con panel lateral de materias

// --- Caches globales (usados por asignaciones.js y otros modulos) ---
let _cursosCache = null;
let _materiasCache = null;

async function getCursosOptions() {
    if (!_cursosCache) {
        try { _cursosCache = await Api.get('/cursos'); } catch { _cursosCache = []; }
    }
    return _cursosCache.filter(c => c.activo !== false).map(c => ({ value: c.idCurso, label: c.nombre }));
}

async function getMateriasOptions() {
    if (!_materiasCache) {
        try { _materiasCache = await Api.get('/materias'); } catch { _materiasCache = []; }
    }
    return _materiasCache.filter(m => m.activo !== false).map(m => ({ value: m.idMateria, label: m.nombre }));
}

// --- Pagina principal de cursos ---
const CursosPage = {
    allData: [],
    cursoMateriasMap: {},
    _currentFilter: 0,         // Indice del campo de busqueda activo
    _showInactivos: false,     // Toggle para mostrar solo inactivos
    _searchFields: [
        { label: 'Nombre', getValue: (c) => c.nombre },
        { label: 'Comision', getValue: (c) => c.comision },
        { label: 'Turno', getValue: (c) => c.turno },
        { label: 'Carrera', getValue: (c) => { const carr = _carrerasCache?.find(ca => ca.idCarrera === c.idCarrera); return carr?.nombre || ''; }},
        { label: 'Anio Lectivo', getValue: (c) => c.anioLectivo ? String(c.anioLectivo) : '' }
    ],

    // --- Renderizado: tabla + toolbar + panel de materias ---
    async render() {
        const carrOpts = await getCarrerasOptions();
        await getMateriasOptions();

        const filterItems = this._searchFields.map((f, i) =>
            `<button data-filter="${i}" class="${i === 0 ? 'active' : ''}">${f.label}</button>`
        ).join('');

        const content = document.getElementById('page-content');
        content.innerHTML = `
            <div class="page-header">
                <h2 class="page-title"><i class="bi bi-collection"></i> Cursos</h2>
                <button class="btn btn-primary" id="btn-new"><i class="bi bi-plus-lg"></i> Nuevo</button>
            </div>
            <div class="table-wrapper">
                <div class="table-toolbar">
                    <div class="search-group">
                        <div class="table-search">
                            <i class="bi bi-search"></i>
                            <input type="text" id="crud-search" placeholder="Buscar por nombre...">
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
                    <span class="table-count" id="table-count"></span>
                </div>
                <div class="table-scroll-body">
                    <table>
                        <thead>
                            <tr>
                                <th>Nombre</th>
                                <th class="col-xs">Anio</th>
                                <th class="col-sm">Comision</th>
                                <th class="col-sm">Turno</th>
                                <th>Carrera</th>
                                <th class="col-md">Anio Lectivo</th>
                                <th class="col-sm">Materias</th>
                                <th class="col-sm">Estado</th>
                                <th class="col-actions">Acciones</th>
                            </tr>
                        </thead>
                        <tbody id="table-body">
                            <tr><td colspan="9" class="table-empty">Cargando...</td></tr>
                        </tbody>
                    </table>
                </div>
            </div>
            <div id="materias-panel" class="materias-panel" style="display:none;">
                <div class="materias-panel-header">
                    <h4 id="materias-panel-title"></h4>
                    <button class="btn-icon" id="materias-panel-close"><i class="bi bi-x-lg"></i></button>
                </div>
                <div class="materias-panel-body">
                    <div class="materias-add-row">
                        <select class="form-control" id="materia-select">
                            <option value="">-- Agregar materia --</option>
                            ${_materiasCache.filter(m => m.activo !== false).map(m => `<option value="${m.idMateria}">${m.nombre}</option>`).join('')}
                        </select>
                        <button class="btn btn-primary btn-sm" id="btn-add-materia"><i class="bi bi-plus"></i> Agregar</button>
                    </div>
                    <div id="materias-list" class="materias-list"></div>
                </div>
            </div>`;

        // --- Eventos de la toolbar ---
        document.getElementById('btn-new').addEventListener('click', () => {
            if (carrOpts.length === 0) {
                UI.toast('No es posible crear un curso, primero registra al menos una carrera', 'warning');
                return;
            }
            this.openForm(null, carrOpts);
        });
        document.getElementById('crud-search').addEventListener('input', (e) => this.renderTable(e.target.value, carrOpts));

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
                    document.getElementById('crud-search').placeholder = `Buscar por ${label.toLowerCase()}...`;
                }
                filterMenu.classList.remove('open');
                this.renderTable(document.getElementById('crud-search').value, carrOpts);
            });
        });

        document.getElementById('materias-panel-close').addEventListener('click', () => this.closeMateriaPanel());
        document.getElementById('btn-add-materia').addEventListener('click', () => this.addMateria());

        await this.loadData(carrOpts);
    },

    async loadData(carrOpts) {
        try {
            this.allData = await Api.get('/cursos');
            _cursosCache = this.allData;
            this.renderTable('', carrOpts);
        } catch (err) {
            UI.toast(err.mensaje || 'Error al cargar cursos', 'error');
        }
    },

    // --- Renderiza filas de la tabla con filtros de busqueda y estado ---
    renderTable(search, carrOpts) {
        const tbody = document.getElementById('table-body');
        let filtered = this.allData;

        // Activo/inactivo filter
        if (!this._showInactivos) {
            filtered = filtered.filter(c => c.activo !== false);
        } else {
            filtered = filtered.filter(c => c.activo === false);
        }

        // Search by selected field
        if (search) {
            const s = search.toLowerCase();
            const field = this._searchFields[this._currentFilter];
            filtered = filtered.filter(c => {
                const val = field.getValue(c);
                return val && val.toLowerCase().includes(s);
            });
        }

        document.getElementById('table-count').textContent = `${filtered.length} registro(s)`;

        if (filtered.length === 0) {
            tbody.innerHTML = '<tr><td colspan="9" class="table-empty">No se encontraron registros</td></tr>';
            return;
        }

        tbody.innerHTML = filtered.map(c => {
            const carr = _carrerasCache?.find(ca => ca.idCarrera === c.idCarrera);
            const isInactive = c.activo === false;
            return `<tr>
                <td>${c.nombre}</td>
                <td>${c.anioCarrera || '-'}</td>
                <td>${c.comision || '-'}</td>
                <td>${c.turno ? `<span class="badge badge-info">${c.turno}</span>` : '-'}</td>
                <td>${carr ? carr.nombre : '#' + c.idCarrera}</td>
                <td>${c.anioLectivo || '-'}</td>
                <td>
                    <button class="btn btn-outline btn-sm materias-btn" data-id="${c.idCurso}" data-nombre="${c.nombre}">
                        <i class="bi bi-book"></i> Materias
                    </button>
                </td>
                <td>${UI.activoBadge(c.activo)}</td>
                <td class="table-actions">
                    ${!isInactive ? `<button class="btn-icon edit" title="Editar" data-id="${c.idCurso}"><i class="bi bi-pencil"></i></button>` : ''}
                    ${isInactive
                        ? `<button class="btn-icon reactivate" title="Reactivar" data-id="${c.idCurso}"><i class="bi bi-arrow-counterclockwise"></i></button>`
                        : `<button class="btn-icon delete" title="Eliminar" data-id="${c.idCurso}"><i class="bi bi-trash"></i></button>`
                    }
                </td>
            </tr>`;
        }).join('');

        tbody.querySelectorAll('.edit').forEach(btn =>
            btn.addEventListener('click', () => {
                const item = this.allData.find(d => String(d.idCurso) === btn.dataset.id);
                if (item) this.openForm(item, carrOpts);
            })
        );

        tbody.querySelectorAll('.delete').forEach(btn =>
            btn.addEventListener('click', () => {
                UI.confirm('Estas seguro de eliminar este curso?', async () => {
                    try {
                        await Api.delete(`/cursos/${btn.dataset.id}`);
                        UI.toast('Curso eliminado', 'success');
                        await this.loadData(carrOpts);
                    } catch (err) {
                        UI.toast(err.mensaje || 'Error al eliminar', 'error');
                    }
                });
            })
        );

        tbody.querySelectorAll('.reactivate').forEach(btn =>
            btn.addEventListener('click', () => {
                UI.confirm('Reactivar este curso?', async () => {
                    try {
                        await Api.patch(`/cursos/${btn.dataset.id}/reactivar`);
                        UI.toast('Curso reactivado', 'success');
                        _cursosCache = null;
                        await this.loadData(carrOpts);
                    } catch (err) {
                        UI.toast(err.mensaje || 'Error al reactivar', 'error');
                    }
                });
            })
        );

        tbody.querySelectorAll('.materias-btn').forEach(btn =>
            btn.addEventListener('click', () => {
                this.openMateriaPanel(parseInt(btn.dataset.id), btn.dataset.nombre);
            })
        );
    },

    // --- Formulario modal para crear/editar curso ---
    openForm(item, carrOpts) {
        const isEdit = !!item;
        const html = `
            <div class="form-group">
                <label>Nombre</label>
                <input type="text" class="form-control" id="field-nombre" value="${item?.nombre || ''}" placeholder="Ej: 1A, 2B" maxlength="100">
            </div>
            <div class="form-row">
                <div class="form-group">
                    <label>Anio Carrera (1-10)</label>
                    <input type="number" class="form-control" id="field-anioCarrera" value="${item?.anioCarrera || ''}" placeholder="1">
                </div>
                <div class="form-group">
                    <label>Comision</label>
                    <input type="text" class="form-control" id="field-comision" value="${item?.comision || ''}" placeholder="Ej: A, B, C" maxlength="20">
                </div>
            </div>
            <div class="form-row">
                <div class="form-group">
                    <label>Turno</label>
                    <select class="form-control" id="field-turno">
                        <option value="">-- Seleccionar --</option>
                        ${UI.selectOptions('turno', item?.turno || '')}
                    </select>
                </div>
                <div class="form-group">
                    <label>Anio Lectivo</label>
                    <input type="number" class="form-control" id="field-anioLectivo" value="${item?.anioLectivo || ''}" placeholder="2025">
                </div>
            </div>
            <div class="form-group">
                <label>Carrera</label>
                <select class="form-control" id="field-idCarrera">
                    <option value="">-- Seleccionar --</option>
                    ${carrOpts.map(o => `<option value="${o.value}" ${String(o.value) === String(item?.idCarrera || '') ? 'selected' : ''}>${o.label}</option>`).join('')}
                </select>
            </div>`;

        UI.openModal(isEdit ? 'Editar Curso' : 'Nuevo Curso', html, async () => {
            const dto = {
                nombre: document.getElementById('field-nombre').value,
                anioCarrera: parseInt(document.getElementById('field-anioCarrera').value) || null,
                comision: document.getElementById('field-comision').value,
                turno: document.getElementById('field-turno').value,
                idCarrera: parseInt(document.getElementById('field-idCarrera').value) || null,
                anioLectivo: parseInt(document.getElementById('field-anioLectivo').value) || null
            };
            try {
                if (isEdit) {
                    await Api.put(`/cursos/${item.idCurso}`, dto);
                    UI.toast('Curso actualizado', 'success');
                } else {
                    await Api.post('/cursos', dto);
                    UI.toast('Curso creado', 'success');
                }
                UI.closeModal();
                _cursosCache = null;
                await this.loadData(carrOpts);
            } catch (err) {
                const msg = err.detalles?.length ? err.detalles.join(', ') : err.mensaje || 'Error al guardar';
                UI.toast(msg, 'error');
            }
        });
    },

    // ========== Panel lateral: gestion de materias por curso (CursoMateria) ==========

    _currentCursoId: null,
    _cursoMaterias: [],

    async openMateriaPanel(cursoId, cursoNombre) {
        this._currentCursoId = cursoId;
        document.getElementById('materias-panel-title').textContent = `Materias de "${cursoNombre}"`;
        document.getElementById('materias-panel').style.display = 'block';
        document.getElementById('materias-panel').scrollIntoView({ behavior: 'smooth' });
        await this.loadCursoMaterias();
    },

    closeMateriaPanel() {
        document.getElementById('materias-panel').style.display = 'none';
        this._currentCursoId = null;
    },

    async loadCursoMaterias() {
        const list = document.getElementById('materias-list');
        list.innerHTML = '<div style="text-align:center; color:var(--text-muted); padding:12px;">Cargando...</div>';

        try {
            this._cursoMaterias = await Api.get(`/curso-materias/curso/${this._currentCursoId}`);
            this.renderMateriasList();
        } catch (err) {
            list.innerHTML = '<div style="text-align:center; color:var(--danger); padding:12px;">Error al cargar materias</div>';
        }
    },

    renderMateriasList() {
        const list = document.getElementById('materias-list');

        if (this._cursoMaterias.length === 0) {
            list.innerHTML = '<div class="materias-empty">No hay materias asignadas a este curso</div>';
            return;
        }

        list.innerHTML = this._cursoMaterias.map(cm => {
            const materia = _materiasCache?.find(m => m.idMateria === cm.idMateria);
            return `<div class="materia-item">
                <div class="materia-item-info">
                    <i class="bi bi-book"></i>
                    <span>${materia ? materia.nombre : 'Materia #' + cm.idMateria}</span>
                    ${materia?.horasSemanales ? `<span class="badge badge-muted">${materia.horasSemanales} hs/sem</span>` : ''}
                </div>
                <button class="btn-icon delete materia-remove" data-id="${cm.idCursoMateria}" title="Quitar materia">
                    <i class="bi bi-x-circle"></i>
                </button>
            </div>`;
        }).join('');

        list.querySelectorAll('.materia-remove').forEach(btn =>
            btn.addEventListener('click', () => this.removeMateria(parseInt(btn.dataset.id)))
        );
    },

    // Agrega una materia al curso actual via POST /curso-materias
    async addMateria() {
        const select = document.getElementById('materia-select');
        const idMateria = parseInt(select.value);
        if (!idMateria) {
            UI.toast('Selecciona una materia', 'warning');
            return;
        }

        // Evita duplicar materia ya asignada
        if (this._cursoMaterias.some(cm => cm.idMateria === idMateria)) {
            UI.toast('Esta materia ya esta asignada a este curso', 'warning');
            return;
        }

        try {
            await Api.post('/curso-materias', {
                idCurso: this._currentCursoId,
                idMateria: idMateria
            });
            select.value = '';
            UI.toast('Materia agregada al curso', 'success');
            await this.loadCursoMaterias();
        } catch (err) {
            UI.toast(err.mensaje || 'Error al agregar materia', 'error');
        }
    },

    // Quita una materia del curso via DELETE /curso-materias/{id}
    async removeMateria(idCursoMateria) {
        UI.confirm('Quitar esta materia del curso?', async () => {
            try {
                await Api.delete(`/curso-materias/${idCursoMateria}`);
                UI.toast('Materia quitada del curso', 'success');
                await this.loadCursoMaterias();
            } catch (err) {
                UI.toast(err.mensaje || 'Error al quitar materia', 'error');
            }
        });
    }
};

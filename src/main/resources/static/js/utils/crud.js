// --- Generador generico de paginas CRUD ---
// Recibe config (titulo, endpoint, columnas, campos) y genera tabla+busqueda+paginacion+formulario
// searchFields: [{ key, label, getValue }] - primer elemento es filtro por defecto, "Inactivos" se agrega auto
// requiredDeps: [{ check, message }] - dependencias requeridas antes de crear un registro
function createCrudPage({ title, icon, endpoint, columns, formFields, formToDto, mapRow, searchFields, noEdit, noCreate, noDelete, requiredDeps }) {

    let currentFilter = 0; // Indice del filtro de busqueda activo
    let showInactivos = false;

    // --- Construccion del HTML de busqueda y filtros ---
    function buildSearchHtml() {
        const filterItems = (searchFields || []).map((f, i) =>
            `<button data-filter="${i}" class="${i === 0 ? 'active' : ''}">${f.label}</button>`
        ).join('');
        const defaultLabel = searchFields?.[0]?.label || 'Nombre';

        return `
            <div class="search-group">
                <div class="table-search">
                    <i class="bi bi-search"></i>
                    <input type="text" id="crud-search" placeholder="Buscar por ${defaultLabel.toLowerCase()}...">
                </div>
                ${searchFields && searchFields.length > 0 ? `
                <div class="filter-dropdown">
                    <button class="filter-dropdown-btn" id="filter-btn">
                        <i class="bi bi-funnel"></i> <span id="filter-label">${defaultLabel}</span> <i class="bi bi-chevron-down" style="font-size:0.65rem;"></i>
                    </button>
                    <div class="filter-dropdown-menu" id="filter-menu">
                        ${filterItems}
                        <div class="filter-divider"></div>
                        <button data-filter="inactivos">Inactivos</button>
                    </div>
                </div>` : ''}
            </div>`;
    }

    // --- Construccion del HTML del formulario (crear/editar) ---
    function buildFormHtml(data) {
        return formFields.map(f => {
            const val = data ? (data[f.key] ?? '') : (f.default ?? '');
            if (f.type === 'row-start') return '<div class="form-row">';
            if (f.type === 'row-end') return '</div>';
            if (f.type === 'select') {
                const options = typeof f.options === 'function' ? f.options() : f.options;
                return `<div class="form-group">
                    <label>${f.label}</label>
                    <select class="form-control" id="field-${f.key}">
                        <option value="">-- Seleccionar --</option>
                        ${options.map(o => `<option value="${o.value}" ${String(o.value) === String(val) ? 'selected' : ''}>${o.label}</option>`).join('')}
                    </select>
                </div>`;
            }
            if (f.type === 'enum') {
                return `<div class="form-group">
                    <label>${f.label}</label>
                    <select class="form-control" id="field-${f.key}">
                        <option value="">-- Seleccionar --</option>
                        ${UI.selectOptions(f.enumName, String(val))}
                    </select>
                </div>`;
            }
            if (f.type === 'textarea') {
                return `<div class="form-group">
                    <label>${f.label}</label>
                    <textarea class="form-control" id="field-${f.key}" rows="3" ${f.maxlength ? `maxlength="${f.maxlength}"` : ''}>${val}</textarea>
                </div>`;
            }
            return `<div class="form-group">
                <label>${f.label}</label>
                <input type="${f.type || 'text'}" class="form-control" id="field-${f.key}" value="${val}" ${f.placeholder ? `placeholder="${f.placeholder}"` : ''} ${f.maxlength ? `maxlength="${f.maxlength}"` : ''}>
            </div>`;
        }).join('');
    }

    // Recolecta valores del formulario y aplica formToDto si existe
    function getFormValues() {
        const data = {};
        formFields.forEach(f => {
            if (f.type === 'row-start' || f.type === 'row-end') return;
            const el = document.getElementById(`field-${f.key}`);
            if (el) data[f.key] = el.value;
        });
        return formToDto ? formToDto(data) : data;
    }

    // Verifica dependencias requeridas antes de abrir formulario de creacion
    async function checkDeps() {
        if (!requiredDeps || requiredDeps.length === 0) return true;
        for (const dep of requiredDeps) {
            const ok = await dep.check();
            if (!ok) {
                UI.toast(dep.message, 'warning');
                return false;
            }
        }
        return true;
    }

    // Columnas visibles (se oculta la columna ID)
    const displayColumns = columns.filter(c => c.label !== 'ID');
    const idKey = columns[0]?.idKey || 'id';

    let allData = []; // Datos cargados del endpoint

    // --- Render: construye estructura de pagina (header, tabla, toolbar) ---
    async function render() {
        currentFilter = 0;
        showInactivos = false;

        const content = document.getElementById('page-content');
        content.innerHTML = `
            <div class="page-header">
                <h2 class="page-title"><i class="bi ${icon}"></i> ${title}</h2>
                ${!noCreate ? '<button class="btn btn-primary" id="btn-new"><i class="bi bi-plus-lg"></i> Nuevo</button>' : ''}
            </div>
            <div class="table-wrapper">
                <div class="table-toolbar">
                    ${buildSearchHtml()}
                    <span class="table-count" id="table-count"></span>
                </div>
                <div class="table-scroll-body">
                    <table>
                        <thead>
                            <tr>
                                ${displayColumns.map(c => `<th${c.width ? ` class="${c.width}"` : ''}>${c.label}</th>`).join('')}
                                <th class="col-actions">Acciones</th>
                            </tr>
                        </thead>
                        <tbody id="table-body">
                            <tr><td colspan="${displayColumns.length + 1}" class="table-empty">Cargando...</td></tr>
                        </tbody>
                    </table>
                </div>
            </div>`;

        if (!noCreate) {
            document.getElementById('btn-new')?.addEventListener('click', async () => {
                if (await checkDeps()) openForm(null);
            });
        }
        document.getElementById('crud-search').addEventListener('input', (e) => renderTable(e.target.value));

        // --- Logica del dropdown de filtros ---
        const filterBtn = document.getElementById('filter-btn');
        const filterMenu = document.getElementById('filter-menu');
        if (filterBtn && filterMenu) {
            filterBtn.addEventListener('click', (e) => {
                e.stopPropagation();
                filterMenu.classList.toggle('open');
            });
            document.addEventListener('click', () => filterMenu.classList.remove('open'));

            filterMenu.querySelectorAll('button').forEach(btn => {
                btn.addEventListener('click', () => {
                    const filterVal = btn.dataset.filter;
                    if (filterVal === 'inactivos') {
                        showInactivos = !showInactivos;
                        btn.classList.toggle('active', showInactivos);
                    } else {
                        const idx = parseInt(filterVal);
                        currentFilter = idx;
                        // Reset inactivos toggle when selecting a normal filter
                        showInactivos = false;
                        const inactivosBtn = filterMenu.querySelector('[data-filter="inactivos"]');
                        if (inactivosBtn) inactivosBtn.classList.remove('active');
                        // Update active state for non-inactivos buttons
                        filterMenu.querySelectorAll('button:not([data-filter="inactivos"])').forEach(b => b.classList.remove('active'));
                        btn.classList.add('active');
                        // Update label and placeholder
                        const label = searchFields[idx].label;
                        document.getElementById('filter-label').textContent = label;
                        document.getElementById('crud-search').placeholder = `Buscar por ${label.toLowerCase()}...`;
                    }
                    filterMenu.classList.remove('open');
                    renderTable(document.getElementById('crud-search').value);
                });
            });
        }

        await loadData();
    }

    // --- Carga de datos desde el endpoint ---
    async function loadData() {
        try {
            allData = await Api.get(endpoint);
            renderTable('');
        } catch (err) {
            UI.toast(err.mensaje || 'Error al cargar datos', 'error');
        }
    }

    // --- Renderizado de tabla con filtrado y busqueda ---
    function renderTable(search) {
        const tbody = document.getElementById('table-body');
        let filtered = allData;

        // Filtrar por activos/inactivos
        if (!showInactivos) {
            filtered = filtered.filter(item => item.activo !== false);
        } else {
            filtered = filtered.filter(item => item.activo === false);
        }

        // Busqueda de texto segun el campo de filtro seleccionado
        if (search && searchFields && searchFields.length > 0) {
            const s = search.toLowerCase();
            const field = searchFields[currentFilter];
            filtered = filtered.filter(item => {
                const val = field.getValue(item);
                return val && val.toLowerCase().includes(s);
            });
        }

        document.getElementById('table-count').textContent = `${filtered.length} registro(s)`;

        if (filtered.length === 0) {
            tbody.innerHTML = `<tr><td colspan="${displayColumns.length + 1}" class="table-empty">No se encontraron registros</td></tr>`;
            return;
        }

        // Generar filas con botones de editar/eliminar/reactivar
        tbody.innerHTML = filtered.map(item => {
            const row = mapRow(item);
            const displayRow = row.slice(1);
            const isInactive = item.activo === false;
            return `<tr>
                ${displayRow.map(cell => `<td>${cell}</td>`).join('')}
                <td class="table-actions">
                    ${!noEdit && !isInactive ? `<button class="btn-icon edit" title="Editar" data-id="${item[idKey]}"><i class="bi bi-pencil"></i></button>` : ''}
                    ${!noDelete ? (isInactive
                        ? `<button class="btn-icon reactivate" title="Reactivar" data-id="${item[idKey]}"><i class="bi bi-arrow-counterclockwise"></i></button>`
                        : `<button class="btn-icon delete" title="Eliminar" data-id="${item[idKey]}"><i class="bi bi-trash"></i></button>`
                    ) : ''}
                </td>
            </tr>`;
        }).join('');

        // --- Listeners de acciones: editar, eliminar, reactivar ---
        tbody.querySelectorAll('.edit').forEach(btn =>
            btn.addEventListener('click', () => {
                const id = btn.dataset.id;
                const item = allData.find(d => String(d[idKey]) === id);
                if (item) openForm(item);
            })
        );

        tbody.querySelectorAll('.delete').forEach(btn =>
            btn.addEventListener('click', () => {
                const id = btn.dataset.id;
                UI.confirm('Estas seguro de eliminar este registro?', async () => {
                    try {
                        await Api.delete(`${endpoint}/${id}`);
                        UI.toast('Registro eliminado', 'success');
                        await loadData();
                    } catch (err) {
                        UI.toast(err.mensaje || 'Error al eliminar', 'error');
                    }
                });
            })
        );

        tbody.querySelectorAll('.reactivate').forEach(btn =>
            btn.addEventListener('click', () => {
                const id = btn.dataset.id;
                UI.confirm('Reactivar este registro?', async () => {
                    try {
                        await Api.patch(`${endpoint}/${id}/reactivar`);
                        UI.toast('Registro reactivado', 'success');
                        await loadData();
                    } catch (err) {
                        UI.toast(err.mensaje || 'Error al reactivar', 'error');
                    }
                });
            })
        );
    }

    // --- Apertura de formulario en modal (crear o editar) ---
    function openForm(item) {
        const isEdit = !!item;
        const html = buildFormHtml(item);

        UI.openModal(isEdit ? `Editar ${title}` : `Nuevo ${title}`, html, async () => {
            const dto = getFormValues();
            try {
                if (isEdit) {
                    await Api.put(`${endpoint}/${item[idKey]}`, dto);
                    UI.toast('Registro actualizado', 'success');
                } else {
                    await Api.post(endpoint, dto);
                    UI.toast('Registro creado', 'success');
                }
                UI.closeModal();
                await loadData();
            } catch (err) {
                const msg = err.detalles?.length
                    ? err.detalles.join(', ')
                    : err.mensaje || 'Error al guardar';
                UI.toast(msg, 'error');
            }
        });
    }

    return { render, loadData };
}

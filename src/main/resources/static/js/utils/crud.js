// Generic CRUD page builder
function createCrudPage({ title, icon, endpoint, columns, formFields, formToDto, mapRow, searchFilter }) {

    function buildSearchHtml() {
        return `
            <div class="table-search">
                <i class="bi bi-search"></i>
                <input type="text" id="crud-search" placeholder="Buscar...">
            </div>`;
    }

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
                    <textarea class="form-control" id="field-${f.key}" rows="3">${val}</textarea>
                </div>`;
            }
            return `<div class="form-group">
                <label>${f.label}</label>
                <input type="${f.type || 'text'}" class="form-control" id="field-${f.key}" value="${val}" ${f.placeholder ? `placeholder="${f.placeholder}"` : ''}>
            </div>`;
        }).join('');
    }

    function getFormValues() {
        const data = {};
        formFields.forEach(f => {
            if (f.type === 'row-start' || f.type === 'row-end') return;
            const el = document.getElementById(`field-${f.key}`);
            if (el) data[f.key] = el.value;
        });
        return formToDto ? formToDto(data) : data;
    }

    let allData = [];

    async function render() {
        const content = document.getElementById('page-content');
        content.innerHTML = `
            <div class="page-header">
                <h2 class="page-title"><i class="bi ${icon}"></i> ${title}</h2>
                <button class="btn btn-primary" id="btn-new"><i class="bi bi-plus-lg"></i> Nuevo</button>
            </div>
            <div class="table-wrapper">
                <div class="table-toolbar">
                    ${buildSearchHtml()}
                    <span class="table-count" id="table-count"></span>
                </div>
                <table>
                    <thead>
                        <tr>
                            ${columns.map(c => `<th>${c.label}</th>`).join('')}
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody id="table-body">
                        <tr><td colspan="${columns.length + 1}" class="table-empty">Cargando...</td></tr>
                    </tbody>
                </table>
            </div>`;

        document.getElementById('btn-new').addEventListener('click', () => openForm(null));
        document.getElementById('crud-search').addEventListener('input', (e) => renderTable(e.target.value));

        await loadData();
    }

    async function loadData() {
        try {
            allData = await Api.get(endpoint);
            renderTable('');
        } catch (err) {
            UI.toast(err.mensaje || 'Error al cargar datos', 'error');
        }
    }

    function renderTable(search) {
        const tbody = document.getElementById('table-body');
        let filtered = allData;
        if (search && searchFilter) {
            const s = search.toLowerCase();
            filtered = allData.filter(item => searchFilter(item, s));
        }

        document.getElementById('table-count').textContent = `${filtered.length} registro(s)`;

        if (filtered.length === 0) {
            tbody.innerHTML = `<tr><td colspan="${columns.length + 1}" class="table-empty">No se encontraron registros</td></tr>`;
            return;
        }

        tbody.innerHTML = filtered.map(item => {
            const row = mapRow(item);
            return `<tr>
                ${row.map(cell => `<td>${cell}</td>`).join('')}
                <td class="table-actions">
                    <button class="btn-icon edit" title="Editar" data-id="${item[columns[0]?.idKey || 'id']}"><i class="bi bi-pencil"></i></button>
                    <button class="btn-icon delete" title="Eliminar" data-id="${item[columns[0]?.idKey || 'id']}"><i class="bi bi-trash"></i></button>
                </td>
            </tr>`;
        }).join('');

        tbody.querySelectorAll('.edit').forEach(btn =>
            btn.addEventListener('click', () => {
                const id = btn.dataset.id;
                const item = allData.find(d => String(d[columns[0]?.idKey || 'id']) === id);
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
    }

    function openForm(item) {
        const isEdit = !!item;
        const html = buildFormHtml(item);
        const idKey = columns[0]?.idKey || 'id';

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

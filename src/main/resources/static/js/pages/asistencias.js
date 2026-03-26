// Pagina: Asistencias manuales con filtros
const AsistenciasPage = {
    async render() {
        // Carga opciones para filtros y formulario
        const [profOpts, asigOpts] = await Promise.all([
            getProfesoresOptions(),
            getAsignacionesOptions()
        ]);

        const content = document.getElementById('page-content');
        content.innerHTML = `
            <div class="page-header">
                <h2 class="page-title"><i class="bi bi-calendar-check"></i> Asistencias</h2>
                <button class="btn btn-primary" id="btn-new-asist"><i class="bi bi-plus-lg"></i> Registrar Asistencia</button>
            </div>

            <!-- Tarjeta de filtros: profesor, fecha, estado -->
            <div class="asist-filter-card">
                <div class="asist-filter-header">
                    <div class="asist-filter-title">
                        <i class="bi bi-funnel"></i> Filtros
                    </div>
                    <button class="btn btn-sm btn-secondary" id="btn-clear-filters" title="Limpiar filtros">
                        <i class="bi bi-x-lg"></i> Limpiar
                    </button>
                </div>
                <div class="asist-filter-body">
                    <div class="asist-filter-group">
                        <label><i class="bi bi-person"></i> Profesor</label>
                        <select class="form-control" id="filter-profesor">
                            <option value="">Todos</option>
                            ${profOpts.map(o => `<option value="${o.value}">${o.label}</option>`).join('')}
                        </select>
                    </div>
                    <div class="asist-filter-group">
                        <label><i class="bi bi-calendar3"></i> Fecha</label>
                        <input type="date" class="form-control" id="filter-fecha">
                    </div>
                    <div class="asist-filter-group">
                        <label><i class="bi bi-flag"></i> Estado</label>
                        <select class="form-control" id="filter-estado">
                            <option value="">Todos</option>
                            <option value="PRESENTE">Presente</option>
                            <option value="AUSENTE">Ausente</option>
                            <option value="TARDANZA">Tardanza</option>
                            <option value="JUSTIFICADO">Justificado</option>
                        </select>
                    </div>
                    <div class="asist-filter-group asist-filter-action">
                        <button class="btn btn-primary" id="btn-filter"><i class="bi bi-search"></i> Buscar</button>
                    </div>
                </div>
            </div>

            <!-- Tabla de resultados filtrados -->
            <div class="asist-results-card">
                <div class="asist-results-header">
                    <span class="asist-results-label">
                        <i class="bi bi-list-ul"></i> Resultados
                        <span class="asist-results-count" id="asist-count">0</span>
                    </span>
                </div>
                <div class="table-wrapper" style="border:none; border-radius:0;">
                    <div class="table-scroll-body">
                        <table>
                            <thead>
                                <tr>
                                    <th class="col-md">Fecha</th>
                                    <th>Profesor</th>
                                    <th>Cargo</th>
                                    <th class="col-sm">Entrada</th>
                                    <th class="col-sm">Salida</th>
                                    <th class="col-sm">Estado</th>
                                    <th class="col-sm">Modo</th>
                                    <th class="col-actions">Acciones</th>
                                </tr>
                            </thead>
                            <tbody id="asist-table"></tbody>
                        </table>
                    </div>
                </div>
            </div>`;

        document.getElementById('btn-new-asist').addEventListener('click', () => {
            if (profOpts.length === 0) {
                UI.toast('No es posible registrar una asistencia, primero registra al menos un profesor', 'warning');
                return;
            }
            this.openForm(null, profOpts, asigOpts);
        });
        document.getElementById('btn-filter').addEventListener('click', () => this.loadData(profOpts, asigOpts));
        document.getElementById('btn-clear-filters').addEventListener('click', () => {
            document.getElementById('filter-profesor').value = '';
            document.getElementById('filter-fecha').value = '';
            document.getElementById('filter-estado').value = '';
            this.loadData(profOpts, asigOpts);
        });

        // Cualquier cambio en filtros recarga automaticamente
        ['filter-profesor', 'filter-fecha', 'filter-estado'].forEach(id => {
            document.getElementById(id).addEventListener('change', () => this.loadData(profOpts, asigOpts));
        });

        this.profOpts = profOpts;
        this.asigOpts = asigOpts;
        this.loadData(profOpts, asigOpts);
    },

    // --- Carga y filtrado de datos ---
    async loadData(profOpts, asigOpts) {
        const tbody = document.getElementById('asist-table');
        const countEl = document.getElementById('asist-count');
        tbody.innerHTML = '<tr><td colspan="8" class="table-empty">Cargando...</td></tr>';

        try {
            let data = await Api.get('/asistencias');

            // Aplica filtros locales sobre los datos ya obtenidos
            const fProf = document.getElementById('filter-profesor').value;
            const fFecha = document.getElementById('filter-fecha').value;
            const fEstado = document.getElementById('filter-estado').value;

            if (fProf) data = data.filter(a => String(a.idProfesor) === fProf);
            if (fFecha) data = data.filter(a => a.fecha === fFecha);
            if (fEstado) data = data.filter(a => a.estado === fEstado);

            countEl.textContent = data.length;

            if (data.length === 0) {
                tbody.innerHTML = '<tr><td colspan="8" class="table-empty">Sin registros</td></tr>';
                return;
            }

            // Ordena de mas reciente a mas antiguo y genera filas con badges de estado
            tbody.innerHTML = data.reverse().map(a => {
                const prof = profOpts.find(o => o.value === a.idProfesor);
                const asig = asigOpts.find(o => o.value === a.idAsignacion);
                const isInactive = a.activo === false;
                return `<tr${isInactive ? ' class="row-inactive"' : ''}>
                    <td>${UI.formatDate(a.fecha)}</td>
                    <td>${prof ? prof.label : '#' + a.idProfesor}</td>
                    <td>${asig ? asig.label : (a.idAsignacion ? '#' + a.idAsignacion : '<span class="text-muted">-</span>')}</td>
                    <td>${UI.formatTime(a.horaEntrada)}</td>
                    <td>${UI.formatTime(a.horaSalida)}</td>
                    <td>${UI.estadoBadge(a.estado)}</td>
                    <td><span class="badge badge-muted">${a.modoRegistro || '-'}</span></td>
                    <td class="table-actions">
                        ${!isInactive ? `<button class="btn-icon edit" data-id="${a.idAsistencia}" title="Editar"><i class="bi bi-pencil"></i></button>` : ''}
                        ${isInactive
                            ? `<button class="btn-icon reactivate" title="Reactivar" data-id="${a.idAsistencia}"><i class="bi bi-arrow-counterclockwise"></i></button>`
                            : `<button class="btn-icon delete" data-id="${a.idAsistencia}" title="Eliminar"><i class="bi bi-trash"></i></button>`
                        }
                    </td>
                </tr>`;
            }).join('');

            tbody.querySelectorAll('.edit').forEach(btn =>
                btn.addEventListener('click', async () => {
                    const item = data.find(a => String(a.idAsistencia) === btn.dataset.id);
                    if (item) this.openForm(item, profOpts, asigOpts);
                })
            );

            tbody.querySelectorAll('.delete').forEach(btn =>
                btn.addEventListener('click', () => {
                    UI.confirm('Eliminar esta asistencia?', async () => {
                        try {
                            await Api.delete(`/asistencias/${btn.dataset.id}`);
                            UI.toast('Asistencia eliminada', 'success');
                            this.loadData(profOpts, asigOpts);
                        } catch (err) { UI.toast(err.mensaje || 'Error', 'error'); }
                    });
                })
            );

            tbody.querySelectorAll('.reactivate').forEach(btn =>
                btn.addEventListener('click', () => {
                    UI.confirm('Reactivar esta asistencia?', async () => {
                        try {
                            await Api.patch(`/asistencias/${btn.dataset.id}/reactivar`);
                            UI.toast('Asistencia reactivada', 'success');
                            this.loadData(profOpts, asigOpts);
                        } catch (err) { UI.toast(err.mensaje || 'Error al reactivar', 'error'); }
                    });
                })
            );
        } catch (err) {
            UI.toast(err.mensaje || 'Error al cargar asistencias', 'error');
        }
    },

    // --- Modal de alta/edicion ---
    // Si item es null, crea nuevo registro con fecha y hora actuales como default
    openForm(item, profOpts, asigOpts) {
        const now = new Date();
        const hoy = now.toISOString().split('T')[0];
        const horaActual = now.toTimeString().substring(0, 5);

        const html = `
            <div class="form-group">
                <label>Profesor</label>
                <select class="form-control" id="field-idProfesor">
                    <option value="">-- Seleccionar --</option>
                    ${profOpts.map(o => `<option value="${o.value}" ${item && o.value === item.idProfesor ? 'selected' : ''}>${o.label}</option>`).join('')}
                </select>
            </div>
            <div class="form-group">
                <label>Cargo (opcional)</label>
                <select class="form-control" id="field-idAsignacion">
                    <option value="">-- Sin cargo --</option>
                    ${asigOpts.map(o => `<option value="${o.value}" ${item && o.value === item.idAsignacion ? 'selected' : ''}>${o.label}</option>`).join('')}
                </select>
            </div>
            <div class="form-group">
                <label>Fecha</label>
                <input type="date" class="form-control" id="field-fecha" value="${item ? item.fecha : hoy}">
            </div>
            <div class="form-row">
                <div class="form-group">
                    <label>Hora Entrada</label>
                    <input type="time" class="form-control" id="field-horaEntrada" value="${item ? (item.horaEntrada || '').substring(0,5) : horaActual}">
                </div>
                <div class="form-group">
                    <label>Hora Salida</label>
                    <input type="time" class="form-control" id="field-horaSalida" value="${item ? (item.horaSalida || '').substring(0,5) : ''}">
                </div>
            </div>
            <div class="form-row">
                <div class="form-group">
                    <label>Estado</label>
                    <select class="form-control" id="field-estado">
                        ${UI.selectOptions('estadoAsistencia', item ? item.estado : 'PRESENTE')}
                    </select>
                </div>
                <div class="form-group">
                    <label>Modo Registro</label>
                    <select class="form-control" id="field-modoRegistro">
                        ${UI.selectOptions('modoRegistro', item ? item.modoRegistro : 'MANUAL')}
                    </select>
                </div>
            </div>
            <div class="form-group">
                <label>Observaciones</label>
                <textarea class="form-control" id="field-observaciones" rows="2" maxlength="500">${item ? (item.observaciones || '') : ''}</textarea>
            </div>`;

        UI.openModal(item ? 'Editar Asistencia' : 'Registrar Asistencia', html, async () => {
            // Validacion: profesor obligatorio
            if (!document.getElementById('field-idProfesor').value) {
                UI.toast('Debe seleccionar un profesor', 'warning');
                return;
            }

            const dto = {
                idProfesor: parseInt(document.getElementById('field-idProfesor').value) || null,
                idAsignacion: parseInt(document.getElementById('field-idAsignacion').value) || null,
                fecha: document.getElementById('field-fecha').value,
                horaEntrada: document.getElementById('field-horaEntrada').value || null,
                horaSalida: document.getElementById('field-horaSalida').value || null,
                estado: document.getElementById('field-estado').value,
                modoRegistro: document.getElementById('field-modoRegistro').value,
                observaciones: document.getElementById('field-observaciones').value || null
            };

            try {
                if (item) {
                    await Api.put(`/asistencias/${item.idAsistencia}`, dto);
                    UI.toast('Asistencia actualizada', 'success');
                } else {
                    await Api.post('/asistencias', dto);
                    UI.toast('Asistencia registrada', 'success');
                }
                UI.closeModal();
                this.loadData(this.profOpts, this.asigOpts);
            } catch (err) {
                const msg = err.detalles?.length ? err.detalles.join(', ') : err.mensaje || 'Error';
                UI.toast(msg, 'error');
            }
        });
    }
};

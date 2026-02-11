const AsistenciasPage = {
    async render() {
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
            <div class="card" style="margin-bottom:20px; display:flex; gap:12px; align-items:flex-end; flex-wrap:wrap;">
                <div class="form-group" style="margin-bottom:0; min-width:180px;">
                    <label>Profesor</label>
                    <select class="form-control" id="filter-profesor">
                        <option value="">Todos</option>
                        ${profOpts.map(o => `<option value="${o.value}">${o.label}</option>`).join('')}
                    </select>
                </div>
                <div class="form-group" style="margin-bottom:0; min-width:140px;">
                    <label>Fecha</label>
                    <input type="date" class="form-control" id="filter-fecha">
                </div>
                <div class="form-group" style="margin-bottom:0; min-width:140px;">
                    <label>Estado</label>
                    <select class="form-control" id="filter-estado">
                        <option value="">Todos</option>
                        <option value="PRESENTE">Presente</option>
                        <option value="AUSENTE">Ausente</option>
                        <option value="TARDANZA">Tardanza</option>
                        <option value="JUSTIFICADO">Justificado</option>
                    </select>
                </div>
                <button class="btn btn-secondary" id="btn-filter">Filtrar</button>
            </div>
            <div class="table-wrapper">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Fecha</th>
                            <th>Profesor</th>
                            <th>Asignacion</th>
                            <th>Entrada</th>
                            <th>Salida</th>
                            <th>Estado</th>
                            <th>Modo</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody id="asist-table"></tbody>
                </table>
            </div>`;

        document.getElementById('btn-new-asist').addEventListener('click', () => this.openForm(null, profOpts, asigOpts));
        document.getElementById('btn-filter').addEventListener('click', () => this.loadData(profOpts, asigOpts));

        this.profOpts = profOpts;
        this.asigOpts = asigOpts;
        this.loadData(profOpts, asigOpts);
    },

    async loadData(profOpts, asigOpts) {
        const tbody = document.getElementById('asist-table');
        tbody.innerHTML = '<tr><td colspan="9" class="table-empty">Cargando...</td></tr>';

        try {
            let data = await Api.get('/asistencias');

            // Apply filters
            const fProf = document.getElementById('filter-profesor').value;
            const fFecha = document.getElementById('filter-fecha').value;
            const fEstado = document.getElementById('filter-estado').value;

            if (fProf) data = data.filter(a => String(a.idProfesor) === fProf);
            if (fFecha) data = data.filter(a => a.fecha === fFecha);
            if (fEstado) data = data.filter(a => a.estado === fEstado);

            if (data.length === 0) {
                tbody.innerHTML = '<tr><td colspan="9" class="table-empty">Sin registros</td></tr>';
                return;
            }

            tbody.innerHTML = data.reverse().map(a => {
                const prof = profOpts.find(o => o.value === a.idProfesor);
                const asig = asigOpts.find(o => o.value === a.idAsignacion);
                return `<tr>
                    <td>${a.idAsistencia}</td>
                    <td>${UI.formatDate(a.fecha)}</td>
                    <td>${prof ? prof.label : '#' + a.idProfesor}</td>
                    <td>${asig ? asig.label : (a.idAsignacion ? '#' + a.idAsignacion : '-')}</td>
                    <td>${UI.formatTime(a.horaEntrada)}</td>
                    <td>${UI.formatTime(a.horaSalida)}</td>
                    <td>${UI.estadoBadge(a.estado)}</td>
                    <td><span class="badge badge-muted">${a.modoRegistro || '-'}</span></td>
                    <td class="table-actions">
                        <button class="btn-icon edit" data-id="${a.idAsistencia}"><i class="bi bi-pencil"></i></button>
                        <button class="btn-icon delete" data-id="${a.idAsistencia}"><i class="bi bi-trash"></i></button>
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
        } catch (err) {
            UI.toast(err.mensaje || 'Error al cargar asistencias', 'error');
        }
    },

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
                <label>Asignacion (opcional)</label>
                <select class="form-control" id="field-idAsignacion">
                    <option value="">-- Sin asignacion --</option>
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
                <textarea class="form-control" id="field-observaciones" rows="2">${item ? (item.observaciones || '') : ''}</textarea>
            </div>`;

        UI.openModal(item ? 'Editar Asistencia' : 'Registrar Asistencia', html, async () => {
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

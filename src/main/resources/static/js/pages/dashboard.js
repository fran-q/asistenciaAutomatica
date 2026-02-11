const DashboardPage = {
    async render() {
        const content = document.getElementById('page-content');
        content.innerHTML = `
            <div class="page-header">
                <h2 class="page-title"><i class="bi bi-grid-1x2"></i> Dashboard</h2>
            </div>
            <div class="stat-grid" id="stats-grid">
                <div class="stat-card"><div class="stat-icon accent"><i class="bi bi-person-badge"></i></div><div class="stat-info"><h3 id="stat-profesores">--</h3><p>Profesores</p></div></div>
                <div class="stat-card"><div class="stat-icon success"><i class="bi bi-mortarboard"></i></div><div class="stat-info"><h3 id="stat-alumnos">--</h3><p>Alumnos</p></div></div>
                <div class="stat-card"><div class="stat-icon info"><i class="bi bi-calendar-check"></i></div><div class="stat-info"><h3 id="stat-asistencias-hoy">--</h3><p>Asistencias hoy</p></div></div>
                <div class="stat-card"><div class="stat-icon danger"><i class="bi bi-person-x"></i></div><div class="stat-info"><h3 id="stat-ausentes">--</h3><p>Ausentes hoy</p></div></div>
            </div>
            <div class="card">
                <h6 style="margin-bottom:14px; color:var(--text-secondary)">Ultimas asistencias registradas</h6>
                <div class="table-wrapper" style="box-shadow:none; border:none;">
                    <table>
                        <thead>
                            <tr>
                                <th>Fecha</th>
                                <th>Profesor</th>
                                <th>Entrada</th>
                                <th>Salida</th>
                                <th>Estado</th>
                                <th>Modo</th>
                            </tr>
                        </thead>
                        <tbody id="recent-asistencias">
                            <tr><td colspan="6" class="table-empty">Cargando...</td></tr>
                        </tbody>
                    </table>
                </div>
            </div>`;

        this.loadStats();
    },

    async loadStats() {
        try {
            const [profesores, alumnos, asistencias, usuarios] = await Promise.all([
                Api.get('/profesores'),
                Api.get('/alumnos'),
                Api.get('/asistencias'),
                Api.get('/usuarios')
            ]);

            document.getElementById('stat-profesores').textContent = profesores.length;
            document.getElementById('stat-alumnos').textContent = alumnos.length;

            const hoy = new Date().toISOString().split('T')[0];
            const asistHoy = asistencias.filter(a => a.fecha === hoy);
            document.getElementById('stat-asistencias-hoy').textContent = asistHoy.length;
            document.getElementById('stat-ausentes').textContent = asistHoy.filter(a => a.estado === 'AUSENTE').length;

            // Recent table - last 10
            const recent = asistencias.slice(-10).reverse();
            const tbody = document.getElementById('recent-asistencias');

            if (recent.length === 0) {
                tbody.innerHTML = '<tr><td colspan="6" class="table-empty">Sin asistencias registradas</td></tr>';
                return;
            }

            // Build professor name map: idProfesor -> user name
            const profMap = {};
            profesores.forEach(p => {
                const user = usuarios.find(u => u.idUsuario === p.idUsuario);
                profMap[p.idProfesor] = user ? `${user.nombre} ${user.apellido}` : `Prof. #${p.idProfesor}`;
            });

            tbody.innerHTML = recent.map(a => `
                <tr>
                    <td>${UI.formatDate(a.fecha)}</td>
                    <td>${profMap[a.idProfesor] || '-'}</td>
                    <td>${UI.formatTime(a.horaEntrada)}</td>
                    <td>${UI.formatTime(a.horaSalida)}</td>
                    <td>${UI.estadoBadge(a.estado)}</td>
                    <td><span class="badge badge-muted">${a.modoRegistro}</span></td>
                </tr>`).join('');

        } catch (err) {
            UI.toast('Error al cargar estadisticas', 'error');
        }
    }
};

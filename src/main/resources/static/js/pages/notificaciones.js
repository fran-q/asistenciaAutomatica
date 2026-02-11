const NotificacionesPage = {
    async render() {
        const [alumnoOpts, asigOpts] = await Promise.all([
            getAlumnosOptions(),
            getAsignacionesOptions()
        ]);

        const tipoOpts = [
            { value: 'ASISTENCIA', label: 'Asistencia' },
            { value: 'INASISTENCIA', label: 'Inasistencia' },
            { value: 'TARDANZA', label: 'Tardanza' },
            { value: 'GENERAL', label: 'General' }
        ];

        const page = createCrudPage({
            title: 'Notificaciones',
            icon: 'bi-bell',
            endpoint: '/notificaciones',
            columns: [
                { label: 'ID', idKey: 'idNotificacion' },
                { label: 'Alumno' },
                { label: 'Tipo' },
                { label: 'Titulo' },
                { label: 'Fecha' },
                { label: 'Estado' }
            ],
            formFields: [
                { key: 'idAlumno', label: 'Alumno', type: 'select', options: alumnoOpts },
                { key: 'tipo', label: 'Tipo', type: 'select', options: tipoOpts },
                { key: 'titulo', label: 'Titulo', placeholder: 'Titulo de la notificacion' },
                { key: 'mensaje', label: 'Mensaje', type: 'textarea' },
                { key: 'idAsignacion', label: 'Asignacion (opcional)', type: 'select', options: asigOpts }
            ],
            formToDto: (d) => ({
                idAlumno: d.idAlumno ? parseInt(d.idAlumno) : null,
                tipo: d.tipo,
                titulo: d.titulo,
                mensaje: d.mensaje,
                idAsistencia: null,
                idAsignacion: d.idAsignacion ? parseInt(d.idAsignacion) : null
            }),
            mapRow: (n) => {
                const alumno = alumnoOpts.find(o => o.value === n.idAlumno);
                const tipoBadgeMap = {
                    'ASISTENCIA': 'badge-success',
                    'INASISTENCIA': 'badge-danger',
                    'TARDANZA': 'badge-warning',
                    'GENERAL': 'badge-info'
                };
                return [
                    n.idNotificacion,
                    alumno ? alumno.label : `#${n.idAlumno}`,
                    `<span class="badge ${tipoBadgeMap[n.tipo] || 'badge-muted'}">${n.tipo}</span>`,
                    n.titulo || '-',
                    n.fechaCreacion ? UI.formatDate(n.fechaCreacion.split('T')[0]) : '-',
                    UI.activoBadge(n.activo)
                ];
            },
            searchFilter: (n, s) => {
                const alumno = alumnoOpts.find(o => o.value === n.idAlumno);
                return `${alumno?.label} ${n.titulo} ${n.tipo}`.toLowerCase().includes(s);
            }
        });
        page.render();
    }
};

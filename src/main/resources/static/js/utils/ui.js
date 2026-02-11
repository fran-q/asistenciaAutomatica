const UI = {
    // Toast notifications
    toast(message, type = 'info') {
        const container = document.getElementById('toast-container');
        const icons = {
            success: 'bi-check-circle-fill',
            error: 'bi-x-circle-fill',
            warning: 'bi-exclamation-triangle-fill',
            info: 'bi-info-circle-fill'
        };
        const toast = document.createElement('div');
        toast.className = `toast toast-border-${type} ${type}`;
        toast.innerHTML = `<i class="bi ${icons[type] || icons.info}"></i><span>${message}</span>`;
        container.appendChild(toast);
        setTimeout(() => {
            toast.classList.add('toast-out');
            setTimeout(() => toast.remove(), 300);
        }, 3500);
    },

    // Modal
    openModal(title, bodyHtml, onSave) {
        document.getElementById('modal-title').textContent = title;
        document.getElementById('modal-body').innerHTML = bodyHtml;
        document.getElementById('modal-overlay').style.display = 'flex';

        const saveBtn = document.getElementById('modal-save');
        const newSave = saveBtn.cloneNode(true);
        saveBtn.parentNode.replaceChild(newSave, saveBtn);
        newSave.id = 'modal-save';
        newSave.addEventListener('click', onSave);
    },

    closeModal() {
        document.getElementById('modal-overlay').style.display = 'none';
    },

    // Confirm dialog
    confirm(message, onConfirm) {
        document.getElementById('confirm-message').textContent = message;
        document.getElementById('confirm-overlay').style.display = 'flex';

        const okBtn = document.getElementById('confirm-ok');
        const newOk = okBtn.cloneNode(true);
        okBtn.parentNode.replaceChild(newOk, okBtn);
        newOk.id = 'confirm-ok';
        newOk.addEventListener('click', () => {
            this.closeConfirm();
            onConfirm();
        });
    },

    closeConfirm() {
        document.getElementById('confirm-overlay').style.display = 'none';
    },

    // Breadcrumb
    setBreadcrumb(items) {
        const bc = document.getElementById('breadcrumb');
        bc.innerHTML = items.map((item, i) => {
            const isLast = i === items.length - 1;
            return (i > 0 ? '<span class="breadcrumb-sep">/</span>' : '') +
                `<span class="breadcrumb-item">${item}</span>`;
        }).join('');
    },

    // Loading state
    showLoading() {
        document.getElementById('page-content').innerHTML =
            '<div class="page-loading"><div class="spinner"></div> Cargando...</div>';
    },

    // Format helpers
    formatDate(dateStr) {
        if (!dateStr) return '-';
        const [y, m, d] = dateStr.split('-');
        return `${d}/${m}/${y}`;
    },

    formatTime(timeStr) {
        if (!timeStr) return '-';
        return timeStr.substring(0, 5);
    },

    estadoBadge(estado) {
        const map = {
            'PRESENTE': 'badge-success',
            'AUSENTE': 'badge-danger',
            'TARDANZA': 'badge-warning',
            'JUSTIFICADO': 'badge-info'
        };
        return `<span class="badge ${map[estado] || 'badge-muted'}">${estado}</span>`;
    },

    rolBadge(rol) {
        const map = {
            'ADMIN': 'badge-accent',
            'PROFESOR': 'badge-info',
            'ALUMNO': 'badge-success'
        };
        return `<span class="badge ${map[rol] || 'badge-muted'}">${rol}</span>`;
    },

    activoBadge(activo) {
        return activo
            ? '<span class="badge badge-success">Activo</span>'
            : '<span class="badge badge-danger">Inactivo</span>';
    },

    // Enum labels
    enumLabels: {
        genero: { MASCULINO: 'Masculino', FEMENINO: 'Femenino', OTRO: 'Otro', NO_ESPECIFICA: 'No especifica' },
        tipoDocumento: { DNI: 'DNI', PASAPORTE: 'Pasaporte', CEDULA: 'Cedula', OTRO: 'Otro' },
        turno: { MANIANA: 'Maniana', TARDE: 'Tarde', NOCHE: 'Noche' },
        categoriaProfesor: { TITULAR: 'Titular', ADJUNTO: 'Adjunto', SUPLENTE: 'Suplente', INTERINO: 'Interino' },
        estadoAsistencia: { PRESENTE: 'Presente', AUSENTE: 'Ausente', TARDANZA: 'Tardanza', JUSTIFICADO: 'Justificado' },
        modoRegistro: { FACIAL: 'Facial', MANUAL: 'Manual' },
        diaSemana: { LUNES: 'Lunes', MARTES: 'Martes', MIERCOLES: 'Miercoles', JUEVES: 'Jueves', VIERNES: 'Viernes', SABADO: 'Sabado' },
        rol: { ADMIN: 'Admin', PROFESOR: 'Profesor', ALUMNO: 'Alumno' }
    },

    selectOptions(enumName, selected = '') {
        const entries = this.enumLabels[enumName];
        if (!entries) return '';
        return Object.entries(entries).map(([val, label]) =>
            `<option value="${val}" ${val === selected ? 'selected' : ''}>${label}</option>`
        ).join('');
    }
};

// Modal/Confirm close handlers
document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.modal-close').forEach(btn =>
        btn.addEventListener('click', () => UI.closeModal())
    );
    document.querySelectorAll('.confirm-close').forEach(btn =>
        btn.addEventListener('click', () => UI.closeConfirm())
    );
    document.getElementById('modal-overlay').addEventListener('click', (e) => {
        if (e.target === e.currentTarget) UI.closeModal();
    });
    document.getElementById('confirm-overlay').addEventListener('click', (e) => {
        if (e.target === e.currentTarget) UI.closeConfirm();
    });
});

// --- Utilidades de interfaz: toasts, modales, confirmaciones, formatos ---
const UI = {
    // --- Toasts de notificacion (usa .notif para evitar conflicto con Bootstrap .toast) ---
    toast(message, type = 'info') {
        const container = document.getElementById('notif-container');
        const icons = {
            success: 'bi-check-circle-fill',
            error: 'bi-x-circle-fill',
            warning: 'bi-exclamation-triangle-fill',
            info: 'bi-info-circle-fill'
        };
        const notif = document.createElement('div');
        notif.className = `notif ${type}`;
        notif.innerHTML = `<i class="bi ${icons[type] || icons.info}"></i><span>${message}</span><button class="notif-close"><i class="bi bi-x-lg"></i></button>`;

        let timer;
        const dismiss = () => {
            clearTimeout(timer);
            if (notif.parentNode) {
                notif.classList.add('notif-out');
                setTimeout(() => notif.remove(), 350);
            }
        };

        notif.querySelector('.notif-close').addEventListener('click', dismiss);
        container.appendChild(notif);

        timer = setTimeout(dismiss, 20000); // Autodescartar tras 20 segundos
    },

    // Muestra errores de validacion: 1-2 individuales, 3+ mensaje generico
    showValidationErrors(errors) {
        if (errors.length === 0) return;
        if (errors.length <= 2) {
            errors.forEach(e => UI.toast(e, 'error'));
        } else {
            UI.toast('Hay campos obligatorios sin completar o con formato invalido. Revisa el formulario antes de confirmar.', 'error');
        }
    },

    // --- Modal generico (titulo, contenido HTML, callback al guardar) ---
    openModal(title, bodyHtml, onSave) {
        document.getElementById('modal-title').textContent = title;
        document.getElementById('modal-body').innerHTML = bodyHtml;
        document.getElementById('modal-overlay').style.display = 'flex';

        // Clonar boton para eliminar listeners anteriores
        const saveBtn = document.getElementById('modal-save');
        const newSave = saveBtn.cloneNode(true);
        saveBtn.parentNode.replaceChild(newSave, saveBtn);
        newSave.id = 'modal-save';
        newSave.addEventListener('click', onSave);
    },

    closeModal() {
        document.getElementById('modal-overlay').style.display = 'none';
    },

    // --- Dialogo de confirmacion ---
    confirm(message, onConfirm) {
        document.getElementById('confirm-message').textContent = message;
        document.getElementById('confirm-overlay').style.display = 'flex';

        const okBtn = document.getElementById('confirm-ok');

        // Clonar boton OK para eliminar event listeners anteriores
        const newOk = okBtn.cloneNode(true);
        okBtn.parentNode.replaceChild(newOk, okBtn);
        newOk.id = 'confirm-ok';

        // Clonar TODOS los botones .confirm-close (Cancelar y X)
        const closeBtns = document.querySelectorAll('#confirm-overlay .confirm-close');
        const newCloseBtns = [];
        closeBtns.forEach(btn => {
            const newBtn = btn.cloneNode(true);
            btn.parentNode.replaceChild(newBtn, btn);
            newCloseBtns.push(newBtn);
        });

        // Si se pasa callback (patron clasico): usarlo directamente
        if (typeof onConfirm === 'function') {
            newOk.addEventListener('click', () => {
                this.closeConfirm();
                onConfirm();
            });
            newCloseBtns.forEach(btn => {
                btn.addEventListener('click', () => this.closeConfirm());
            });
            return;
        }

        // Si no se pasa callback: retornar Promise (patron async/await)
        return new Promise((resolve) => {
            newOk.addEventListener('click', () => {
                this.closeConfirm();
                resolve(true);
            });
            newCloseBtns.forEach(btn => {
                btn.addEventListener('click', () => {
                    this.closeConfirm();
                    resolve(false);
                });
            });
        });
    },

    closeConfirm() {
        document.getElementById('confirm-overlay').style.display = 'none';
    },

    // --- Breadcrumb (migas de pan) ---
    setBreadcrumb(items) {
        const bc = document.getElementById('breadcrumb');
        bc.innerHTML = items.map((item, i) => {
            const isLast = i === items.length - 1;
            return (i > 0 ? '<span class="breadcrumb-sep">/</span>' : '') +
                `<span class="breadcrumb-item">${item}</span>`;
        }).join('');
    },

    // --- Estado de carga (spinner) ---
    showLoading() {
        document.getElementById('page-content').innerHTML =
            '<div class="page-loading"><div class="spinner"></div> Cargando...</div>';
    },

    // --- Formateadores de fecha y hora ---
    formatDate(dateStr) {
        if (!dateStr) return '-';
        const [y, m, d] = dateStr.split('-');
        return `${d}/${m}/${y}`;
    },

    formatTime(timeStr) {
        if (!timeStr) return '-';
        return timeStr.substring(0, 5); // Recorta segundos (HH:mm)
    },

    // --- Badges de estado, rol y activo ---
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

    // --- Etiquetas legibles para enums del backend ---
    enumLabels: {
        genero: { MASCULINO: 'Masculino', FEMENINO: 'Femenino', OTRO: 'Otro' },
        tipoDocumento: { DNI: 'DNI', PASAPORTE: 'Pasaporte', CEDULA: 'Cedula', OTRO: 'Otro' },
        turno: { MANIANA: 'Maniana', TARDE: 'Tarde', NOCHE: 'Noche' },
        categoriaProfesor: { TITULAR: 'Titular', ADJUNTO: 'Adjunto', SUPLENTE: 'Suplente', INTERINO: 'Interino' },
        estadoAsistencia: { PRESENTE: 'Presente', AUSENTE: 'Ausente', TARDANZA: 'Tardanza', JUSTIFICADO: 'Justificado' },
        modoRegistro: { FACIAL: 'Facial', MANUAL: 'Manual' },
        diaSemana: { LUNES: 'Lunes', MARTES: 'Martes', MIERCOLES: 'Miercoles', JUEVES: 'Jueves', VIERNES: 'Viernes', SABADO: 'Sabado' },
        rol: { ADMIN: 'Admin', PROFESOR: 'Profesor', ALUMNO: 'Alumno' }
    },

    // Genera <option> HTML a partir de un enum
    selectOptions(enumName, selected = '') {
        const entries = this.enumLabels[enumName];
        if (!entries) return '';
        return Object.entries(entries).map(([val, label]) =>
            `<option value="${val}" ${val === selected ? 'selected' : ''}>${label}</option>`
        ).join('');
    },

    // --- Reglas de validacion por tipo de documento ---
    docRules: {
        DNI:       { pattern: '^\\d{7,8}$',      maxlength: 8,  placeholder: 'Ej: 12345678', hint: '7 u 8 digitos' },
        PASAPORTE: { pattern: '^[A-Z]{3}\\d{6}$', maxlength: 9,  placeholder: 'Ej: AAB123456', hint: '3 letras + 6 digitos' },
        CEDULA:    { pattern: '^\\d{6,10}$',       maxlength: 10, placeholder: 'Ej: 1234567', hint: '6 a 10 digitos' },
        OTRO:      { pattern: '.{6,20}',           maxlength: 20, placeholder: 'Nro. documento', hint: '6 a 20 caracteres' }
    },

    // --- Restringe input a solo numeros (o alfanumerico si el tipo de doc lo requiere) ---
    bindNumericOnly(inputId, docTypeId) {
        const input = document.getElementById(inputId);
        if (!input) return;

        input.addEventListener('input', () => {
            let allowLetters = false;
            if (docTypeId) {
                const tipo = document.getElementById(docTypeId)?.value;
                allowLetters = tipo === 'PASAPORTE' || tipo === 'OTRO';
            }
            if (allowLetters) {
                input.value = input.value.replace(/[^a-zA-Z0-9]/g, '');
            } else {
                input.value = input.value.replace(/\D/g, '');
            }
        });

        // Also prevent non-numeric key presses for immediate feedback
        input.addEventListener('keypress', (e) => {
            let allowLetters = false;
            if (docTypeId) {
                const tipo = document.getElementById(docTypeId)?.value;
                allowLetters = tipo === 'PASAPORTE' || tipo === 'OTRO';
            }
            if (allowLetters) {
                if (!/[a-zA-Z0-9]/.test(e.key)) e.preventDefault();
            } else {
                if (!/\d/.test(e.key)) e.preventDefault();
            }
        });
    },

    // No-op: password toggles are now handled via global event delegation
    bindPasswordToggles() {},

    // --- Actualiza reglas del campo nro. documento al cambiar tipo de documento ---
    bindDocTypeChange(tipoDocId, numDocId) {
        const tipoSel = document.getElementById(tipoDocId);
        const numDoc = document.getElementById(numDocId);
        if (!tipoSel || !numDoc) return;

        const updateDocField = () => {
            const tipo = tipoSel.value;
            const rules = UI.docRules[tipo] || UI.docRules.OTRO;
            numDoc.setAttribute('maxlength', rules.maxlength);
            numDoc.setAttribute('pattern', rules.pattern);
            numDoc.setAttribute('placeholder', rules.placeholder);
            // Update hint
            let hint = numDoc.parentElement.querySelector('.form-hint');
            if (!hint) {
                hint = document.createElement('small');
                hint.className = 'form-hint';
                numDoc.parentElement.appendChild(hint);
            }
            hint.textContent = tipo ? rules.hint : '';
        };

        tipoSel.addEventListener('change', updateDocField);
        updateDocField(); // run on init
    },

    // --- Validacion de formulario de persona (retorna array de errores) ---
    validatePersonForm(fields) {
        const errors = [];
        const v = (id) => (document.getElementById(id)?.value || '').trim();

        if (!v(fields.nombre) || v(fields.nombre).length < 2)
            errors.push('El nombre es obligatorio (min. 2 caracteres)');
        if (!/^[a-zA-ZáéíóúüñÁÉÍÓÚÜÑ'\- ]+$/.test(v(fields.nombre)) && v(fields.nombre))
            errors.push('El nombre solo puede contener letras, espacios y guiones');

        if (!v(fields.apellido) || v(fields.apellido).length < 2)
            errors.push('El apellido es obligatorio (min. 2 caracteres)');

        if (!v(fields.email))
            errors.push('El email es obligatorio');
        else if (!/^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/.test(v(fields.email)))
            errors.push('El formato del email no es valido');

        if (fields.contrasena) {
            const pass = v(fields.contrasena);
            const needsPass = fields.requirePass; // true on create
            if (needsPass && !pass) {
                errors.push('La contrasena es obligatoria');
            } else if (pass) {
                if (pass.length < 8)
                    errors.push('La contrasena debe tener al menos 8 caracteres');
                if (!/[A-Z]/.test(pass))
                    errors.push('La contrasena debe contener al menos una letra mayuscula');
                if (!/[a-z]/.test(pass))
                    errors.push('La contrasena debe contener al menos una letra minuscula');
                if (!/\d/.test(pass))
                    errors.push('La contrasena debe contener al menos un numero');
                if (!/[@$!%*?&#+\-_.]/.test(pass))
                    errors.push('La contrasena debe contener al menos un caracter especial (@$!%*?&#+-._ )');
            }
            // Confirm password match
            if (fields.confirmarContrasena) {
                const confirm = v(fields.confirmarContrasena);
                if (needsPass && !confirm) {
                    errors.push('Debe confirmar la contrasena');
                } else if (pass && pass !== confirm) {
                    errors.push('Las contrasenas no coinciden');
                }
            }
        }

        if (!v(fields.tipoDocumento))
            errors.push('El tipo de documento es obligatorio');

        const tipoDoc = v(fields.tipoDocumento);
        const numDoc = v(fields.numeroDocumento);
        if (!numDoc) {
            errors.push('El numero de documento es obligatorio');
        } else if (tipoDoc) {
            const rules = UI.docRules[tipoDoc] || UI.docRules.OTRO;
            if (!new RegExp(rules.pattern).test(numDoc))
                errors.push(`Formato de documento invalido para ${tipoDoc}: ${rules.hint}`);
        }

        if (!v(fields.genero))
            errors.push('El genero es obligatorio');

        if (fields.idInstitucion && !v(fields.idInstitucion))
            errors.push('La institucion es obligatoria');

        if (fields.telefono && v(fields.telefono) && !/^\d{10}$/.test(v(fields.telefono)))
            errors.push('El telefono debe tener exactamente 10 digitos (sin 0 ni 15)');

        return errors;
    }
};

// --- Inicializacion global: cierre de modales, toggle de contrasena, maxlength ---
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

    // Delegacion global: toggle visibilidad de contrasena (funciona con elementos futuros)
    document.addEventListener('click', (e) => {
        const btn = e.target.closest('.password-toggle');
        if (!btn) return;
        e.preventDefault();
        e.stopPropagation();
        const wrapper = btn.closest('.password-wrapper');
        if (!wrapper) return;
        const input = wrapper.querySelector('input');
        if (!input) return;
        const isPassword = input.type === 'password';
        input.type = isPassword ? 'text' : 'password';
        const icon = btn.querySelector('i');
        if (icon) icon.className = isPassword ? 'bi bi-eye' : 'bi bi-eye-slash';
    });

    // Forzar maxlength en inputs (recorta texto pegado que exceda el limite)
    document.addEventListener('input', (e) => {
        const input = e.target;
        if (input.tagName !== 'INPUT' && input.tagName !== 'TEXTAREA') return;
        const max = parseInt(input.getAttribute('maxlength'));
        if (!max || isNaN(max)) return;
        if (input.value.length > max) {
            input.value = input.value.slice(0, max);
        }
    });
});

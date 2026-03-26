// --- Modulo de autenticacion: login, registro, gestion de sesion ---
const Auth = {
    // --- Gestion de token y sesion (localStorage) ---
    getToken() {
        return localStorage.getItem('auth_token');
    },
    getUser() {
        const u = localStorage.getItem('auth_user');
        return u ? JSON.parse(u) : null;
    },
    isLoggedIn() {
        return !!this.getToken();
    },
    saveSession(token, usuario) {
        localStorage.setItem('auth_token', token);
        localStorage.setItem('auth_user', JSON.stringify(usuario));
    },
    clearSession() {
        localStorage.removeItem('auth_token');
        localStorage.removeItem('auth_user');
    },
    logout() {
        // Detener camaras y websockets activos (evita memory leak)
        if (typeof _cameraActive !== 'undefined' && _cameraActive) {
            if (typeof ReconocimientoPage !== 'undefined') {
                ReconocimientoPage.stopCamera();
            }
        }
        if (typeof EnrolamientoPage !== 'undefined' && EnrolamientoPage._stream) {
            EnrolamientoPage.stopCamera();
        }
        this.clearSession();
        this.showAuthScreen();
    },

    // Valida token contra el backend (/auth/me)
    async validate() {
        if (!this.isLoggedIn()) return false;
        try {
            const user = await Api.get('/auth/me');
            localStorage.setItem('auth_user', JSON.stringify(user));
            return true;
        } catch {
            this.clearSession();
            return false;
        }
    },

    // Ingresa a la app tras login/registro exitoso (sin re-validar token)
    _enterApp() {
        this.showApp();
        if (!App._initialized) {
            App.initTheme();
            App.initSidebar();
            App.initNavigation();
            App.initLogout();
            App._initialized = true;
        }
        App.navigate('dashboard');
    },

    // --- Control de visibilidad: pantalla de auth vs app principal ---
    showAuthScreen() {
        document.getElementById('auth-screen').style.display = 'flex';
        document.getElementById('sidebar').style.display = 'none';
        document.getElementById('main-content').style.display = 'none';
        this.renderLogin();
    },
    showApp() {
        document.getElementById('auth-screen').style.display = 'none';
        document.getElementById('sidebar').style.display = '';
        document.getElementById('main-content').style.display = '';
        // Update topbar user info
        const user = this.getUser();
        if (user) {
            document.getElementById('user-display-name').textContent = `Bienvenido, ${user.nombre} ${user.apellido}`;
        }
    },

    // --- Transicion animada entre pantallas de auth (slide izq/der) ---
    _setAuthContent(html, direction) {
        const container = document.getElementById('auth-screen');
        const existing = container.querySelector('.auth-card, .auth-card-wide');

        if (existing && direction) {
            // Animate out
            existing.classList.add('auth-slide-out-' + (direction === 'forward' ? 'left' : 'right'));
            setTimeout(() => {
                container.innerHTML = html;
                const card = container.querySelector('.auth-card, .auth-card-wide');
                if (card) {
                    card.classList.add('auth-slide-in-' + (direction === 'forward' ? 'right' : 'left'));
                    card.addEventListener('animationend', () => {
                        card.classList.remove('auth-slide-in-right', 'auth-slide-in-left');
                    }, { once: true });
                }
            }, 200);
        } else {
            // No animation (first render) or same-screen re-render
            container.innerHTML = html;
            const card = container.querySelector('.auth-card, .auth-card-wide');
            if (card) {
                card.classList.add('auth-fade-in');
                card.addEventListener('animationend', () => {
                    card.classList.remove('auth-fade-in');
                }, { once: true });
            }
        }
    },

    // --- Formulario de login ---
    renderLogin(direction) {
        const html = `
            <div class="auth-card">
                <div class="auth-header">
                    <i class="bi bi-clipboard-check auth-logo"></i>
                    <h2>Sistema de Asistencia</h2>
                    <p class="auth-subtitle">Ingresa a tu cuenta para continuar</p>
                </div>
                <div class="auth-tabs">
                    <button class="auth-tab active" id="tab-login">Iniciar Sesion</button>
                    <button class="auth-tab" id="tab-register">Registrarse</button>
                </div>
                <form id="login-form" class="auth-form">
                    <div class="form-group">
                        <label>Email</label>
                        <input type="email" class="form-control" id="login-email" placeholder="ejemplo@correo.com" required maxlength="254">
                    </div>
                    <div class="form-group">
                        <label>Contrasena</label>
                        <div class="password-wrapper">
                            <input type="password" class="form-control" id="login-pass" placeholder="Tu contrasena" required maxlength="64">
                            <button type="button" class="password-toggle" tabindex="-1"><i class="bi bi-eye-slash"></i></button>
                        </div>
                    </div>
                    <button type="submit" class="btn btn-primary auth-btn">
                        <i class="bi bi-box-arrow-in-right"></i> Ingresar
                    </button>
                </form>
            </div>`;

        this._setAuthContent(html, direction);

        const delay = direction ? 210 : 10;
        setTimeout(() => {
            document.getElementById('login-form')?.addEventListener('submit', (e) => {
                e.preventDefault();
                this.handleLogin();
            });
            document.getElementById('tab-register')?.addEventListener('click', () => this.renderRegister('forward'));
            UI.bindPasswordToggles();
        }, delay);
    },

    // --- Llamada API de login ---
    async handleLogin() {
        const email = document.getElementById('login-email').value.trim();
        const pass = document.getElementById('login-pass').value;
        if (!email || !pass) { UI.toast('Completa todos los campos', 'warning'); return; }

        const btn = document.querySelector('.auth-btn');
        btn.disabled = true;
        btn.innerHTML = '<i class="bi bi-hourglass-split"></i> Ingresando...';

        try {
            const res = await Api.request('/auth/login', {
                method: 'POST',
                body: JSON.stringify({ email, contrasena: pass })
            });
            this.saveSession(res.token, res.usuario);
            this._enterApp();
        } catch (err) {
            // Si el error es por email no verificado, redirigir a verificacion
            if (err.mensaje && err.mensaje.includes('verificar tu email')) {
                this._regEmail = email;
                UI.toast('Tu email no esta verificado. Ingresa el codigo enviado a tu correo.', 'warning');
                this._renderVerificacion('forward');
                return;
            }
            UI.toast(err.mensaje || 'Error al iniciar sesion', 'error');
            btn.disabled = false;
            btn.innerHTML = '<i class="bi bi-box-arrow-in-right"></i> Ingresar';
        }
    },

    // --- Registro de administrador (2 pasos + verificacion email) ---
    _regInstituciones: [], // Instituciones disponibles para seleccionar
    _regData: {},          // Datos acumulados entre pasos
    _regConfirm: '',       // Confirmacion de contrasena
    _regEmail: '',         // Email para verificacion post-registro

    renderRegister(direction) {
        this._loadInstituciones().then(() => this._renderRegStep1(direction));
    },

    async _loadInstituciones() {
        try {
            this._regInstituciones = await Api.request('/auth/instituciones');
        } catch { this._regInstituciones = []; }
    },

    // --- Paso 1: datos personales (nombre, email, documento, contrasena) ---
    _renderRegStep1(direction) {
        const d = this._regData;

        const html = `
            <div class="auth-card auth-card-wide">
                <div class="auth-header">
                    <i class="bi bi-clipboard-check auth-logo"></i>
                    <h2>Crear cuenta de Administrador</h2>
                    <p class="auth-subtitle">Paso 1 de 2 - Datos personales</p>
                </div>
                <form id="reg-form-1" class="auth-form">
                    <div class="form-row">
                        <div class="form-group">
                            <label>Nombre *</label>
                            <input type="text" class="form-control" id="r-nombre" value="${d.nombre || ''}" placeholder="Ej: Juan Carlos" minlength="2" maxlength="50">
                        </div>
                        <div class="form-group">
                            <label>Apellido *</label>
                            <input type="text" class="form-control" id="r-apellido" value="${d.apellido || ''}" placeholder="Ej: Garcia Lopez" minlength="2" maxlength="64">
                        </div>
                    </div>
                    <div class="form-group">
                        <label>Email *</label>
                        <input type="email" class="form-control" id="r-email" value="${d.email || ''}" placeholder="ejemplo@correo.com" maxlength="254">
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label>Contrasena *</label>
                            <div class="password-wrapper">
                                <input type="password" class="form-control" id="r-contrasena" value="${d.contrasena || ''}" placeholder="Min. 8 caracteres" minlength="8" maxlength="64">
                                <button type="button" class="password-toggle" tabindex="-1"><i class="bi bi-eye-slash"></i></button>
                            </div>
                            <small class="form-hint">Min. 8, 1 mayuscula, 1 minuscula, 1 numero, 1 especial</small>
                        </div>
                        <div class="form-group">
                            <label>Confirmar Contrasena *</label>
                            <div class="password-wrapper">
                                <input type="password" class="form-control" id="r-confirmar" value="${this._regConfirm || ''}" placeholder="Repetir contrasena" maxlength="64">
                                <button type="button" class="password-toggle" tabindex="-1"><i class="bi bi-eye-slash"></i></button>
                            </div>
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label>Tipo Documento *</label>
                            <select class="form-control" id="r-tipoDoc">
                                <option value="">-- Seleccionar --</option>
                                ${UI.selectOptions('tipoDocumento', d.tipoDocumento || '')}
                            </select>
                        </div>
                        <div class="form-group">
                            <label>Nro. Documento *</label>
                            <input type="text" class="form-control" id="r-numDoc" value="${d.numeroDocumento || ''}" placeholder="12345678" maxlength="20">
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label>Genero *</label>
                            <select class="form-control" id="r-genero">
                                <option value="">-- Seleccionar --</option>
                                ${UI.selectOptions('genero', d.genero || '')}
                            </select>
                        </div>
                        <div class="form-group">
                            <label>Telefono</label>
                            <input type="text" class="form-control" id="r-telefono" value="${d.telefono || ''}" placeholder="1134567890" maxlength="10">
                            <small class="form-hint">10 digitos sin 0 ni 15</small>
                        </div>
                    </div>
                    <div class="form-group">
                        <label>Direccion</label>
                        <input type="text" class="form-control" id="r-direccion" value="${d.direccion || ''}" placeholder="Ej: Av. Corrientes 1234" maxlength="255">
                    </div>
                    <div class="auth-actions">
                        <button type="button" class="btn btn-secondary" id="reg-back-login">
                            <i class="bi bi-arrow-left"></i> Volver
                        </button>
                        <button type="submit" class="btn btn-primary">
                            Siguiente <i class="bi bi-arrow-right"></i>
                        </button>
                    </div>
                </form>
            </div>`;

        this._setAuthContent(html, direction);

        const delay = direction ? 210 : 10;
        setTimeout(() => {
            document.getElementById('reg-back-login')?.addEventListener('click', () => {
                this._saveRegStep1Fields();
                this.renderLogin('back');
            });
            document.getElementById('reg-form-1')?.addEventListener('submit', (e) => {
                e.preventDefault();
                this._handleRegStep1();
            });
            UI.bindPasswordToggles();
            UI.bindDocTypeChange('r-tipoDoc', 'r-numDoc');
            UI.bindNumericOnly('r-numDoc', 'r-tipoDoc');
            UI.bindNumericOnly('r-telefono');
        }, delay);
    },

    // Guarda campos del paso 1 sin validar (para navegacion atras)
    _saveRegStep1Fields() {
        this._regData = {
            nombre: document.getElementById('r-nombre')?.value.trim() || '',
            apellido: document.getElementById('r-apellido')?.value.trim() || '',
            email: document.getElementById('r-email')?.value.trim() || '',
            contrasena: document.getElementById('r-contrasena')?.value || '',
            tipoDocumento: document.getElementById('r-tipoDoc')?.value || '',
            numeroDocumento: document.getElementById('r-numDoc')?.value.trim() || '',
            genero: document.getElementById('r-genero')?.value || '',
            telefono: document.getElementById('r-telefono')?.value.trim() || '',
            direccion: document.getElementById('r-direccion')?.value.trim() || '',
            rol: 'ADMIN',
            idInstitucion: this._regData.idInstitucion || null
        };
        this._regConfirm = document.getElementById('r-confirmar')?.value || '';
    },

    // Valida paso 1 y avanza al paso 2
    _handleRegStep1() {
        const errors = UI.validatePersonForm({
            nombre: 'r-nombre', apellido: 'r-apellido', email: 'r-email',
            contrasena: 'r-contrasena', confirmarContrasena: 'r-confirmar', requirePass: true,
            tipoDocumento: 'r-tipoDoc', numeroDocumento: 'r-numDoc',
            genero: 'r-genero', telefono: 'r-telefono'
        });
        if (errors.length > 0) { UI.showValidationErrors(errors); return; }

        this._saveRegStep1Fields();
        this._renderRegStep2('forward');
    },

    // --- Paso 2: seleccion o creacion de institucion ---
    _renderRegStep2(direction) {
        const instOptions = this._regInstituciones
            .filter(i => i.activo !== false)
            .map(i => `<option value="${i.idInstitucion}">${i.nombre}</option>`)
            .join('');

        const selectedInst = this._regData.idInstitucion || '';

        const html = `
            <div class="auth-card">
                <div class="auth-header">
                    <i class="bi bi-clipboard-check auth-logo"></i>
                    <h2>Crear cuenta de Administrador</h2>
                    <p class="auth-subtitle">Paso 2 de 2 - Seleccionar institucion</p>
                </div>
                <form id="reg-form-2" class="auth-form">
                    <div class="form-group">
                        <label>Institucion *</label>
                        <select class="form-control" id="r-institucion">
                            <option value="">-- Seleccionar --</option>
                            ${instOptions}
                        </select>
                    </div>
                    <p class="auth-hint">No encuentras tu institucion?
                        <a href="#" id="btn-new-inst">Crear una nueva</a>
                    </p>
                    <div id="new-inst-form" style="display:none;" class="new-inst-section">
                        <h6 class="auth-section-title">Nueva Institucion</h6>
                        <div class="form-group">
                            <label>Nombre *</label>
                            <input type="text" class="form-control" id="ni-nombre" placeholder="Nombre de la institucion" maxlength="255">
                        </div>
                        <div class="form-group">
                            <label>Direccion</label>
                            <input type="text" class="form-control" id="ni-direccion" placeholder="Direccion" maxlength="255">
                        </div>
                        <div class="form-row">
                            <div class="form-group">
                                <label>Telefono</label>
                                <input type="text" class="form-control" id="ni-telefono" placeholder="Telefono" maxlength="10">
                            </div>
                            <div class="form-group">
                                <label>Email</label>
                                <input type="email" class="form-control" id="ni-email" placeholder="Email institucional" maxlength="254">
                            </div>
                        </div>
                        <button type="button" class="btn btn-secondary btn-sm" id="btn-save-inst">
                            <i class="bi bi-plus-lg"></i> Crear Institucion
                        </button>
                    </div>
                    <div class="auth-actions">
                        <button type="button" class="btn btn-secondary" id="reg-back-step1">
                            <i class="bi bi-arrow-left"></i> Atras
                        </button>
                        <button type="submit" class="btn btn-primary auth-btn">
                            <i class="bi bi-person-plus"></i> Registrarse
                        </button>
                    </div>
                </form>
            </div>`;

        this._setAuthContent(html, direction);

        const delay = direction ? 210 : 10;
        setTimeout(() => {
            // Restore selected institution
            if (selectedInst) {
                const sel = document.getElementById('r-institucion');
                if (sel) sel.value = selectedInst;
            }
            document.getElementById('reg-back-step1')?.addEventListener('click', () => this._renderRegStep1('back'));
            document.getElementById('btn-new-inst')?.addEventListener('click', (e) => {
                e.preventDefault();
                const form = document.getElementById('new-inst-form');
                if (form) {
                    form.style.display = 'block';
                    form.classList.add('auth-fade-in');
                }
            });
            document.getElementById('btn-save-inst')?.addEventListener('click', () => this._createInstitucion());
            document.getElementById('reg-form-2')?.addEventListener('submit', (e) => {
                e.preventDefault();
                this._handleRegStep2();
            });
            UI.bindNumericOnly('ni-telefono');
        }, delay);
    },

    // Crea institucion nueva desde el formulario de registro
    async _createInstitucion() {
        const nombre = document.getElementById('ni-nombre').value.trim();
        if (!nombre) { UI.toast('El nombre de la institucion es obligatorio', 'warning'); return; }

        const btn = document.getElementById('btn-save-inst');
        btn.disabled = true;
        btn.innerHTML = '<i class="bi bi-hourglass-split"></i> Creando...';

        try {
            const inst = await Api.request('/auth/register-institucion', {
                method: 'POST',
                body: JSON.stringify({
                    nombre,
                    direccion: document.getElementById('ni-direccion').value.trim() || null,
                    telefono: document.getElementById('ni-telefono').value.trim() || null,
                    email: document.getElementById('ni-email').value.trim() || null
                })
            });
            this._regInstituciones.push(inst);
            UI.toast('Institucion creada', 'success');
            this._regData.idInstitucion = inst.idInstitucion;
            this._renderRegStep2(null);
            setTimeout(() => {
                const sel = document.getElementById('r-institucion');
                if (sel) sel.value = inst.idInstitucion;
            }, 20);
        } catch (err) {
            UI.toast(err.mensaje || 'Error al crear institucion', 'error');
            btn.disabled = false;
            btn.innerHTML = '<i class="bi bi-plus-lg"></i> Crear Institucion';
        }
    },

    // Envia registro completo al backend -> muestra pantalla de verificacion
    _submitting: false, // Guard contra doble envio
    async _handleRegStep2() {
        if (this._submitting) return;
        const idInstitucion = document.getElementById('r-institucion').value;
        if (!idInstitucion) { UI.toast('Selecciona una institucion', 'warning'); return; }

        this._submitting = true;
        this._regData.idInstitucion = parseInt(idInstitucion);
        const emailToVerify = this._regData.email;

        const btn = document.querySelector('.auth-btn');
        btn.disabled = true;
        btn.innerHTML = '<i class="bi bi-hourglass-split"></i> Registrando...';

        try {
            const res = await Api.request('/auth/register', {
                method: 'POST',
                body: JSON.stringify(this._regData)
            });
            // Ya no devuelve token, solo mensaje de verificacion
            UI.toast(res.mensaje, 'success');
            this._regEmail = emailToVerify;
            this._regData = {};
            this._regConfirm = '';
            this._renderVerificacion('forward');
        } catch (err) {
            const msg = err.detalles?.length ? err.detalles.join(', ') : err.mensaje || 'Error al registrarse';
            UI.toast(msg, 'error');
            btn.disabled = false;
            btn.innerHTML = '<i class="bi bi-person-plus"></i> Registrarse';
        } finally {
            this._submitting = false;
        }
    },

    // --- Verificacion de email (paso 3) ---
    _verifyTimer: null, // Referencia al intervalo del countdown de 2 min
    _renderVerificacion(direction) {
        const email = this._regEmail;

        const html = `
            <div class="auth-card">
                <div class="auth-header">
                    <i class="bi bi-envelope-check auth-logo"></i>
                    <h2>Verificar Email</h2>
                    <p class="auth-subtitle">Ingresa el codigo de 5 caracteres enviado a<br><strong>${email}</strong></p>
                </div>
                <form id="verify-form" class="auth-form">
                    <div class="form-group">
                        <label>Codigo de verificacion</label>
                        <input type="text" class="form-control verification-code-input" id="v-codigo"
                               placeholder="Ej: AB3K7" maxlength="5" autocomplete="off">
                    </div>
                    <p class="auth-hint" id="verify-countdown" style="text-align:center; margin-bottom:0.5rem;"></p>
                    <button type="submit" class="btn btn-primary auth-btn">
                        <i class="bi bi-check-circle"></i> Verificar
                    </button>
                </form>
                <p class="auth-hint" style="margin-top:1rem;">
                    No recibiste el codigo?
                    <a href="#" id="btn-resend">Reenviar codigo</a>
                    <span id="resend-timer" style="display:none; color:var(--text-secondary);"></span>
                </p>
                <p class="auth-hint">
                    <a href="#" id="btn-back-login">Volver al login</a>
                </p>
            </div>`;

        this._setAuthContent(html, direction);

        const delay = direction ? 210 : 10;
        setTimeout(() => {
            document.getElementById('verify-form')?.addEventListener('submit', (e) => {
                e.preventDefault();
                this._handleVerify();
            });
            document.getElementById('btn-resend')?.addEventListener('click', (e) => {
                e.preventDefault();
                this._handleResend();
            });
            document.getElementById('btn-back-login')?.addEventListener('click', (e) => {
                e.preventDefault();
                this._clearVerifyTimer();
                this._regEmail = '';
                this.renderLogin('back');
            });
            // Auto-uppercase y filtrar solo alfanumericos
            document.getElementById('v-codigo')?.addEventListener('input', (e) => {
                e.target.value = e.target.value.toUpperCase().replace(/[^A-Z0-9]/g, '');
            });
            // Iniciar countdown de 2 minutos
            this._startVerifyCountdown(120);
        }, delay);
    },

    // Inicia countdown visual de N segundos; al llegar a 0 redirige al registro
    _startVerifyCountdown(totalSeconds) {
        this._clearVerifyTimer();
        let remaining = totalSeconds;
        const el = document.getElementById('verify-countdown');
        const update = () => {
            const m = Math.floor(remaining / 60);
            const s = remaining % 60;
            if (el) el.textContent = `Tiempo restante: ${m}:${s.toString().padStart(2, '0')}`;
        };
        update();
        this._verifyTimer = setInterval(() => {
            remaining--;
            update();
            if (remaining <= 0) {
                this._clearVerifyTimer();
                UI.toast('El tiempo de verificacion ha expirado. Debe registrarse nuevamente.', 'error');
                this._regEmail = '';
                this._regData = {};
                this._regConfirm = '';
                this.renderRegister('forward');
            }
        }, 1000);
    },

    // Limpia el timer de verificacion si existe
    _clearVerifyTimer() {
        if (this._verifyTimer) {
            clearInterval(this._verifyTimer);
            this._verifyTimer = null;
        }
    },

    // Envia codigo de verificacion al backend
    async _handleVerify() {
        const codigo = document.getElementById('v-codigo').value.trim();
        if (codigo.length !== 5) {
            UI.toast('El codigo debe tener 5 caracteres', 'warning');
            return;
        }

        const btn = document.querySelector('.auth-btn');
        btn.disabled = true;
        btn.innerHTML = '<i class="bi bi-hourglass-split"></i> Verificando...';

        try {
            const res = await Api.request('/auth/verificar', {
                method: 'POST',
                body: JSON.stringify({ email: this._regEmail, codigo })
            });
            this._clearVerifyTimer();
            this.saveSession(res.token, res.usuario);
            UI.toast('Email verificado exitosamente', 'success');
            this._regEmail = '';
            this._enterApp();
        } catch (err) {
            const msg = err.mensaje || 'Codigo incorrecto';
            // Si el backend indica que debe registrarse nuevamente (intentos agotados o expirado)
            if (msg.includes('registrarse nuevamente')) {
                this._clearVerifyTimer();
                UI.toast(msg, 'error');
                this._regEmail = '';
                this._regData = {};
                this._regConfirm = '';
                this.renderRegister('forward');
                return;
            }
            UI.toast(msg, 'error');
            btn.disabled = false;
            btn.innerHTML = '<i class="bi bi-check-circle"></i> Verificar';
        }
    },

    // Reenviar codigo con cooldown visual de 60s
    async _handleResend() {
        const link = document.getElementById('btn-resend');
        const timer = document.getElementById('resend-timer');

        try {
            await Api.request('/auth/reenviar-codigo', {
                method: 'POST',
                body: JSON.stringify({ email: this._regEmail })
            });
            UI.toast('Codigo reenviado', 'success');

            // Reiniciar el countdown de 2 minutos con nuevo codigo
            this._startVerifyCountdown(120);

            // Cooldown visual de 60 segundos para el boton de reenvio
            link.style.display = 'none';
            timer.style.display = '';
            let seconds = 60;
            timer.textContent = `(Reenviar en ${seconds}s)`;
            const interval = setInterval(() => {
                seconds--;
                timer.textContent = `(Reenviar en ${seconds}s)`;
                if (seconds <= 0) {
                    clearInterval(interval);
                    link.style.display = '';
                    timer.style.display = 'none';
                }
            }, 1000);
        } catch (err) {
            UI.toast(err.mensaje || 'Error al reenviar', 'error');
        }
    }
};

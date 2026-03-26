// Pagina: Carga biometrica (captura automatica de plantillas faciales)
const EnrolamientoPage = {
    _ws: null,                // Conexion WebSocket al backend
    _stream: null,            // MediaStream de la camara
    _interval: null,          // Intervalo de preview en vivo
    _selectedProfesor: null,  // idUsuario del profesor seleccionado
    _sampleCount: 0,          // Muestras capturadas en la sesion actual
    _maxSamples: 20,          // Muestras max (definidas por el backend)
    _enrolling: false,        // Si esta en proceso de captura automatica
    _enrollTimer: null,       // Timer del countdown de captura
    _enrollSeconds: 30,       // Duracion total de la captura en segundos
    _enrolledMap: {},         // Map idUsuario -> plantilla (para detectar re-enrolamiento)

    async render() {
        const container = document.getElementById('page-content');
        container.innerHTML = `
            <div class="page-header">
                <h2>Carga Biometrica</h2>
            </div>

            <div class="face-page-grid">
                <!-- Main enrollment card -->
                <div class="face-card face-card-main">
                    <div class="face-card-header">
                        <div class="face-card-title">
                            <i class="bi bi-person-bounding-box"></i> Captura de rostro
                        </div>
                        <button id="btn-enroll-start" class="btn btn-sm btn-secondary">
                            <i class="bi bi-camera-video"></i> Iniciar Camara
                        </button>
                    </div>

                    <div class="enroll-select-row">
                        <label>Profesor</label>
                        <select id="enroll-profesor" class="form-control">
                            <option value="">Seleccionar profesor...</option>
                        </select>
                    </div>

                    <div class="face-video-container" id="enroll-video-container">
                        <video id="enroll-video" autoplay playsinline muted></video>
                        <canvas id="enroll-canvas"></canvas>
                        <div class="face-video-placeholder" id="enroll-placeholder">
                            <i class="bi bi-person-bounding-box"></i>
                            <span>Seleccione un profesor e inicie la camara</span>
                        </div>
                    </div>

                    <div class="enroll-bottom-bar">
                        <div class="enrollment-progress-container" id="enroll-progress-container" style="display:none;">
                            <div class="enrollment-progress-bar">
                                <div class="enrollment-progress-fill" id="enroll-progress-fill"></div>
                            </div>
                            <div class="enrollment-progress-info">
                                <span id="enroll-progress-text">0/15 muestras</span>
                                <span id="enroll-timer-text"></span>
                            </div>
                            <small id="enroll-instruction" class="enrollment-instruction">
                                <i class="bi bi-info-circle"></i> Mire a la camara y mueva la cabeza lentamente en distintas direcciones
                            </small>
                        </div>
                        <button id="btn-enroll-capture" class="btn btn-primary" disabled>
                            <i class="bi bi-record-circle"></i> Iniciar Captura (30s)
                        </button>
                    </div>
                </div>

                <!-- Enrolled list -->
                <div class="face-card face-card-side">
                    <div class="face-card-header">
                        <div class="face-card-title">
                            <i class="bi bi-people"></i> Profesores enrolados
                        </div>
                        <span class="face-history-count" id="enrolled-count">0</span>
                    </div>
                    <div id="enrolled-list" class="face-history-list">
                        <div class="face-empty-state">
                            <i class="bi bi-person-x"></i>
                            <span>Cargando...</span>
                        </div>
                    </div>
                </div>
            </div>
        `;

        await this.loadProfesores();
        await this.loadEnrolled();

        document.getElementById('btn-enroll-start').addEventListener('click', () => {
            if (this._stream) {
                this.stopCamera();
            } else {
                this.startCamera();
            }
        });

        document.getElementById('btn-enroll-capture').addEventListener('click', () => {
            if (this._enrolling) {
                this.stopEnrollment();
            } else {
                this.startEnrollment();
            }
        });

        document.getElementById('enroll-profesor').addEventListener('change', (e) => {
            this._selectedProfesor = e.target.value ? Number(e.target.value) : null;
            this._sampleCount = 0;
            this.updateCaptureButton();
        });
    },

    // --- Carga de datos ---
    async loadProfesores() {
        try {
            const profesores = await Api.get('/profesores');
            const usuarios = await Api.get('/usuarios');
            const select = document.getElementById('enroll-profesor');

            const userMap = {};
            usuarios.forEach(u => { userMap[u.idUsuario] = `${u.nombre} ${u.apellido}`; });

            profesores.forEach(p => {
                const opt = document.createElement('option');
                opt.value = p.idUsuario;
                opt.textContent = userMap[p.idUsuario] || `Profesor ${p.idUsuario}`;
                select.appendChild(opt);
            });
        } catch (e) {
            UI.toast('Error al cargar profesores', 'danger');
        }
    },

    async loadEnrolled() {
        try {
            const plantillas = await Api.get('/plantillas-biometricas');
            const usuarios = await Api.get('/usuarios');
            const userMap = {};
            usuarios.forEach(u => { userMap[u.idUsuario] = `${u.nombre} ${u.apellido}`; });

            // Cache de plantillas por idUsuario para detectar re-enrolamiento
            this._enrolledMap = {};
            plantillas.forEach(p => { this._enrolledMap[p.idUsuario] = p; });

            const listEl = document.getElementById('enrolled-list');
            const countEl = document.getElementById('enrolled-count');
            if (countEl) countEl.textContent = plantillas.length;

            if (plantillas.length === 0) {
                listEl.innerHTML = `
                    <div class="face-empty-state">
                        <i class="bi bi-person-x"></i>
                        <span>No hay profesores enrolados</span>
                    </div>`;
                return;
            }

            listEl.innerHTML = plantillas.map(p => `
                <div class="face-history-item enrolled-item">
                    <div class="enrolled-info">
                        <i class="bi bi-person-check"></i>
                        <div>
                            <strong>${userMap[p.idUsuario] || 'Usuario ' + p.idUsuario}</strong>
                            <small>${p.cantidadMuestras} muestras &middot; ${new Date(p.fechaCreacion).toLocaleDateString('es-AR')}</small>
                        </div>
                    </div>
                    <button class="btn-icon btn-icon-danger" onclick="EnrolamientoPage.deleteEnrollment(${p.idPlantillaBiometrica})" title="Eliminar enrolamiento">
                        <i class="bi bi-trash3"></i>
                    </button>
                </div>
            `).join('');
        } catch (e) {
            UI.toast('Error al cargar enrolados', 'danger');
        }
    },

    async deleteEnrollment(id) {
        const ok = await UI.confirm('Eliminar enrolamiento', 'Se eliminara el registro facial de este profesor.');
        if (!ok) return;
        try {
            await Api.delete(`/plantillas-biometricas/${id}`);
            UI.toast('Enrolamiento eliminado', 'success');
            this.loadEnrolled();
        } catch (e) {
            UI.toast('Error al eliminar', 'danger');
        }
    },

    // --- Gestion de camara ---
    _showLoading() {
        const icon = document.getElementById('enroll-placeholder')?.querySelector('i');
        const text = document.getElementById('enroll-placeholder')?.querySelector('span');
        if (icon) icon.className = 'bi bi-arrow-repeat face-loading-spinner';
        if (text) text.textContent = 'Accediendo a la camara...';
    },

    _resetPlaceholder() {
        const icon = document.getElementById('enroll-placeholder')?.querySelector('i');
        const text = document.getElementById('enroll-placeholder')?.querySelector('span');
        if (icon) icon.className = 'bi bi-person-bounding-box';
        if (text) text.textContent = 'Seleccione un profesor e inicie la camara';
    },

    async startCamera() {
        // Validar que se haya seleccionado un profesor
        if (!this._selectedProfesor) {
            UI.toast('Debe seleccionar un profesor antes de iniciar la camara', 'warning');
            return;
        }

        // Validar que la camara de reconocimiento este apagada
        if (typeof _cameraActive !== 'undefined' && _cameraActive) {
            UI.toast('Debe apagar la camara de Reconocimiento antes de usar Carga Biometrica', 'warning');
            return;
        }

        const MAX_RETRIES = 3;
        const RETRY_DELAY = 1000;

        this._showLoading();
        const btn = document.getElementById('btn-enroll-start');
        if (btn) btn.disabled = true;

        for (let attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                this._stream = await navigator.mediaDevices.getUserMedia({
                    video: { width: { ideal: 640 }, height: { ideal: 480 }, facingMode: 'user' }
                });
                const video = document.getElementById('enroll-video');
                video.srcObject = this._stream;

                await new Promise((resolve, reject) => {
                    video.onloadedmetadata = () => {
                        video.play().then(resolve).catch(reject);
                    };
                    setTimeout(() => reject(new Error('Timeout al iniciar la camara')), 5000);
                });

                document.getElementById('enroll-placeholder').style.display = 'none';

                const canvas = document.getElementById('enroll-canvas');
                canvas.width = video.videoWidth;
                canvas.height = video.videoHeight;

                if (btn) {
                    btn.disabled = false;
                    btn.innerHTML = '<i class="bi bi-camera-video-off"></i> Detener';
                    btn.classList.remove('btn-secondary');
                    btn.classList.add('btn-danger');
                }

                this.connectWebSocket();
                this.updateCaptureButton();
                return;

            } catch (err) {
                if (this._stream) {
                    this._stream.getTracks().forEach(t => t.stop());
                    this._stream = null;
                }

                if (attempt < MAX_RETRIES) {
                    await new Promise(r => setTimeout(r, RETRY_DELAY));
                } else {
                    this._resetPlaceholder();
                    if (btn) btn.disabled = false;
                    UI.toast('Error al acceder a la camara: ' + err.message, 'danger');
                }
            }
        }
    },

    stopCamera() {
        // Si esta en proceso de captura, detenerla primero
        if (this._enrolling) {
            this.stopEnrollment();
        }

        if (this._interval) {
            clearInterval(this._interval);
            this._interval = null;
        }
        if (this._ws) {
            this._ws.close();
            this._ws = null;
        }
        if (this._stream) {
            this._stream.getTracks().forEach(t => t.stop());
            this._stream = null;
        }

        const video = document.getElementById('enroll-video');
        if (video) video.srcObject = null;

        const canvas = document.getElementById('enroll-canvas');
        if (canvas) {
            const ctx = canvas.getContext('2d');
            ctx.clearRect(0, 0, canvas.width, canvas.height);
        }

        const placeholder = document.getElementById('enroll-placeholder');
        if (placeholder) placeholder.style.display = '';
        this._resetPlaceholder();

        const btn = document.getElementById('btn-enroll-start');
        if (btn) {
            btn.innerHTML = '<i class="bi bi-camera-video"></i> Iniciar Camara';
            btn.classList.remove('btn-danger');
            btn.classList.add('btn-secondary');
        }

        // Ocultar progreso
        const progressContainer = document.getElementById('enroll-progress-container');
        if (progressContainer) progressContainer.style.display = 'none';

        this.updateCaptureButton();
    },

    connectWebSocket() {
        const token = Auth.getToken();
        const protocol = location.protocol === 'https:' ? 'wss:' : 'ws:';
        const wsUrl = `${protocol}//${location.host}/ws/face?token=${token}`;

        this._ws = new WebSocket(wsUrl);
        this._ws.onopen = () => {};
        this._ws.onmessage = (event) => {
            const data = JSON.parse(event.data);
            this.handleResponse(data);
        };
        this._ws.onclose = () => {};
        this._ws.onerror = () => {};

        this.startLivePreview();
    },

    startLivePreview() {
        if (this._interval) clearInterval(this._interval);

        const video = document.getElementById('enroll-video');
        const tmpCanvas = document.createElement('canvas');

        this._interval = setInterval(() => {
            if (!this._ws || this._ws.readyState !== WebSocket.OPEN) return;
            if (!video || video.readyState < 2) return;

            tmpCanvas.width = video.videoWidth;
            tmpCanvas.height = video.videoHeight;
            const ctx = tmpCanvas.getContext('2d');
            ctx.drawImage(video, 0, 0);

            tmpCanvas.toBlob((blob) => {
                if (blob && this._ws && this._ws.readyState === WebSocket.OPEN) {
                    this._ws.send(blob);
                }
            }, 'image/jpeg', 0.85);
        }, 500);
    },

    // --- Captura automatica de 30 segundos ---
    async startEnrollment() {
        if (!this._ws || this._ws.readyState !== WebSocket.OPEN) {
            UI.toast('WebSocket no conectado', 'danger');
            return;
        }
        if (!this._selectedProfesor) {
            UI.toast('Seleccione un profesor primero', 'warning');
            return;
        }

        // Validar que reconocimiento este apagado
        if (typeof _cameraActive !== 'undefined' && _cameraActive) {
            UI.toast('Debe apagar la camara de Reconocimiento antes de capturar', 'warning');
            return;
        }

        // Verificar si el profesor ya tiene una carga biometrica existente
        const existing = this._enrolledMap[this._selectedProfesor];
        if (existing) {
            const fecha = new Date(existing.fechaCreacion).toLocaleDateString('es-AR');
            const confirmar = await UI.confirm(
                'Registro biometrico existente',
                `Este profesor ya cuenta con un registro biometrico en el sistema:\n\n` +
                `- Muestras registradas: ${existing.cantidadMuestras}\n` +
                `- Fecha de registro: ${fecha}\n\n` +
                `Al realizar una nueva captura, el registro anterior sera reemplazado por uno nuevo. ` +
                `Esto es recomendable si el reconocimiento facial no esta funcionando correctamente ` +
                `o si las condiciones de captura cambiaron (iluminacion, camara, etc).\n\n` +
                `¿Desea eliminar el registro actual e iniciar una nueva captura?`
            );
            if (!confirmar) return;

            // Eliminar la plantilla anterior
            try {
                await Api.delete(`/plantillas-biometricas/${existing.idPlantillaBiometrica}`);
                await this.loadEnrolled();
            } catch (e) {
                UI.toast('Error al eliminar la carga anterior', 'danger');
                return;
            }
        }

        this._enrolling = true;
        this._sampleCount = 0;

        // Cambiar WebSocket a modo ENROLLMENT (el backend resetea contadores)
        this._ws.send(JSON.stringify({
            mode: 'ENROLLMENT',
            idUsuario: this._selectedProfesor
        }));

        // Mostrar UI de progreso
        const progressContainer = document.getElementById('enroll-progress-container');
        if (progressContainer) progressContainer.style.display = '';

        this.updateProgressUI();

        // Cambiar boton a "Detener"
        const btn = document.getElementById('btn-enroll-capture');
        if (btn) {
            btn.innerHTML = '<i class="bi bi-stop-circle"></i> Detener Captura';
            btn.classList.remove('btn-primary');
            btn.classList.add('btn-danger');
        }

        // Deshabilitar selector de profesor durante captura
        const select = document.getElementById('enroll-profesor');
        if (select) select.disabled = true;

        // Countdown timer
        let remaining = this._enrollSeconds;
        this.updateTimerText(remaining);

        this._enrollTimer = setInterval(() => {
            remaining--;
            this.updateTimerText(remaining);

            if (remaining <= 0) {
                this.stopEnrollment();
            }
        }, 1000);

        UI.toast('Captura iniciada. Mire a la camara y mueva la cabeza lentamente', 'info');
    },

    stopEnrollment() {
        this._enrolling = false;

        if (this._enrollTimer) {
            clearInterval(this._enrollTimer);
            this._enrollTimer = null;
        }

        // Volver a modo RECOGNITION en el WebSocket
        if (this._ws && this._ws.readyState === WebSocket.OPEN) {
            this._ws.send(JSON.stringify({ mode: 'RECOGNITION' }));
        }

        // Restaurar boton
        const btn = document.getElementById('btn-enroll-capture');
        if (btn) {
            btn.innerHTML = '<i class="bi bi-record-circle"></i> Iniciar Captura (30s)';
            btn.classList.remove('btn-danger');
            btn.classList.add('btn-primary');
        }

        // Rehabilitar selector
        const select = document.getElementById('enroll-profesor');
        if (select) select.disabled = false;

        if (this._sampleCount > 0) {
            UI.toast(`Captura finalizada: ${this._sampleCount} muestras registradas`, 'success');
            this.loadEnrolled();
        } else {
            UI.toast('Captura detenida sin muestras capturadas', 'warning');
        }
    },

    updateTimerText(seconds) {
        const el = document.getElementById('enroll-timer-text');
        if (el) el.textContent = `${seconds}s restantes`;
    },

    updateProgressUI() {
        const fill = document.getElementById('enroll-progress-fill');
        const text = document.getElementById('enroll-progress-text');
        const pct = Math.min((this._sampleCount / this._maxSamples) * 100, 100);

        if (fill) fill.style.width = `${pct}%`;
        if (text) text.textContent = `${this._sampleCount}/${this._maxSamples} muestras`;
    },

    // --- Procesamiento de respuestas ---
    handleResponse(data) {
        const canvas = document.getElementById('enroll-canvas');
        if (!canvas) return;
        const ctx = canvas.getContext('2d');
        ctx.clearRect(0, 0, canvas.width, canvas.height);

        // Debug
        if (data.status !== 'NO_FACE' && data.status !== 'MODE_SET' && data.status !== 'ENROLLMENT_PREVIEW') {
            console.log('[Enroll WS]', data.status, data.faceBox ? `box: ${data.faceBox}` : 'sin box');
        }

        // Dibujar face box
        if (data.faceBox) {
            let color = '#f0c040'; // amarillo por defecto
            if (data.status === 'ENROLLED' || data.status === 'ENROLLMENT_COMPLETE') {
                color = '#27ae60'; // verde al capturar
            }

            const [x, y, w, h] = data.faceBox;
            ctx.strokeStyle = color;
            ctx.lineWidth = 4;
            ctx.strokeRect(x, y, w, h);
        }

        // Muestra capturada automaticamente
        if (data.status === 'ENROLLED') {
            this._sampleCount = data.sampleNumber || (this._sampleCount + 1);
            if (data.maxSamples) this._maxSamples = data.maxSamples;
            this.updateProgressUI();
        }

        // Preview durante enrollment (sin captura, solo face box)
        if (data.status === 'ENROLLMENT_PREVIEW') {
            if (data.maxSamples) this._maxSamples = data.maxSamples;
            this._sampleCount = data.sampleNumber || this._sampleCount;
            this.updateProgressUI();
        }

        // Captura completa (max muestras alcanzado por el backend)
        if (data.status === 'ENROLLMENT_COMPLETE') {
            this._sampleCount = data.totalSamples || this._sampleCount;
            this.updateProgressUI();
            UI.toast('Carga biometrica completada exitosamente', 'success');
            this.stopEnrollment();
        }

        if (data.status === 'ERROR') {
            UI.toast('Error en la carga biometrica: ' + (data.message || 'Error desconocido'), 'danger');
        }
    },

    // Habilita boton de captura solo si hay camara activa, profesor seleccionado y no esta capturando
    updateCaptureButton() {
        const btn = document.getElementById('btn-enroll-capture');
        if (!btn) return;
        btn.disabled = !this._stream || !this._selectedProfesor;
    }
};

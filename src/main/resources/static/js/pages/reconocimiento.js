// Pagina: Reconocimiento facial en tiempo real

// --- Estado global de camara (persiste entre navegaciones SPA) ---
let _cameraStream = null;   // MediaStream de la camara
let _cameraWs = null;       // Conexion WebSocket al backend
let _cameraActive = false;  // Flag de camara activa
let _cameraInterval = null; // Intervalo de captura de frames

const ReconocimientoPage = {
    _historyToday: [], // Registros de asistencia facial del dia

    render() {
        const container = document.getElementById('page-content');
        container.innerHTML = `
            <div class="page-header">
                <h2>Reconocimiento Facial</h2>
                <div class="page-actions">
                    <button id="btn-toggle-camera" class="btn btn-primary">
                        <i class="bi bi-camera-video"></i> Iniciar Camara
                    </button>
                </div>
            </div>

            <div class="face-page-grid">
                <!-- Main camera card -->
                <div class="face-card face-card-main">
                    <div class="face-card-header">
                        <div class="face-card-title">
                            <i class="bi bi-webcam"></i> Camara en vivo
                        </div>
                        <span id="face-status" class="face-status no-face">
                            <i class="bi bi-circle-fill"></i> Sin camara
                        </span>
                    </div>
                    <div class="face-video-container" id="video-container">
                        <video id="face-video" autoplay playsinline muted></video>
                        <canvas id="face-canvas"></canvas>
                        <div id="face-overlay-text" class="face-overlay-text"></div>
                        <div class="face-video-placeholder" id="video-placeholder">
                            <i class="bi bi-camera-video-off" id="placeholder-icon"></i>
                            <span id="placeholder-text">Presione "Iniciar Camara" para comenzar</span>
                        </div>
                    </div>
                    <div id="last-registration" class="last-registration" style="display:none;">
                        <i class="bi bi-check-circle-fill"></i>
                        <span id="last-reg-text"></span>
                    </div>
                </div>

                <!-- History sidebar -->
                <div class="face-card face-card-side">
                    <div class="face-card-header">
                        <div class="face-card-title">
                            <i class="bi bi-clock-history"></i> Registros de hoy
                        </div>
                        <span class="face-history-count" id="history-count">0</span>
                    </div>
                    <div id="face-history" class="face-history-list">
                        <div class="face-empty-state">
                            <i class="bi bi-inbox"></i>
                            <span>No hay registros aun</span>
                        </div>
                    </div>
                </div>
            </div>
        `;

        // Toggle iniciar/detener camara
        document.getElementById('btn-toggle-camera').addEventListener('click', () => {
            if (_cameraActive) {
                this.stopCamera();
            } else {
                this.startCamera();
            }
        });

        // Si la camara ya estaba activa (usuario navego y volvio), reconecta
        if (_cameraStream && _cameraActive) {
            const video = document.getElementById('face-video');
            video.srcObject = _cameraStream;
            document.getElementById('video-placeholder').style.display = 'none';
            this.updateUI(true);

            // Esperar a que el video tenga dimensiones y sincronizar canvas
            const syncCanvas = () => {
                const canvas = document.getElementById('face-canvas');
                if (canvas && video.videoWidth > 0) {
                    canvas.width = video.videoWidth;
                    canvas.height = video.videoHeight;
                } else {
                    requestAnimationFrame(syncCanvas);
                }
            };
            syncCanvas();

            if (!_cameraWs || _cameraWs.readyState !== WebSocket.OPEN) {
                this.connectWebSocket();
            }
            this.startFrameCapture();
        }

        // Carga historial de asistencias faciales del dia
        this.loadTodayHistory();
    },

    // --- Gestion de camara ---
    // Muestra spinner de carga en el placeholder de la camara
    _showCameraLoading() {
        const icon = document.getElementById('placeholder-icon');
        const text = document.getElementById('placeholder-text');
        if (icon) {
            icon.className = 'bi bi-arrow-repeat face-loading-spinner';
        }
        if (text) text.textContent = 'Accediendo a la camara...';
    },

    // Restaura el placeholder a su estado original
    _resetPlaceholder() {
        const icon = document.getElementById('placeholder-icon');
        const text = document.getElementById('placeholder-text');
        if (icon) icon.className = 'bi bi-camera-video-off';
        if (text) text.textContent = 'Presione "Iniciar Camara" para comenzar';
    },

    async startCamera() {
        const MAX_RETRIES = 3;
        const RETRY_DELAY = 1000;

        // Mostrar spinner de carga mientras se accede a la camara
        this._showCameraLoading();
        const btn = document.getElementById('btn-toggle-camera');
        if (btn) btn.disabled = true;

        for (let attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                _cameraStream = await navigator.mediaDevices.getUserMedia({
                    video: { width: { ideal: 640 }, height: { ideal: 480 }, facingMode: 'user' }
                });
                const video = document.getElementById('face-video');
                video.srcObject = _cameraStream;

                // Esperar a que el video este listo para reproducirse
                await new Promise((resolve, reject) => {
                    video.onloadedmetadata = () => {
                        video.play().then(resolve).catch(reject);
                    };
                    // Timeout de seguridad por intento
                    setTimeout(() => reject(new Error('Timeout al iniciar la camara')), 5000);
                });

                // Exito: ocultar placeholder, configurar canvas y conectar
                document.getElementById('video-placeholder').style.display = 'none';
                _cameraActive = true;

                const canvas = document.getElementById('face-canvas');
                canvas.width = video.videoWidth;
                canvas.height = video.videoHeight;
                this.connectWebSocket();
                this.startFrameCapture();

                this.updateUI(true);
                this.updateSidebarIndicator(true);
                if (btn) btn.disabled = false;
                return; // Salir del bucle tras exito

            } catch (err) {
                // Limpiar stream parcial si existe antes de reintentar
                if (_cameraStream) {
                    _cameraStream.getTracks().forEach(t => t.stop());
                    _cameraStream = null;
                }

                if (attempt < MAX_RETRIES) {
                    // Esperar antes del siguiente intento
                    await new Promise(r => setTimeout(r, RETRY_DELAY));
                } else {
                    // Todos los intentos fallaron
                    this._resetPlaceholder();
                    if (btn) btn.disabled = false;
                    UI.toast('Error al acceder a la camara: ' + err.message, 'danger');
                }
            }
        }
    },

    stopCamera() {
        if (_cameraInterval) {
            clearInterval(_cameraInterval);
            _cameraInterval = null;
        }
        if (_cameraWs) {
            _cameraWs.close();
            _cameraWs = null;
        }
        if (_cameraStream) {
            _cameraStream.getTracks().forEach(t => t.stop());
            _cameraStream = null;
        }
        _cameraActive = false;

        const video = document.getElementById('face-video');
        if (video) video.srcObject = null;

        // Limpiar canvas de face box
        const canvas = document.getElementById('face-canvas');
        if (canvas) {
            const ctx = canvas.getContext('2d');
            ctx.clearRect(0, 0, canvas.width, canvas.height);
        }

        const placeholder = document.getElementById('video-placeholder');
        if (placeholder) placeholder.style.display = '';
        this._resetPlaceholder();

        this.updateUI(false);
        this.updateSidebarIndicator(false);
    },

    // --- Conexion WebSocket ---
    connectWebSocket() {
        const token = Auth.getToken();
        const protocol = location.protocol === 'https:' ? 'wss:' : 'ws:';
        const wsUrl = `${protocol}//${location.host}/ws/face?token=${token}`;

        _cameraWs = new WebSocket(wsUrl);

        _cameraWs.onopen = () => {
            console.log('[WS] Conectado a', wsUrl.replace(/token=.*/, 'token=***'));
            // Informa al servidor que se opera en modo reconocimiento
            _cameraWs.send(JSON.stringify({ mode: 'RECOGNITION' }));
        };

        _cameraWs.onmessage = (event) => {
            const data = JSON.parse(event.data);
            this.handleResponse(data);
        };

        _cameraWs.onclose = (e) => {
            console.warn('[WS] Desconectado, codigo:', e.code, 'razon:', e.reason);
            // Reconexion automatica si la camara sigue activa
            if (_cameraActive) {
                setTimeout(() => this.connectWebSocket(), 2000);
            }
        };

        _cameraWs.onerror = (e) => {
            console.error('[WS] Error de conexion', e);
        };
    },

    // --- Captura de frames ---
    // Envia frames JPEG al servidor via WebSocket cada ~333ms (~3 FPS)
    startFrameCapture() {
        if (_cameraInterval) clearInterval(_cameraInterval);

        const video = document.getElementById('face-video');
        const canvas = document.createElement('canvas');

        _cameraInterval = setInterval(() => {
            if (!_cameraWs || _cameraWs.readyState !== WebSocket.OPEN) return;
            if (!video || video.readyState < 2) return;

            canvas.width = video.videoWidth;
            canvas.height = video.videoHeight;
            const ctx = canvas.getContext('2d');
            ctx.drawImage(video, 0, 0);

            canvas.toBlob((blob) => {
                if (blob && _cameraWs && _cameraWs.readyState === WebSocket.OPEN) {
                    _cameraWs.send(blob);
                }
            }, 'image/jpeg', 0.85);
        }, 500);
    },

    // --- Procesamiento de respuestas del servidor ---
    // Estados: NO_FACE, UNKNOWN, REGISTERED, ALREADY_REGISTERED, COOLDOWN, NO_SCHEDULE
    handleResponse(data) {
        // Toast de asistencia registrada funciona SIEMPRE (aunque no este en la pagina)
        if (data.status === 'REGISTERED') {
            UI.toast('Asistencia registrada: ' + (data.message || ''), 'success');
        }

        const canvas = document.getElementById('face-canvas');
        const overlayText = document.getElementById('face-overlay-text');
        const statusEl = document.getElementById('face-status');

        // Si no estamos en la pagina de reconocimiento, solo el toast importa
        if (!canvas) return;

        const ctx = canvas.getContext('2d');
        ctx.clearRect(0, 0, canvas.width, canvas.height);

        // Debug: ver TODAS las respuestas del servidor en consola
        console.log('[Face WS]', data.status, data.faceBox ? `box: [${data.faceBox}]` : '', data.message || '');

        // Dibujar recuadro de tracking siempre que haya faceBox
        if (data.faceBox) {
            // Amarillo para rostros no registrados, verde para registrados
            let boxColor = '#f0c040'; // amarillo por defecto
            if (data.status === 'REGISTERED' || data.status === 'ALREADY_REGISTERED' || data.status === 'COOLDOWN') {
                boxColor = '#27ae60'; // verde para rostros del sistema
            }
            this.drawFaceBox(ctx, data.faceBox, boxColor);
        }

        switch (data.status) {
            case 'NO_FACE':
                statusEl.className = 'face-status no-face';
                statusEl.innerHTML = '<i class="bi bi-circle-fill"></i> Sin rostro';
                if (overlayText) overlayText.textContent = '';
                break;

            case 'UNKNOWN':
                statusEl.className = 'face-status unknown';
                statusEl.innerHTML = '<i class="bi bi-circle-fill"></i> No reconocido';
                if (overlayText) overlayText.textContent = '';
                break;

            case 'REGISTERED':
                statusEl.className = 'face-status recognized';
                statusEl.innerHTML = '<i class="bi bi-circle-fill"></i> Registrado';
                if (overlayText) overlayText.textContent = data.message || 'Asistencia registrada';
                this.showLastRegistration(data);
                this.addToHistory(data);
                break;

            case 'ALREADY_REGISTERED':
                statusEl.className = 'face-status recognized';
                statusEl.innerHTML = '<i class="bi bi-circle-fill"></i> Ya registrado';
                if (overlayText) overlayText.textContent = 'Ya registrado hoy';
                break;

            case 'COOLDOWN':
                statusEl.className = 'face-status processing';
                statusEl.innerHTML = '<i class="bi bi-circle-fill"></i> Espere...';
                if (overlayText) overlayText.textContent = '';
                break;

            case 'NO_SCHEDULE':
                statusEl.className = 'face-status processing';
                statusEl.innerHTML = '<i class="bi bi-circle-fill"></i> Sin horario';
                if (overlayText) overlayText.textContent = data.message || 'Sin clase ahora';
                break;

            case 'MODE_SET':
                break;

            default:
                statusEl.className = 'face-status no-face';
                statusEl.innerHTML = '<i class="bi bi-circle-fill"></i> ' + (data.message || data.status);
        }
    },

    // Dibuja rectangulo sobre el rostro detectado en el canvas overlay
    drawFaceBox(ctx, faceBox, color) {
        if (!faceBox || faceBox.length < 4) return;
        const [x, y, w, h] = faceBox;
        ctx.strokeStyle = color;
        ctx.lineWidth = 4;
        ctx.strokeRect(x, y, w, h);
    },

    // --- Historial lateral ---
    showLastRegistration(data) {
        const el = document.getElementById('last-registration');
        const textEl = document.getElementById('last-reg-text');
        if (el && textEl) {
            const time = new Date().toLocaleTimeString('es-AR');
            textEl.textContent = `${time} - ${data.message || 'Registrado'}`;
            el.style.display = 'flex';
        }
    },

    addToHistory(data) {
        const time = new Date().toLocaleTimeString('es-AR');
        this._historyToday.unshift({
            time,
            message: data.message || 'Asistencia registrada',
            status: data.status
        });
        this.renderHistory();
    },

    // Carga asistencias faciales de hoy desde la API
    async loadTodayHistory() {
        try {
            const today = new Date().toISOString().split('T')[0];
            const asistencias = await Api.get(`/asistencias`);
            const todayRecords = asistencias.filter(a =>
                a.fecha === today && a.modoRegistro === 'FACIAL'
            );
            this._historyToday = todayRecords.map(a => ({
                time: a.horaEntrada || '',
                message: `${a.estado}`,
                idAsignacion: a.idAsignacion,
                status: 'REGISTERED'
            }));
            this.renderHistory();
        } catch (e) {
            // Silently fail
        }
    },

    renderHistory() {
        const container = document.getElementById('face-history');
        const countEl = document.getElementById('history-count');
        if (!container) return;

        if (countEl) countEl.textContent = this._historyToday.length;

        if (this._historyToday.length === 0) {
            container.innerHTML = `
                <div class="face-empty-state">
                    <i class="bi bi-inbox"></i>
                    <span>No hay registros aun</span>
                </div>`;
            return;
        }

        container.innerHTML = this._historyToday.map(h => `
            <div class="face-history-item">
                <div class="face-history-dot ${h.status === 'REGISTERED' ? 'dot-success' : ''}"></div>
                <div class="face-history-info">
                    <span class="face-history-msg">${h.message}</span>
                    <span class="face-history-time">${h.time}</span>
                </div>
            </div>
        `).join('');
    },

    // --- Actualizacion de UI ---
    updateUI(active) {
        const btn = document.getElementById('btn-toggle-camera');
        if (!btn) return;

        if (active) {
            btn.innerHTML = '<i class="bi bi-camera-video-off"></i> Detener Camara';
            btn.classList.remove('btn-primary');
            btn.classList.add('btn-danger');
            // Actualizar indicador de estado a "Conectada" al iniciar camara
            const statusEl = document.getElementById('face-status');
            if (statusEl) {
                statusEl.className = 'face-status recognized';
                statusEl.innerHTML = '<i class="bi bi-circle-fill"></i> Conectada';
            }
        } else {
            btn.innerHTML = '<i class="bi bi-camera-video"></i> Iniciar Camara';
            btn.classList.remove('btn-danger');
            btn.classList.add('btn-primary');
            const statusEl = document.getElementById('face-status');
            if (statusEl) {
                statusEl.className = 'face-status no-face';
                statusEl.innerHTML = '<i class="bi bi-circle-fill"></i> Sin camara';
            }
        }
    },

    // Agrega/quita punto verde en el sidebar para indicar camara activa
    updateSidebarIndicator(active) {
        const navItem = document.querySelector('[data-page="reconocimiento"]');
        if (!navItem) return;

        let indicator = navItem.querySelector('.camera-active-indicator');
        if (active && !indicator) {
            indicator = document.createElement('span');
            indicator.className = 'camera-active-indicator';
            navItem.appendChild(indicator);
        } else if (!active && indicator) {
            indicator.remove();
        }
    }
};

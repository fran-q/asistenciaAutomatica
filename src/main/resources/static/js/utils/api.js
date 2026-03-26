// --- Cliente API con autenticacion JWT ---
const API_BASE = 'http://localhost:8080/api';

const Api = {
    // Metodo central: arma headers, adjunta token y ejecuta fetch
    async request(endpoint, options = {}) {
        const url = `${API_BASE}${endpoint}`;

        // --- Headers con token de autorizacion ---
        const headers = { 'Content-Type': 'application/json' };
        const token = localStorage.getItem('auth_token');
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        const config = {
            headers,
            ...options,
        };

        try {
            const response = await fetch(url, config);
            if (!response.ok) {
                // --- Manejo de error 401 (sesion expirada) ---
                if (response.status === 401) {
                    localStorage.removeItem('auth_token');
                    localStorage.removeItem('auth_user');
                    if (typeof Auth !== 'undefined') {
                        Auth.showAuthScreen();
                    }
                    throw { mensaje: 'Sesion expirada, inicia sesion nuevamente', detalles: [] };
                }
                // Intenta parsear error del backend; si falla, genera uno generico
                const error = await response.json().catch(() => ({
                    mensaje: `Error ${response.status}`,
                    detalles: []
                }));
                throw error;
            }
            if (response.status === 204) return null; // DELETE exitoso sin cuerpo
            return await response.json();
        } catch (err) {
            if (err.mensaje) throw err; // Error ya formateado del backend
            throw { mensaje: 'Error de conexion con el servidor', detalles: [] };
        }
    },

    // --- Metodos de conveniencia (GET, POST, PUT, DELETE, PATCH) ---
    get(endpoint) {
        return this.request(endpoint);
    },

    post(endpoint, data) {
        return this.request(endpoint, { method: 'POST', body: JSON.stringify(data) });
    },

    put(endpoint, data) {
        return this.request(endpoint, { method: 'PUT', body: JSON.stringify(data) });
    },

    delete(endpoint) {
        return this.request(endpoint, { method: 'DELETE' });
    },

    patch(endpoint, data) {
        return this.request(endpoint, { method: 'PATCH', body: data ? JSON.stringify(data) : undefined });
    }
};

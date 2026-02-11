const API_BASE = 'http://localhost:8080/api';

const Api = {
    async request(endpoint, options = {}) {
        const url = `${API_BASE}${endpoint}`;
        const config = {
            headers: { 'Content-Type': 'application/json' },
            ...options,
        };

        try {
            const response = await fetch(url, config);
            if (!response.ok) {
                const error = await response.json().catch(() => ({
                    mensaje: `Error ${response.status}`,
                    detalles: []
                }));
                throw error;
            }
            if (response.status === 204) return null;
            return await response.json();
        } catch (err) {
            if (err.mensaje) throw err;
            throw { mensaje: 'Error de conexion con el servidor', detalles: [] };
        }
    },

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
    }
};

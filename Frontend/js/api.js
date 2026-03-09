/**
 * Senior API Wrapper
 * Handles normalization, token persistence, and interceptor-like behavior.
 */
const API = {
    async request(endpoint, options = {}) {
        const token = localStorage.getItem(CONFIG.TOKEN_KEY);

        const headers = {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
            ...options.headers
        };

        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        const url = `${CONFIG.API_BASE_URL}${endpoint}`;

        try {
            const response = await fetch(url, {
                ...options,
                headers
            });

            // Handle Global Status Interceptors
            if (response.status === 401) {
                return this.handleUnauthorized();
            }

            const result = await this.parseResponse(response);

            if (!response.ok) {
                throw new Error(result.message || `Request failed with status ${response.status}`);
            }

            return result;
        } catch (error) {
            console.error(`[API Error] ${options.method || 'GET'} ${endpoint}:`, error);
            throw error;
        }
    },

    async parseResponse(response) {
        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            return await response.json();
        }
        return { message: await response.text() };
    },

    get(endpoint) {
        return this.request(endpoint, { method: 'GET' });
    },

    post(endpoint, body) {
        return this.request(endpoint, {
            method: 'POST',
            body: JSON.stringify(body)
        });
    },

    put(endpoint, body) {
        return this.request(endpoint, {
            method: 'PUT',
            body: JSON.stringify(body)
        });
    },

    delete(endpoint) {
        return this.request(endpoint, { method: 'DELETE' });
    },

    handleUnauthorized() {
        console.warn('Session expired or unauthorized. Redirecting to login.');
        localStorage.removeItem(CONFIG.TOKEN_KEY);
        localStorage.removeItem(CONFIG.USER_KEY);

        // Prevent infinite loops if already on login
        if (!window.location.pathname.endsWith('login.html')) {
            window.location.href = CONFIG.FRONTEND_ROOT + 'login.html';
        }
        return null;
    }
};

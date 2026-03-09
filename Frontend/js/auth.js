const Auth = {
    init() {
        this.setupEventListeners();
        this.checkAuth();
    },

    setupEventListeners() {
        const signupForm = document.getElementById('signupForm');
        const loginForm = document.getElementById('loginForm');
        const roleOptions = document.querySelectorAll('.role-option');

        let selectedRole = 'STARTUP';

        if (roleOptions) {
            roleOptions.forEach(opt => {
                opt.addEventListener('click', () => {
                    roleOptions.forEach(o => o.classList.remove('active'));
                    opt.classList.add('active');
                    selectedRole = opt.dataset.role;
                });
            });
        }

        if (signupForm) {
            signupForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                const btn = signupForm.querySelector('button');
                UI.showLoading(btn);

                const data = {
                    name: document.getElementById('name').value,
                    email: document.getElementById('email').value,
                    password: document.getElementById('password').value,
                    role: selectedRole
                };

                try {
                    const res = await API.post('/auth/signup', data);
                    this.handleAuthSuccess(res.data);
                } catch (err) {
                    UI.toast(err.message, 'error');
                } finally {
                    UI.hideLoading(btn);
                }
            });
        }

        if (loginForm) {
            loginForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                const btn = loginForm.querySelector('button');
                UI.showLoading(btn);

                const data = {
                    email: document.getElementById('email').value,
                    password: document.getElementById('password').value
                };

                try {
                    const res = await API.post('/auth/login', data);
                    this.handleAuthSuccess(res.data);
                } catch (err) {
                    UI.toast(err.message, 'error');
                } finally {
                    UI.hideLoading(btn);
                }
            });
        }
    },

    handleAuthSuccess(data) {
        localStorage.setItem(CONFIG.TOKEN_KEY, data.token);
        localStorage.setItem(CONFIG.USER_KEY, JSON.stringify({
            id: data.id,
            name: data.name,
            email: data.email,
            role: data.role
        }));

        UI.toast('Authentication successful!', 'success');

        setTimeout(() => {
            this.redirectByRole(data.role);
        }, 1000);
    },

    redirectByRole(role) {
        const root = CONFIG.FRONTEND_ROOT;
        switch (role) {
            case 'ADMIN': window.location.href = root + 'dashboards/admin_dashboard.html'; break;
            case 'STARTUP': window.location.href = root + 'dashboards/startup_dashboard.html'; break;
            case 'INVESTOR': window.location.href = root + 'dashboards/investor_dashboard.html'; break;
            default: window.location.href = root + 'index.html';
        }
    },

    checkAuth() {
        const token = localStorage.getItem(CONFIG.TOKEN_KEY);
        const user = JSON.parse(localStorage.getItem(CONFIG.USER_KEY));
        const path = window.location.pathname;

        if (token && (path.endsWith('login.html') || path.endsWith('signup.html'))) {
            this.redirectByRole(user.role);
        }
    },

    logout() {
        localStorage.removeItem(CONFIG.TOKEN_KEY);
        localStorage.removeItem(CONFIG.USER_KEY);
        window.location.href = CONFIG.FRONTEND_ROOT + 'login.html';
    }
};

document.addEventListener('DOMContentLoaded', () => Auth.init());

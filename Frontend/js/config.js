const CONFIG = {
    API_BASE_URL: 'https://right-one-vfi9.onrender.com/api',
    TOKEN_KEY: 'rightone_token',
    USER_KEY: 'rightone_user',
    get FRONTEND_ROOT() {
        const path = window.location.pathname;
        const subfolders = ['/dashboards/', '/deals/', '/profile/'];
        return subfolders.some(sf => path.includes(sf)) ? '../' : './';
    }
};

const UI_CONFIG = {
    ANIMATION_DURATION: 300,
    TOAST_DURATION: 3000
};

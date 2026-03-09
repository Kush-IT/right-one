/**
 * Senior UI Utility Service
 * Decouples logic from styling by leveraging CSS transitions and components.
 */
const UI = {
    /**
     * Display a premium toast notification
     * @param {string} message 
     * @param {'success' | 'error' | 'info'} type 
     */
    toast(message, type = 'info') {
        const container = this.getToastContainer();
        const toast = document.createElement('div');

        toast.className = `toast toast-${type} animate-fade`;
        toast.innerHTML = `<div class="toast-content">${message}</div>`;

        container.appendChild(toast);

        // Auto-remove after duration
        setTimeout(() => {
            toast.classList.add('toast-hide');
            setTimeout(() => toast.remove(), 400);
        }, CONFIG.TOAST_DURATION || 3500);
    },

    getToastContainer() {
        let container = document.getElementById('toast-container');
        if (!container) {
            container = document.createElement('div');
            container.id = 'toast-container';
            document.body.appendChild(container);
        }
        return container;
    },

    /**
     * Loading state for buttons
     */
    showLoading(btn) {
        if (!btn || btn.classList.contains('btn-loading')) return;

        btn.disabled = true;
        btn.classList.add('btn-loading');
        btn.dataset.originalHtml = btn.innerHTML;

        btn.innerHTML = `
            <svg class="spinner" viewBox="0 0 50 50">
                <circle class="path" cx="25" cy="25" r="20" fill="none" stroke-width="5"></circle>
            </svg>
            <span>Processing...</span>
        `;
    },

    hideLoading(btn) {
        if (!btn || !btn.classList.contains('btn-loading')) return;

        btn.disabled = false;
        btn.classList.remove('btn-loading');
        btn.innerHTML = btn.dataset.originalHtml;
    },

    /**
     * Modal management
     */
    showModal(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.style.display = 'flex';
            document.body.style.overflow = 'hidden'; // Prevent background scroll
            modal.classList.add('active');
        }
    },

    hideModal(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.style.display = 'none';
            document.body.style.overflow = '';
            modal.classList.remove('active');
        }
    }
};

/**
 * Common Styles Injected via JS for Component Isolation if not in CSS
 * (Optional - usually better to keep in global.css, but useful for one-off utilities)
 */
const style = document.createElement('style');
style.textContent = `
    #toast-container {
        position: fixed;
        bottom: 2rem;
        right: 2rem;
        z-index: 9999;
        display: flex;
        flex-direction: column;
        gap: 0.75rem;
    }
    .toast {
        padding: 1rem 1.5rem;
        border-radius: var(--radius-sm);
        background: var(--bg-surface);
        border: 1px solid var(--glass-border);
        color: var(--text-white);
        box-shadow: 0 10px 25px -10px rgba(0,0,0,0.5);
        min-width: 300px;
        backdrop-filter: blur(10px);
    }
    .toast-error { border-left: 4px solid var(--error); }
    .toast-success { border-left: 4px solid var(--success); }
    .toast-info { border-left: 4px solid var(--info); }
    
    .toast-hide {
        opacity: 0;
        transform: translateX(20px);
        transition: all 0.4s ease;
    }

    .btn-loading { cursor: wait; opacity: 0.8; }
    .spinner {
        animation: rotate 2s linear infinite;
        width: 18px;
        height: 18px;
        margin-right: 8px;
    }
    .spinner .path {
        stroke: currentColor;
        stroke-linecap: round;
        animation: dash 1.5s ease-in-out infinite;
    }
    @keyframes rotate { 100% { transform: rotate(360deg); } }
    @keyframes dash {
        0% { stroke-dasharray: 1, 150; stroke-dashoffset: 0; }
        50% { stroke-dasharray: 90, 150; stroke-dashoffset: -35; }
        100% { stroke-dasharray: 90, 150; stroke-dashoffset: -124; }
    }
`;
document.head.appendChild(style);

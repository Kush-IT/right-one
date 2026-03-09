/**
 * Senior KYC Logic
 * Handles multi-step navigation, file validation, and multipart submission.
 */
const KYC = {
    currentStep: 1,
    totalSteps: 6,

    init() {
        this.updateStepUI();
        this.setupEventListeners();
        this.loadUserData();
        this.loadExistingKyc();
    },

    async loadExistingKyc() {
        const userData = localStorage.getItem(CONFIG.USER_KEY);
        if (!userData) return;

        const user = JSON.parse(userData);
        try {
            const res = await API.get(`/kyc/user/${user.id}`);
            if (res.success && res.data) {
                const kyc = res.data;
                console.log("Existing KYC found:", kyc);

                // Check for Approved status
                if (kyc.status === 'APPROVED') {
                    if (document.getElementById('kycForm')) document.getElementById('kycForm').style.display = 'none';
                    if (document.getElementById('formCard')) document.getElementById('formCard').style.display = 'none';
                    if (document.querySelector('.step-indicator')) document.querySelector('.step-indicator').style.display = 'none';
                    if (document.getElementById('approvedContainer')) document.getElementById('approvedContainer').style.display = 'block';
                    return;
                }

                // Map data to form fields as placeholders
                const form = document.getElementById('kycForm');
                const fieldMapping = {
                    'fullName': kyc.fullName,
                    'dateOfBirth': kyc.dateOfBirth,
                    'gender': kyc.gender,
                    'nationality': kyc.nationality,
                    'phoneNumber': kyc.phoneNumber,
                    'addressLine1': kyc.addressLine1,
                    'addressLine2': kyc.addressLine2,
                    'city': kyc.city,
                    'state': kyc.state,
                    'postalCode': kyc.postalCode,
                    'country': kyc.country,
                    'identityType': kyc.identityType,
                    'identityNumber': kyc.identityNumber,
                    'panNumber': kyc.panNumber,
                    'bankName': kyc.bankName,
                    'bankAccountNumber': kyc.bankAccountNumber,
                    'ifscCode': kyc.ifscCode,
                    'businessName': kyc.businessName,
                    'businessType': kyc.businessType,
                    'businessRegistrationNumber': kyc.businessRegistrationNumber,
                    'businessWebsite': kyc.businessWebsite
                };

                Object.keys(fieldMapping).forEach(name => {
                    const input = form.querySelector(`[name="${name}"]`);
                    if (input && fieldMapping[name]) {
                        if (input.tagName === 'SELECT') {
                            // For selects, we set the value if it's pending/rejected to help them
                            input.value = fieldMapping[name];
                        } else {
                            input.placeholder = `Current: ${fieldMapping[name]}`;
                            // Also set value if they just want to resubmit without changes
                            input.value = fieldMapping[name];
                        }
                    }
                });
            }
        } catch (err) {
            console.error("Error loading existing KYC:", err);
        }
    },

    loadUserData() {
        const userData = localStorage.getItem(CONFIG.USER_KEY);
        if (userData) {
            const user = JSON.parse(userData);
            console.log("Loaded KYC User:", user);

            if (document.getElementById('email')) document.getElementById('email').value = user.email || '';
        } else {
            console.warn("No user data found in localStorage");
        }
    },

    setupEventListeners() {
        // File Previews
        const fileInputs = ['aadharCard', 'panCard', 'bankStatement', 'businessCertificate'];
        fileInputs.forEach(id => {
            const input = document.getElementById(id);
            if (input) {
                input.addEventListener('change', (e) => this.handleFileSelect(e, id));
            }
        });

        // Form Submission
        const form = document.getElementById('kycForm');
        if (form) {
            form.addEventListener('submit', (e) => this.handleSubmit(e));
        }
    },

    handleFileSelect(e, id) {
        const file = e.target.files[0];
        const card = document.getElementById(`${id}Card`);
        const preview = card.querySelector('.preview-area');

        if (file) {
            if (file.size > 5 * 1024 * 1024) {
                UI.toast('File size exceeds 5MB', 'error');
                e.target.value = '';
                return;
            }
            card.classList.add('success');
            preview.textContent = `Selected: ${file.name}`;
        }
    },

    nextStep() {
        if (this.validateCurrentStep()) {
            this.currentStep++;
            this.updateStepUI();
        }
    },

    prevStep() {
        this.currentStep--;
        this.updateStepUI();
    },

    validateCurrentStep() {
        return this.validateSection(this.currentStep);
    },

    validateSection(step) {
        const section = document.querySelector(`.form-section[data-step="${step}"]`);
        const inputs = section.querySelectorAll('input[required], select[required], textarea[required]');

        let valid = true;
        inputs.forEach(input => {
            if (!input.value) {
                input.style.borderColor = 'var(--error)';
                valid = false;
            } else {
                input.style.borderColor = 'var(--glass-border)';
            }
        });

        if (!valid) UI.toast(`Please fill all required fields in Step ${step}`, 'error');
        return valid;
    },

    validateAllSteps() {
        for (let i = 1; i <= this.totalSteps; i++) {
            if (!this.validateSection(i)) {
                this.currentStep = i;
                this.updateStepUI();
                return false;
            }
        }
        return true;
    },

    updateStepUI() {
        // Update Sections
        document.querySelectorAll('.form-section').forEach(s => s.classList.remove('active'));
        document.querySelector(`.form-section[data-step="${this.currentStep}"]`).classList.add('active');

        // Update Indicators
        document.querySelectorAll('.step').forEach((s, idx) => {
            s.classList.remove('active', 'completed');
            if (idx + 1 < this.currentStep) s.classList.add('completed');
            if (idx + 1 === this.currentStep) s.classList.add('active');
        });

        window.scrollTo({ top: 0, behavior: 'smooth' });
    },

    async handleSubmit(e) {
        e.preventDefault();

        console.log("KYC Submission Started");

        if (!this.validateAllSteps()) return;

        if (!document.getElementById('termsAccepted').checked) {
            UI.toast('Please accept the terms and conditions', 'error');
            return;
        }

        const btn = e.target.querySelector('button[type="submit"]');
        UI.showLoading(btn);

        const formData = new FormData(e.target);
        // Note: userId is no longer strictly required in formData as backend uses token

        // Debug: Log entries (excluding files for clarity)
        for (let pair of formData.entries()) {
            if (!(pair[1] instanceof File)) {
                console.log(pair[0] + ': ' + pair[1]);
            }
        }

        try {
            const token = localStorage.getItem(CONFIG.TOKEN_KEY);
            const response = await fetch(`${CONFIG.API_BASE_URL}/kyc/submit`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`
                },
                body: formData
            });

            const result = await response.json();

            if (response.ok) {
                UI.toast('KYC Submitted Successfully', 'success');
                setTimeout(() => {
                    window.location.href = 'pending.html';
                }, 5000);
            } else {
                throw new Error(result.message || 'Submission failed');
            }
        } catch (error) {
            console.error('KYC Submission Error:', error);
            UI.toast(error.message || 'Connection to server failed', 'error');
        } finally {
            UI.hideLoading(btn);
        }
    }
};

document.addEventListener('DOMContentLoaded', () => KYC.init());

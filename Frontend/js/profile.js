const Profile = {
    async init() {
        const user = JSON.parse(localStorage.getItem(CONFIG.USER_KEY));
        this.role = user.role;
        this.setupEventListeners();
        await this.loadProfile();
        await this.loadKycData();
    },

    async loadKycData() {
        const container = document.getElementById('kycDataContainer');
        if (!container) return;

        try {
            const user = JSON.parse(localStorage.getItem(CONFIG.USER_KEY));
            const res = await API.get(`/kyc/user/${user.id}`);

            if (res.success && res.data) {
                const kyc = res.data;
                const groups = {
                    'Personal': {
                        'Full Name': kyc.fullName,
                        'DOB': kyc.dateOfBirth,
                        'Gender': kyc.gender,
                        'Nationality': kyc.nationality,
                        'Phone': kyc.phoneNumber,
                        'Email': kyc.email
                    },
                    'Address': {
                        'Line 1': kyc.addressLine1,
                        'Line 2': kyc.addressLine2,
                        'City': kyc.city,
                        'State': kyc.state,
                        'Country': kyc.country,
                        'Postal Code': kyc.postalCode
                    },
                    'Identity': {
                        'ID Type': kyc.identityType,
                        'ID Number': kyc.identityNumber,
                        'PAN': kyc.panNumber
                    },
                    'Banking': {
                        'Bank': kyc.bankName,
                        'Acc Number': kyc.bankAccountNumber,
                        'IFSC': kyc.ifscCode
                    }
                };

                if (this.role === 'STARTUP') {
                    groups['Business'] = {
                        'Company': kyc.businessName,
                        'Type': kyc.businessType,
                        'Reg Number': kyc.businessRegistrationNumber,
                        'Website': kyc.businessWebsite
                    };
                }

                let html = '';
                for (const [groupName, fields] of Object.entries(groups)) {
                    html += `
                        <div style="margin-bottom: 1.5rem;">
                            <h4 style="color: var(--primary); margin-bottom: 0.5rem; font-size: 0.9rem; text-transform: uppercase; letter-spacing: 0.05em;">${groupName}</h4>
                            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 0.5rem;">
                                ${Object.entries(fields).map(([label, value]) => `
                                    <div>
                                        <div style="font-size: 0.75rem; color: var(--text-secondary);">${label}</div>
                                        <div style="font-weight: 600; color: var(--text-primary);">${value || 'Not provided'}</div>
                                    </div>
                                `).join('')}
                            </div>
                        </div>
                    `;
                }
                container.innerHTML = html;
            } else {
                container.innerHTML = '<p class="text-dim">No KYC data submitted yet.</p>';
            }
        } catch (err) {
            console.error("Error loading profile KYC:", err);
            container.innerHTML = '<p class="text-error">Failed to load KYC information.</p>';
        }
    },

    async loadProfile() {
        try {
            const endpoint = this.role === 'STARTUP' ? '/startup/profile' : '/investor/profile';
            const res = await API.get(endpoint);
            const data = res.data;

            if (this.role === 'STARTUP') {
                document.getElementById('companyName').value = data.companyName || '';
                document.getElementById('sector').value = data.sector || '';
                document.getElementById('valuation').value = data.valuation || '';
            } else {
                document.getElementById('fundSize').value = data.fundSize || '';
                document.getElementById('preference').value = data.investmentPreference || '';
            }
        } catch (err) {
            UI.toast('Failed to load profile', 'error');
        }
    },

    setupEventListeners() {
        const form = document.getElementById('profileForm');
        if (form) {
            form.addEventListener('submit', async (e) => {
                e.preventDefault();
                const btn = form.querySelector('button');
                UI.showLoading(btn);

                let data;
                if (this.role === 'STARTUP') {
                    data = {
                        companyName: document.getElementById('companyName').value,
                        sector: document.getElementById('sector').value,
                        valuation: document.getElementById('valuation').value
                    };
                } else {
                    data = {
                        fundSize: document.getElementById('fundSize').value,
                        investmentPreference: document.getElementById('preference').value
                    };
                }

                try {
                    const endpoint = this.role === 'STARTUP' ? '/startup/profile' : '/investor/profile';
                    await API.put(endpoint, data);
                    UI.toast('Profile updated successfully', 'success');
                } catch (err) {
                    UI.toast(err.message, 'error');
                } finally {
                    UI.hideLoading(btn);
                }
            });
        }
    }
};

document.addEventListener('DOMContentLoaded', () => Profile.init());

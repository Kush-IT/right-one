const Admin = {
    async init() {
        await this.loadStats();
        await this.loadUsers();
        await this.loadKycRequests();
        await this.loadAllInterests();
    },

    async loadUsers() {
        const tbody = document.getElementById('usersTableBody');
        try {
            const res = await API.get('/admin/users');
            const data = res.data;
            tbody.innerHTML = '';

            if (!data || data.length === 0) {
                tbody.innerHTML = '<tr><td colspan="4" style="text-align:center">No users found</td></tr>';
                return;
            }

            data.forEach(u => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td>${u.name}</td>
                    <td>${u.email}</td>
                    <td><span class="badge ${u.role.toLowerCase()} badge-md">${u.role}</span></td>
                    <td>${new Date(u.createdAt).toLocaleDateString()}</td>
                `;
                tbody.appendChild(tr);
            });
        } catch (err) {
            console.error("Error loading users:", err);
        }
    },

    async loadStats() {
        try {
            const res = await API.get('/admin/stats');
            console.log("Admin Stats Response:", res);
            const stats = res.data;
            if (stats) {
                document.getElementById('totalDealsCount').textContent = stats.totalDeals || 0;
                document.getElementById('totalInterestsCount').textContent = stats.totalInterests || 0;
            }
        } catch (err) {
            console.error("Error loading stats:", err);
        }
    },

    async loadAllInterests() {
        const tbody = document.getElementById('interestsTableBody');
        try {
            const res = await API.get('/admin/interests/all');
            const data = res.data;
            tbody.innerHTML = '';

            if (!data || data.length === 0) {
                tbody.innerHTML = '<tr><td colspan="5" style="text-align:center">No interests found</td></tr>';
                return;
            }

            data.forEach(i => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td>${i.investor.user.name}</td>
                    <td>${i.deal.title}</td>
                    <td>₹${i.investmentAmount}L</td>
                    <td><span class="badge ${i.status.toLowerCase()} badge-md">${i.status}</span></td>
                    <td>${new Date(i.createdAt).toLocaleDateString()}</td>
                `;
                tbody.appendChild(tr);
            });
        } catch (err) {
            console.error("Error loading interests:", err);
        }
    },

    setLoading(isLoading) {
        const spinner = document.getElementById('loadingSpinner');
        if (spinner) spinner.style.display = isLoading ? 'block' : 'none';

        const table = document.getElementById('kycTable');
        if (table) table.style.opacity = isLoading ? '0.5' : '1';
    },

    async loadKycRequests() {
        this.setLoading(true);
        const tableBody = document.getElementById('kycTableBody');

        try {
            // Using the API wrapper to handle CONFIG.API_BASE_URL and token
            const result = await API.get("/admin/kyc/all");
            const data = result.data;

            tableBody.innerHTML = '';

            if (!data || data.length === 0) {
                tableBody.innerHTML = `<tr><td colspan="7" style="text-align:center; padding: 2rem;">No KYC requests found.</td></tr>`;
                return;
            }

            data.forEach(kyc => {
                const tr = document.createElement('tr');

                // Format date
                const submittedDate = kyc.submittedAt ? new Date(kyc.submittedAt).toLocaleDateString() : 'N/A';

                // Status Badge logic
                const status = (kyc.status || 'PENDING').toUpperCase();
                let statusBadge = `<span class="badge ${status.toLowerCase()} badge-md">${status}</span>`;

                // Actions logic
                let actionButtons = '';
                if (status === 'PENDING') {
                    actionButtons = `
                        <div style="display: flex; gap: 0.5rem;">
                            <button class="btn btn-approve btn-sm" onclick="Admin.updateKycStatus(${kyc.id}, 'approve')">Approve</button>
                            <button class="btn btn-reject btn-sm" onclick="Admin.updateKycStatus(${kyc.id}, 'reject')">Reject</button>
                        </div>
                    `;
                } else {
                    actionButtons = `<span class="badge ${status.toLowerCase()}" style="opacity: 0.6;">${status}</span>`;
                }

                tr.innerHTML = `
                    <td><strong>${kyc.fullName}</strong></td>
                    <td>${kyc.email}</td>
                    <td><span class="badge ${kyc.userRole ? kyc.userRole.toLowerCase() : 'unknown'} badge-md">${kyc.userRole || 'USER'}</span></td>
                    <td>${kyc.businessName || 'Personal'}</td>
                    <td>${submittedDate}</td>
                    <td>${statusBadge}</td>
                    <td>${actionButtons}</td>
                `;
                tableBody.appendChild(tr);
            });
        } catch (err) {
            console.error("Error in loadKycRequests:", err);
            alert("Error loading KYC data. Ensure backend is running at " + CONFIG.API_BASE_URL);
        } finally {
            this.setLoading(false);
        }
    },

    async updateKycStatus(kycId, action) {
        if (!confirm(`Are you sure you want to ${action} this KYC?`)) return;

        try {
            await API.put(`/admin/kyc/${action}/${kycId}`, {});
            UI.toast(`KYC ${action}d successfully`, 'success');
            await this.loadKycRequests();
        } catch (err) {
            console.error(`Failed to ${action} KYC:`, err);
            alert(`Error: Could not ${action} KYC. Please try again.`);
        }
    }
};

document.addEventListener('DOMContentLoaded', () => Admin.init());

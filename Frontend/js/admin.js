const Admin = {
    async init() {
        await this.loadStats();
        await this.loadUsers();
        await this.loadKycRequests();
        await this.loadAllInterests();
    },

    async loadUsers() {
        const tbody = document.getElementById('usersTableBody');
        if (!tbody) return;
        try {
            const res = await API.get('/admin/users');
            const data = (res && res.data) ? res.data : [];
            tbody.innerHTML = '';

            if (data.length === 0) {
                tbody.innerHTML = '<tr><td colspan="4" style="text-align:center">No users found</td></tr>';
                return;
            }

            data.forEach(u => {
                const tr = document.createElement('tr');
                const dateStr = u.createdAt ? new Date(u.createdAt).toLocaleDateString() : 'N/A';
                tr.innerHTML = `
                    <td>${u.name || 'Unknown'}</td>
                    <td>${u.email || 'N/A'}</td>
                    <td><span class="badge ${(u.role || 'USER').toLowerCase()} badge-md">${u.role || 'USER'}</span></td>
                    <td>${dateStr}</td>
                `;
                tbody.appendChild(tr);
            });
        } catch (err) {
            console.error("Error in loadUsers:", err);
            tbody.innerHTML = '<tr><td colspan="4" style="text-align:center; color: var(--error)">Failed to load users</td></tr>';
        }
    },

    async loadStats() {
        try {
            const res = await API.get('/admin/stats');
            const stats = (res && res.data) ? res.data : null;
            if (stats) {
                if (document.getElementById('totalDealsCount')) 
                    document.getElementById('totalDealsCount').textContent = stats.totalDeals || 0;
                if (document.getElementById('totalInterestsCount'))
                    document.getElementById('totalInterestsCount').textContent = stats.totalInterests || 0;
            }
        } catch (err) {
            console.error("Error in loadStats:", err);
        }
    },

    async loadAllInterests() {
        const tbody = document.getElementById('interestsTableBody');
        if (!tbody) return;
        try {
            const res = await API.get('/admin/interests/all');
            const data = (res && res.data) ? res.data : [];
            tbody.innerHTML = '';

            if (data.length === 0) {
                tbody.innerHTML = '<tr><td colspan="5" style="text-align:center">No interests found</td></tr>';
                return;
            }

            data.forEach(i => {
                const tr = document.createElement('tr');
                const investorName = i.investor?.user?.name || 'Unknown Investor';
                const dealTitle = i.deal?.title || 'Unknown Deal';
                const amount = i.investmentAmount || 0;
                const status = (i.status || 'PENDING').toUpperCase();
                const date = i.createdAt ? new Date(i.createdAt).toLocaleDateString() : 'N/A';
                
                tr.innerHTML = `
                    <td>${investorName}</td>
                    <td>${dealTitle}</td>
                    <td>₹${amount}L</td>
                    <td><span class="badge ${status.toLowerCase()} badge-md">${status}</span></td>
                    <td>${date}</td>
                `;
                tbody.appendChild(tr);
            });
        } catch (err) {
            console.error("Error in loadAllInterests:", err);
            tbody.innerHTML = '<tr><td colspan="5" style="text-align:center; color: var(--error)">Failed to load interests</td></tr>';
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
            const result = await API.get("/admin/kyc/all");
            const data = result.data;

            tableBody.innerHTML = '';

            if (!data || data.length === 0) {
                tableBody.innerHTML = `<tr><td colspan="7" style="text-align:center; padding: 2rem;">No KYC requests found.</td></tr>`;
                return;
            }

            data.forEach(kyc => {
                const tr = document.createElement('tr');

                const submittedDate = kyc.submittedAt ? new Date(kyc.submittedAt).toLocaleDateString() : 'N/A';

                const status = (kyc.status || 'PENDING').toUpperCase();
                let statusBadge = `<span class="badge ${status.toLowerCase()} badge-md">${status}</span>`;

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

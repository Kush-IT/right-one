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
                if (document.getElementById('totalDealsCount')) document.getElementById('totalDealsCount').textContent = stats.totalDeals || 0;
                if (document.getElementById('totalInterestsCount')) document.getElementById('totalInterestsCount').textContent = stats.totalInterests || 0;
                if (document.getElementById('totalUsersCount')) document.getElementById('totalUsersCount').textContent = stats.totalUsers || 0;
                if (document.getElementById('totalStartupsCount')) document.getElementById('totalStartupsCount').textContent = stats.totalStartups || 0;
                if (document.getElementById('totalInvestorsCount')) document.getElementById('totalInvestorsCount').textContent = stats.totalInvestors || 0;
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

            this.renderAnalytics(data);

        } catch (err) {
            console.error("Error in loadAllInterests:", err);
            tbody.innerHTML = '<tr><td colspan="5" style="text-align:center; color: var(--error)">Failed to load interests</td></tr>';
        }
    },

    renderAnalytics(interests) {
        if (!interests || interests.length === 0) return;

        // 1. Prepare Investment Flow Data (Pending vs Accepted)
        let totalPending = 0;
        let totalAccepted = 0;
        
        // 2. Prepare Top Startups Data
        const startupCapitalMap = {};

        interests.forEach(i => {
            const amount = parseFloat(i.investmentAmount || 0);
            const status = (i.status || 'PENDING').toUpperCase();
            
            if(status === 'PENDING') totalPending += amount;
            if(status === 'ACCEPTED') totalAccepted += amount;

            if (status === 'ACCEPTED') {
                const dealTitle = i.deal?.title || 'Unknown Deal';
                startupCapitalMap[dealTitle] = (startupCapitalMap[dealTitle] || 0) + amount;
            }
        });

        if (document.getElementById('totalCapitalDeployed')) {
            document.getElementById('totalCapitalDeployed').textContent = `₹${totalAccepted}L`;
        }

        // Investment Stage Chart (Bar)
        const stageCtx = document.getElementById('investmentStageChart')?.getContext('2d');
        if (stageCtx) {
            new Chart(stageCtx, {
                type: 'bar',
                data: {
                    labels: ['Pending Flow', 'Accepted Capital'],
                    datasets: [{
                        label: 'Investment (Lakhs)',
                        data: [totalPending, totalAccepted],
                        backgroundColor: ['#f59e0b', '#10b981'],
                        borderRadius: 6
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: { legend: { display: false } },
                    scales: {
                        y: { beginAtZero: true, grid: { color: '#e2e8f0' } },
                        x: { grid: { display: false } }
                    }
                }
            });
        }

        // Top Startups Chart (Doughnut)
        const startupCtx = document.getElementById('topStartupsChart')?.getContext('2d');
        if (startupCtx) {
            const labels = Object.keys(startupCapitalMap);
            const data = Object.values(startupCapitalMap);
            
            // Random colors generator
            const bgColors = labels.map((_, i) => `hsl(${(i * 137.5) % 360}, 70%, 60%)`);

            new Chart(startupCtx, {
                type: 'doughnut',
                data: {
                    labels: labels.length > 0 ? labels : ['No Accepted Deals'],
                    datasets: [{
                        data: data.length > 0 ? data : [1],
                        backgroundColor: data.length > 0 ? bgColors : ['#cbd5e1'],
                        borderWidth: 0,
                        hoverOffset: 4
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    cutout: '65%',
                    plugins: {
                        legend: { position: 'right', labels: { color: '#64748b' } }
                    }
                }
            });
        }

        // RIGHTONE Growth Chart (Area Line Chart)
        const growthCtx = document.getElementById('growthChart')?.getContext('2d');
        if (growthCtx) {
            // Generate last 6 months labels dynamically
            const monthNames = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
            const labels = [];
            for (let i = 5; i >= 0; i--) {
                const d = new Date();
                d.setMonth(d.getMonth() - i);
                labels.push(monthNames[d.getMonth()]);
            }

            // Mocked organic exponential growth curve for the demo
            const startupsData = [12, 19, 28, 45, 68, 105];
            const investorsData = [8, 14, 25, 38, 55, 92];

            new Chart(growthCtx, {
                type: 'line',
                data: {
                    labels: labels,
                    datasets: [
                        {
                            label: 'Startups Onboarded',
                            data: startupsData,
                            borderColor: '#3b82f6',
                            backgroundColor: 'rgba(59, 130, 246, 0.15)',
                            borderWidth: 2,
                            tension: 0.4,
                            fill: true
                        },
                        {
                            label: 'Investors Registered',
                            data: investorsData,
                            borderColor: '#10b981',
                            backgroundColor: 'rgba(16, 185, 129, 0.15)',
                            borderWidth: 2,
                            tension: 0.4,
                            fill: true
                        }
                    ]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: { legend: { position: 'top', labels: { usePointStyle: true } } },
                    interaction: { mode: 'index', intersect: false },
                    scales: {
                        y: { beginAtZero: true, grid: { color: '#e2e8f0', borderDash: [5, 5] } },
                        x: { grid: { display: false } }
                    }
                }
            });
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

const Startup = {
    async init() {
        this.loadUserSession();
        await this.loadStats();
        await this.loadInterests();
    },

    loadUserSession() {
        const user = JSON.parse(localStorage.getItem(CONFIG.USER_KEY));
        if (!user || user.role !== 'STARTUP') {
            window.location.href = '../login.html';
            return;
        }
        document.getElementById('welcomeText').textContent = `Welcome back, ${user.name}`;
    },

    async loadStats() {
        try {
            const profile = await API.get('/startup/profile');
            const kycBadge = document.getElementById('kycBadge');
            kycBadge.textContent = profile.data.kycStatus;
            kycBadge.className = `badge badge-${profile.data.kycStatus.toLowerCase()} badge-md`;

            // Fetch summary stats
            const dealsRes = await API.get('/startup/my-deals');
            const deals = dealsRes.data;
            document.getElementById('activeDealsCount').textContent = deals.length;

            let totalInterests = 0;
            let totalRaised = 0;
            for (const deal of deals) {
                const interestRes = await API.get(`/startup/deals/${deal.id}/interests`);
                const interests = interestRes.data;
                totalInterests += interests.length;

                // Sum accepted investments
                totalRaised += interests
                    .filter(i => i.status === 'ACCEPTED')
                    .reduce((sum, i) => sum + (i.investmentAmount || 0), 0);
            }
            document.getElementById('totalInterestsCount').textContent = totalInterests;
            document.getElementById('totalRaised').textContent = `₹${totalRaised}L`;

        } catch (err) {
            console.error(err);
        }
    },

    async loadInterests() {
        try {
            const res = await API.get('/startup/my-deals');
            const deals = res.data;
            for (const deal of deals) {
                const interestRes = await API.get(`/startup/deals/${deal.id}/interests`);
                this.renderInterests(interestRes.data);
            }
        } catch (err) {
            console.error(err);
        }
    },

    interestsList: [],

    renderInterests(interests) {
        const tbody = document.getElementById('interestsTableBody');
        if (this.interestsList.length === 0) tbody.innerHTML = '';

        interests.forEach(i => {
            if (this.interestsList.find(existing => existing.id === i.id)) return;
            this.interestsList.push(i);

            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${i.investorName}</td>
                <td>${i.dealTitle}</td>
                <td title="${i.message}">
                    <div style="font-weight: 500;">₹${i.investmentAmount}L for ${i.equityRequested}%</div>
                    <div class="text-dim" style="font-size: 0.8rem; max-width: 150px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
                        ${i.message}
                    </div>
                </td>
                <td><span class="badge badge-${i.status.toLowerCase()} badge-md">${i.status}</span></td>
                <td>${new Date(i.createdAt).toLocaleDateString()}</td>
                <td>
                    ${i.status === 'PENDING' ? `
                        <div style="display: flex; gap: 0.5rem;">
                            <button onclick="Startup.updateInterest(${i.id}, 'accept')" class="btn btn-primary btn-sm" style="background: var(--success); border: none;">Accept</button>
                            <button onclick="Startup.updateInterest(${i.id}, 'reject')" class="btn btn-primary btn-sm" style="background: var(--error); border: none;">Reject</button>
                        </div>
                    ` : '-'}
                </td>
            `;
            tbody.appendChild(tr);
        });
    },

    async updateInterest(id, action) {
        try {
            await API.put(`/interests/${action}/${id}`);
            UI.toast(`Interest ${action}ed!`, 'success');
            setTimeout(() => window.location.reload(), 1000);
        } catch (err) {
            UI.toast(err.message, 'error');
        }
    }
};

document.addEventListener('DOMContentLoaded', () => Startup.init());

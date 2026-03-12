const Investor = {
    async init() {
        this.loadUserSession();
        await this.loadStats();
        await this.loadDeals();
    },

    loadUserSession() {
        const user = JSON.parse(localStorage.getItem(CONFIG.USER_KEY));
        if (!user || user.role !== 'INVESTOR') {
            window.location.href = '../login.html';
            return;
        }
        document.getElementById('welcomeText').textContent = `Welcome, ${user.name}`;
    },

    async loadStats() {
        try {
            const profileRes = await API.get('/investor/profile');
            const profile = profileRes?.data;
            
            if (profile) {
                const kycBadge = document.getElementById('kycBadge');
                if (kycBadge) {
                    const status = profile.kycStatus || 'PENDING';
                    kycBadge.textContent = status;
                    kycBadge.className = `badge badge-${status.toLowerCase()} badge-md`;
                }
            }

            const interestsRes = await API.get('/interests/my');
            const interests = (interestsRes && interestsRes.data) ? interestsRes.data : [];

            // Calculate Stats
            const pendingCount = interests.filter(i => (i.status || '').toUpperCase() === 'PENDING').length;
            const totalInvested = interests
                .filter(i => (i.status || '').toUpperCase() === 'ACCEPTED')
                .reduce((sum, i) => sum + (i.investmentAmount || 0), 0);

            // Update UI
            if (document.getElementById('pendingInterestsCount'))
                document.getElementById('pendingInterestsCount').textContent = pendingCount;
            if (document.getElementById('totalInvestedValue'))
                document.getElementById('totalInvestedValue').textContent = `₹${totalInvested}L`;

            this.renderMyInterests(interests);
        } catch (err) {
            console.error("Error in loadStats:", err);
            // Don't crash the whole init, just log
        }
    },

    renderMyInterests(interests = []) {
        const tbody = document.getElementById('myInterestsTableBody');
        if (!tbody) return;
        tbody.innerHTML = '';

        if (!interests || interests.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" style="text-align:center">No investment requests yet</td></tr>';
            return;
        }

        interests.forEach(i => {
            const tr = document.createElement('tr');
            const status = (i.status || 'PENDING').toUpperCase();
            tr.innerHTML = `
                <td>${i.dealTitle || 'Unknown Deal'}</td>
                <td>₹${i.investmentAmount || 0} Lakhs</td>
                <td>${i.equityRequested || 0}%</td>
                <td><span class="badge badge-${status.toLowerCase()} badge-md">${status}</span></td>
                <td>${i.createdAt ? new Date(i.createdAt).toLocaleDateString() : 'N/A'}</td>
            `;
            tbody.appendChild(tr);
        });
    },

    async loadDeals() {
        const grid = document.getElementById('dealsGrid');
        if (!grid) return;
        try {
            const res = await API.get('/deals/open');
            const data = (res && res.data) ? res.data : [];
            grid.innerHTML = '';

            const countEl = document.getElementById('availableDealsCount');
            if (countEl) countEl.textContent = data.length;

            if (data.length === 0) {
                grid.innerHTML = '<p class="text-dim">No open deals available at the moment.</p>';
                return;
            }

            data.forEach(deal => {
                const card = document.createElement('div');
                card.className = 'glass-card deal-card';
                card.innerHTML = `
                    <h4>${deal.title || 'Untitled Deal'}</h4>
                    <p class="text-dim" style="font-size: 0.875rem; margin: 0.5rem 0;">${deal.companyName || 'Unknown Company'}</p>
                    <div style="margin: 1rem 0;">
                        <div class="stat-label">Funding Target</div>
                        <div style="font-weight: 600;">₹${deal.fundingRequired || 0}L</div>
                    </div>
                    <div style="margin: 1rem 0;">
                        <div class="stat-label">Equity Offered</div>
                        <div style="font-weight: 600;">${deal.equityOffered || 0}%</div>
                    </div>
                    <button class="btn btn-primary" style="width: 100%;" onclick="Investor.showInterestModal(${deal.id})">
                        Express Interest
                    </button>
                `;
                grid.appendChild(card);
            });
        } catch (err) {
            console.error("Error in loadDeals:", err);
            grid.innerHTML = '<p style="color: var(--error)">Failed to load deals. Please try again later.</p>';
        }
    },

    showInterestModal(dealId) {
        window.location.href = `../deals/express_interest.html?dealId=${dealId}`;
    },

    async submitInterest(event) {
        event.preventDefault();
        const btn = document.getElementById('submitInterestBtn');
        UI.showLoading(btn);

        const data = {
            dealId: document.getElementById('modalDealId').value,
            investmentAmount: document.getElementById('investAmount').value,
            equityRequested: document.getElementById('equityReq').value,
            message: document.getElementById('interestMessage').value
        };

        try {
            await API.post('/interests/create', data);
            UI.toast('Interest submitted successfully!', 'success');
            UI.hideModal('interestModal');
            setTimeout(() => {
                window.location.href = '../deals/my_interests.html';
            }, 1000);
        } catch (err) {
            UI.toast(err.message, 'error');
        } finally {
            UI.hideLoading(btn);
        }
    }
};

document.addEventListener('DOMContentLoaded', () => Investor.init());

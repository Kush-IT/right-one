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
            const profile = await API.get('/investor/profile');
            const kycBadge = document.getElementById('kycBadge');
            kycBadge.textContent = profile.data.kycStatus;
            kycBadge.className = `badge badge-${profile.data.kycStatus.toLowerCase()} badge-md`;

            const interestsRes = await API.get('/interests/my');
            const interests = interestsRes.data;

            // Calculate Stats
            const pendingCount = interests.filter(i => i.status === 'PENDING').length;
            const totalInvested = interests
                .filter(i => i.status === 'ACCEPTED')
                .reduce((sum, i) => sum + (i.investmentAmount || 0), 0);

            // Update UI
            document.getElementById('pendingInterestsCount').textContent = pendingCount;
            document.getElementById('totalInvestedValue').textContent = `₹${totalInvested}L`;

            this.renderMyInterests(interests);
        } catch (err) {
            console.error(err);
        }
    },

    renderMyInterests(interests) {
        const tbody = document.getElementById('myInterestsTableBody');
        tbody.innerHTML = '';

        if (interests.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" style="text-align:center">No investment requests yet</td></tr>';
            return;
        }

        interests.forEach(i => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${i.dealTitle}</td>
                <td>₹${i.investmentAmount} Lakhs</td>
                <td>${i.equityRequested}%</td>
                <td><span class="badge badge-${i.status.toLowerCase()} badge-md">${i.status}</span></td>
                <td>${new Date(i.createdAt).toLocaleDateString()}</td>
            `;
            tbody.appendChild(tr);
        });
    },

    async loadDeals() {
        try {
            const res = await API.get('/deals/open');
            const grid = document.getElementById('dealsGrid');
            grid.innerHTML = '';

            document.getElementById('availableDealsCount').textContent = res.data.length;

            if (res.data.length === 0) {
                grid.innerHTML = '<p class="text-dim">No open deals available at the moment.</p>';
                return;
            }

            res.data.forEach(deal => {
                const card = document.createElement('div');
                card.className = 'glass-card deal-card';
                card.innerHTML = `
                    <h4>${deal.title}</h4>
                    <p class="text-dim" style="font-size: 0.875rem; margin: 0.5rem 0;">${deal.companyName}</p>
                    <div style="margin: 1rem 0;">
                        <div class="stat-label">Funding Target</div>
                        <div style="font-weight: 600;">${deal.fundingRequired}</div>
                    </div>
                    <div style="margin: 1rem 0;">
                        <div class="stat-label">Equity Offered</div>
                        <div style="font-weight: 600;">${deal.equityOffered}</div>
                    </div>
                    <button class="btn btn-primary" style="width: 100%;" onclick="Investor.showInterestModal(${deal.id}, '${deal.title}')">
                        Express Interest
                    </button>
                `;
                grid.appendChild(card);
            });
        } catch (err) {
            UI.toast('Failed to load deals', 'error');
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

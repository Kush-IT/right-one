const InterestPage = {
    dealId: null,

    async init() {
        const urlParams = new URLSearchParams(window.location.search);
        this.dealId = urlParams.get('dealId');

        if (!this.dealId) {
            UI.toast('No deal selected', 'error');
            setTimeout(() => window.location.href = '../dashboards/investor_dashboard.html', 1500);
            return;
        }

        await this.loadDealDetails();
    },

    async loadDealDetails() {
        try {
            // Since we don't have a single deal GET endpoint shown in snippets, 
            // but we know it's needed, we'll try to find it or use a list and filter
            const res = await API.get('/deals/open');
            const deal = res.data.find(d => d.id == this.dealId);

            if (deal) {
                document.getElementById('dealTitle').textContent = `Express Interest in ${deal.title}`;
                document.getElementById('modalDealId').value = this.dealId;
            } else {
                UI.toast('Deal not found', 'error');
            }
        } catch (err) {
            console.error(err);
        }
    },

    async submitInterest(event) {
        event.preventDefault();
        const btn = document.getElementById('submitInterestBtn');
        UI.showLoading(btn);

        const data = {
            dealId: this.dealId,
            investmentAmount: document.getElementById('investAmount').value,
            equityRequested: document.getElementById('equityReq').value,
            message: document.getElementById('interestMessage').value
        };

        try {
            await API.post('/interests/create', data);
            UI.toast('Interest submitted successfully!', 'success');
            setTimeout(() => {
                window.location.href = '../deals/my_interests.html';
            }, 1500);
        } catch (err) {
            UI.toast(err.message, 'error');
        } finally {
            UI.hideLoading(btn);
        }
    }
};

document.addEventListener('DOMContentLoaded', () => InterestPage.init());

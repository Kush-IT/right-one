const Deals = {
    async init() {
        this.setupEventListeners();
        await this.loadExistingDeal();
    },

    async loadExistingDeal() {
        const form = document.getElementById('createDealForm');
        if (!form) return;

        try {
            const res = await API.get('/startup/my-deals');
            if (res.data && res.data.length > 0) {
                const deal = res.data[0];
                document.getElementById('title').value = deal.title;
                document.getElementById('description').value = deal.description;
                document.getElementById('fundingRequired').value = deal.fundingRequired;
                document.getElementById('equityOffered').value = deal.equityOffered;

                form.querySelector('button').textContent = 'Update Deal';
                document.querySelector('h2').textContent = 'Update Funding Round';
            }
        } catch (err) {
            console.error('No existing deal found or error fetching:', err);
        }
    },

    setupEventListeners() {
        const createForm = document.getElementById('createDealForm');
        if (createForm) {
            createForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                const btn = createForm.querySelector('button');
                UI.showLoading(btn);

                const data = {
                    title: document.getElementById('title').value,
                    description: document.getElementById('description').value,
                    fundingRequired: document.getElementById('fundingRequired').value,
                    equityOffered: document.getElementById('equityOffered').value
                };

                try {
                    await API.post('/startup/deals', data);
                    const isUpdate = btn.textContent === 'Update Deal';
                    UI.toast(isUpdate ? 'Deal updated successfully!' : 'Deal posted successfully!', 'success');
                    setTimeout(() => window.location.href = 'my_deals.html', 1500);
                } catch (err) {
                    UI.toast(err.message, 'error');
                } finally {
                    UI.hideLoading(btn);
                }
            });
        }
    }
};

document.addEventListener('DOMContentLoaded', () => Deals.init());

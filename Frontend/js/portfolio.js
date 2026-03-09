const Portfolio = {
    async init() {
        this.loadUserSession();
        await this.loadPortfolio();
    },

    loadUserSession() {
        const user = JSON.parse(localStorage.getItem(CONFIG.USER_KEY));
        if (!user || user.role !== 'INVESTOR') {
            window.location.href = '../login.html';
            return;
        }
    },

    async loadPortfolio() {
        const tableBody = document.getElementById('portfolioTableBody');
        try {
            const res = await API.get('/interests/portfolio');
            const portfolio = res.data;
            tableBody.innerHTML = '';

            if (portfolio.length === 0) {
                tableBody.innerHTML = '<tr><td colspan="5" style="text-align:center; padding: 3rem;" class="text-dim">You don\'t have any accepted investments in your portfolio yet.</td></tr>';
                this.updateStats([]);
                return;
            }

            let totalCapital = 0;
            let totalEquity = 0;

            portfolio.forEach(item => {
                totalCapital += parseFloat(item.investmentAmount || 0);
                totalEquity += parseFloat(item.equityRequested || 0);

                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td style="font-weight:600">${item.companyName}</td>
                    <td>₹${item.investmentAmount} Lakhs</td>
                    <td>${item.equityRequested}%</td>
                    <td><span class="badge badge-approved">ACTIVE HOLDING</span></td>
                    <td class="text-muted">${new Date(item.createdAt).toLocaleDateString()}</td>
                    <td>
                        <button onclick="Portfolio.downloadConfirmation(${JSON.stringify(item).replace(/"/g, '&quot;')})" class="btn btn-secondary btn-sm">
                            <i class="fas fa-download"></i> PDF
                        </button>
                    </td>
                `;
                tableBody.appendChild(tr);
            });

            this.updateStats(portfolio, totalCapital, totalEquity);

        } catch (err) {
            UI.toast('Failed to load portfolio', 'error');
            tableBody.innerHTML = '<tr><td colspan="6" style="text-align:center; padding: 3rem;" class="text-error">Error loading portfolio.</td></tr>';
        }
    },

    async downloadConfirmation(item) {
        const user = JSON.parse(localStorage.getItem(CONFIG.USER_KEY));
        const template = document.getElementById('pdfTemplate');

        // Populate Template
        document.getElementById('pdfCompanyName').textContent = item.companyName;
        document.getElementById('pdfInvestorName').textContent = user.name;
        document.getElementById('pdfAmount').textContent = `₹${item.investmentAmount} Lakhs`;
        document.getElementById('pdfEquity').textContent = `${item.equityRequested}%`;
        document.getElementById('pdfDate').textContent = new Date(item.createdAt).toLocaleDateString('en-IN', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });
        document.getElementById('pdfRefNumber').textContent = `REF: RO-INV-${new Date(item.createdAt).getFullYear()}-${Math.floor(1000 + Math.random() * 9000)}`;

        // Generate PDF
        const element = template.cloneNode(true);
        element.style.display = 'block';

        const opt = {
            margin: 0,
            filename: `Confirmation_${item.companyName.replace(/\s+/g, '_')}.pdf`,
            image: { type: 'jpeg', quality: 0.98 },
            html2canvas: { scale: 2, useCORS: true },
            jsPDF: { unit: 'in', format: 'a4', orientation: 'portrait' }
        };

        try {
            UI.toast('Generating PDF...', 'info');
            await html2pdf().set(opt).from(element).save();
            UI.toast('PDF Downloaded!', 'success');
        } catch (err) {
            console.error('PDF Error:', err);
            UI.toast('Failed to generate PDF', 'error');
        }
    },

    updateStats(portfolio, totalCapital, totalEquity) {
        document.getElementById('companiesCount').textContent = portfolio.length;
        document.getElementById('totalInvested').textContent = `₹${totalCapital.toLocaleString()} Lakhs`;
        const avgEq = portfolio.length > 0 ? (totalEquity / portfolio.length).toFixed(1) : 0;
        document.getElementById('avgEquity').textContent = `${avgEq}%`;
    }
};

document.addEventListener('DOMContentLoaded', () => Portfolio.init());

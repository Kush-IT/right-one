const StartupPortfolio = {
    async init() {
        this.loadUserSession();
        await this.loadCapTable();
    },

    loadUserSession() {
        const user = JSON.parse(localStorage.getItem(CONFIG.USER_KEY));
        if (!user || user.role !== 'STARTUP') {
            window.location.href = '../login.html';
            return;
        }
    },

    async loadCapTable() {
        const tbody = document.getElementById('capTableBody');
        try {
            const res = await API.get('/startup/portfolio');
            const data = res.data;
            tbody.innerHTML = '';

            if (data.length === 0) {
                tbody.innerHTML = '<tr><td colspan="5" style="text-align:center; padding: 3rem;" class="text-dim">No capital raised yet. Your cap table will appear here once you accept investment interests.</td></tr>';
                this.updateStats(0, 0, 0);
                return;
            }

            let totalInvested = 0;
            let totalEquity = 0;

            data.forEach(item => {
                totalInvested += parseFloat(item.investmentAmount || 0);
                totalEquity += parseFloat(item.equityRequested || 0);

                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td style="font-weight:600">${item.investorName}</td>
                    <td>${item.dealTitle}</td>
                    <td>₹${item.investmentAmount} Lakhs</td>
                    <td>${item.equityRequested}%</td>
                    <td class="text-muted">${new Date(item.createdAt).toLocaleDateString()}</td>
                    <td>
                        <button onclick="StartupPortfolio.downloadConfirmation(${JSON.stringify(item).replace(/"/g, '&quot;')})" class="btn btn-secondary btn-sm">
                            <i class="fas fa-download"></i> PDF
                        </button>
                    </td>
                `;
                tbody.appendChild(tr);
            });

            this.updateStats(totalInvested, data.length, totalEquity);

        } catch (err) {
            UI.toast('Failed to load cap table', 'error');
            tbody.innerHTML = '<tr><td colspan="6" style="text-align:center; padding: 3rem;" class="text-error">Error loading portfolio.</td></tr>';
        }
    },

    async downloadConfirmation(item) {
        const user = JSON.parse(localStorage.getItem(CONFIG.USER_KEY));
        const template = document.getElementById('pdfTemplate');

        // Populate Template
        const profileRes = await API.get('/startup/profile');
        const startupName = profileRes.data.companyName;

        document.getElementById('pdfStartupName').textContent = startupName;
        document.getElementById('pdfInvestorName').textContent = item.investorName;
        document.getElementById('pdfAmount').textContent = `₹${item.investmentAmount} Lakhs`;
        document.getElementById('pdfEquity').textContent = `${item.equityRequested}%`;
        document.getElementById('pdfDate').textContent = new Date(item.createdAt).toLocaleDateString('en-IN', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });
        document.getElementById('pdfRefNumber').textContent = `REF: RO-FUND-${new Date(item.createdAt).getFullYear()}-${Math.floor(1000 + Math.random() * 9000)}`;

        // Generate PDF
        const element = template.cloneNode(true);
        element.style.display = 'block';

        const opt = {
            margin: 0,
            filename: `Funding_Ref_${item.investorName.replace(/\s+/g, '_')}.pdf`,
            image: { type: 'jpeg', quality: 0.98 },
            html2canvas: { scale: 2, useCORS: true },
            jsPDF: { unit: 'in', format: 'a4', orientation: 'portrait' }
        };

        try {
            UI.toast('Generating PDF...', 'info');
            await html2pdf().set(opt).from(element).save();
            UI.toast('Confirmation Downloaded!', 'success');
        } catch (err) {
            console.error('PDF Error:', err);
            UI.toast('Failed to generate PDF', 'error');
        }
    },

    updateStats(raised, count, equity) {
        document.getElementById('totalRaised').textContent = `₹${raised.toLocaleString()} Lakhs`;
        document.getElementById('investorsCount').textContent = count;
        document.getElementById('totalEquity').textContent = `${equity.toFixed(1)}%`;
    }
};

document.addEventListener('DOMContentLoaded', () => StartupPortfolio.init());

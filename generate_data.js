const axios = require('axios');
const fs = require('fs');
const FormData = require('form-data');
const path = require('path');

const BASE_URL = 'http://localhost:8080/api';
const PASSWORD = 'kush1234';
const SAMPLE_FILES_DIR = 'c:/Users/kushp/OneDrive/Desktop/RIGHTONE/uploads/sample';

// Ensure sample directory exists
if (!fs.existsSync(SAMPLE_FILES_DIR)) {
    fs.mkdirSync(SAMPLE_FILES_DIR, { recursive: true });
}

// Create placeholder files if they don't exist
['aadhar.png', 'pan.png', 'bank.pdf', 'cert.pdf'].forEach(file => {
    const filePath = path.join(SAMPLE_FILES_DIR, file);
    if (!fs.existsSync(filePath)) {
        fs.writeFileSync(filePath, 'Placeholder content for ' + file);
    }
});

const indianUsers = [
    { name: 'Rahul Sharma', email: 'rahul.sharma@gmail.com', role: 'STARTUP', city: 'Mumbai', state: 'Maharashtra' },
    { name: 'Priya Patel', email: 'priya.patel@gmail.com', role: 'INVESTOR', city: 'Ahmedabad', state: 'Gujarat' },
    { name: 'Sneha Rao', email: 'sneha.rao@gmail.com', role: 'INVESTOR', city: 'Bangalore', state: 'Karnataka' }
];

const startupBusinesses = [
    { name: 'TechNova Solutions', type: 'Private Limited', website: 'https://technova.in' },
    { name: 'FinEdge AI', type: 'Startup', website: 'https://finedge.ai' },
    { name: 'GreenEnergy Labs', type: 'LLP', website: 'https://greenenergy.in' },
    { name: 'NextGen Robotics', type: 'Private Limited', website: 'https://nextgen.io' },
    { name: 'DigitalKart', type: 'Startup', website: 'https://digitalkart.com' },
    { name: 'HealthFlow', type: 'Private Limited', website: 'https://healthflow.in' },
    { name: 'AgriSmart', type: 'Startup', website: 'https://agrismart.co' },
    { name: 'EduPulse', type: 'LLP', website: 'https://edupulse.edu' }
];

async function run() {
    console.log('🚀 Starting Test Data Generation...');

    for (const user of indianUsers) {
        try {
            console.log(`\n👤 Processing User: ${user.name} (${user.role})`);

            // 1. Signup
            await axios.post(`${BASE_URL}/auth/signup`, {
                fullName: user.name,
                email: user.email,
                password: PASSWORD,
                role: user.role
            });
            console.log('✅ Signup successful');

            // 2. Login to get Token
            const loginRes = await axios.post(`${BASE_URL}/auth/login`, {
                email: user.email,
                password: PASSWORD
            });
            const token = loginRes.data.token;
            const authHeader = { Authorization: `Bearer ${token}` };
            console.log('✅ Login successful');

            // 3. Submit KYC
            const form = new FormData();
            form.append('fullName', user.name);
            form.append('dateOfBirth', '1990-01-01');
            form.append('gender', Math.random() > 0.5 ? 'Male' : 'Female');
            form.append('nationality', 'Indian');
            form.append('phoneNumber', '91' + Math.floor(1000000000 + Math.random() * 9000000000));
            form.append('email', user.email);
            form.append('addressLine1', 'Street ' + Math.floor(Math.random() * 100));
            form.append('addressLine2', 'Area ' + user.city);
            form.append('city', user.city);
            form.append('state', user.state);
            form.append('country', 'India');
            form.append('postalCode', Math.floor(100000 + Math.random() * 900000).toString());
            form.append('identityType', 'Aadhar Card');
            form.append('identityNumber', Math.floor(1000 + Math.random() * 9000) + ' ' + Math.floor(1000 + Math.random() * 9000) + ' ' + Math.floor(1000 + Math.random() * 9000));
            form.append('panNumber', 'ABCDE' + Math.floor(1000 + Math.random() * 9000) + 'F');
            form.append('bankName', ['SBI', 'HDFC', 'ICICI', 'Axis'][Math.floor(Math.random() * 4)]);
            form.append('bankAccountNumber', Math.floor(1000000000 + Math.random() * 9000000000).toString());
            form.append('ifscCode', 'SBIN0000456');

            if (user.role === 'STARTUP') {
                const biz = startupBusinesses.pop() || { name: user.name + ' Ventures', type: 'Private Limited', website: 'https://example.com' };
                form.append('businessName', biz.name);
                form.append('businessType', biz.type);
                form.append('businessRegistrationNumber', 'REG' + Math.floor(100000 + Math.random() * 900000));
                form.append('businessWebsite', biz.website);
            } else {
                form.append('businessName', '');
                form.append('businessType', '');
                form.append('businessRegistrationNumber', '');
                form.append('businessWebsite', '');
            }
            form.append('termsAccepted', 'true');

            // Files
            form.append('aadharCard', fs.createReadStream(path.join(SAMPLE_FILES_DIR, 'aadhar.png')));
            form.append('panCard', fs.createReadStream(path.join(SAMPLE_FILES_DIR, 'pan.png')));
            form.append('bankStatement', fs.createReadStream(path.join(SAMPLE_FILES_DIR, 'bank.pdf')));
            form.append('businessCertificate', fs.createReadStream(path.join(SAMPLE_FILES_DIR, 'cert.pdf')));

            const kycRes = await axios.post(`${BASE_URL}/kyc/submit`, form, {
                headers: {
                    ...authHeader,
                    ...form.getHeaders()
                }
            });
            const kycId = kycRes.data.data.id;
            console.log('✅ KYC submitted');

            // 4. Randomly Update KYC Status (Admin Bypass)
            const status = ['APPROVE', 'REJECT', 'PENDING'][Math.floor(Math.random() * 3)];
            if (status !== 'PENDING') {
                await axios.put(`${BASE_URL}/admin/kyc/${status.toLowerCase()}/${kycId}`, {});
                console.log(`✅ KYC ${status}D`);
            }

            // 5. Create Deals for Startups
            if (user.role === 'STARTUP') {
                await axios.post(`${BASE_URL}/startup/deals`, {
                    title: `Investment opportunity in ${user.name}'s project`,
                    description: 'Revolutionizing the industry with AI and Blockchain.',
                    requiredFunding: (Math.floor(50 + Math.random() * 150) * 100000).toString(),
                    startupName: user.name
                }, { headers: authHeader });
                console.log('✅ Startup Deal created');
            }

        } catch (err) {
            console.error(`❌ Error processing ${user.name}:`, err.response ? err.response.data : err.message);
        }
    }

    // 6. Express Interest for Investors
    console.log('\n🤝 Creating Investor Interests...');
    try {
        // Fetch all open deals
        const investor = indianUsers.find(u => u.role === 'INVESTOR');
        const iLogin = await axios.post(`${BASE_URL}/auth/login`, { email: investor.email, password: PASSWORD });
        const iToken = iLogin.data.token;
        const dealsRes = await axios.get(`${BASE_URL}/investor/deals/open`, { headers: { Authorization: `Bearer ${iToken}` } });
        const deals = dealsRes.data.data;

        for (const user of indianUsers.filter(u => u.role === 'INVESTOR')) {
            const loginRes = await axios.post(`${BASE_URL}/auth/login`, { email: user.email, password: PASSWORD });
            const authHeader = { Authorization: `Bearer ${loginRes.data.token}` };

            const targetDeal = deals[Math.floor(Math.random() * deals.length)];
            if (targetDeal) {
                await axios.post(`${BASE_URL}/investor/deals/${targetDeal.id}/interest`, {}, { headers: authHeader });
                console.log(`✅ ${user.name} expressed interest in deal #${targetDeal.id}`);
            }
        }
    } catch (err) {
        console.error('❌ Error creating interests:', err.message);
    }

    console.log('\n✨ Test Data Generation Complete!');
}

run();

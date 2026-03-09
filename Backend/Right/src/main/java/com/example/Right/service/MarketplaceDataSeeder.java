package com.example.Right.service;

import com.example.Right.model.*;
import com.example.Right.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * MarketplaceDataSeeder - Senior Developer Implementation
 * Automatically populates the platform with 100 users, 120 deals, and 300
 * interests
 * for a realistic marketplace experience.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MarketplaceDataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final StartupRepository startupRepository;
    private final InvestorRepository investorRepository;
    private final KycRepository kycRepository;
    private final DealRepository dealRepository;
    private final InvestmentInterestRepository interestRepository;
    private final PasswordEncoder passwordEncoder;

    private final Random random = new Random();

    @Override
    @Transactional
    public void run(String... args) {
        log.info("🚀 Marketplace Data Seeder checking for data...");
        patchExistingData();
        patchExistingKycData();

        if (userRepository.count() > 0) {
            log.info("Database already contains data. Skipping fresh marketplace seeding.");
            return;
        }
        log.info("🚀 Starting Marketplace Data Seeding (CommandLineRunner)...");

        try {
            String commonPassword = passwordEncoder.encode("kush1234");

            // 1. Generate 16 Startups and 10 Investors
            List<User> startupUsers = seedUsers(16, Role.STARTUP, commonPassword);
            List<User> investorUsers = seedUsers(10, Role.INVESTOR, commonPassword);

            // 2. Generate Profiles
            seedStartupProfiles(startupUsers);
            seedInvestorProfiles(investorUsers);

            // 3. Generate KYC Records
            List<User> allUsers = new ArrayList<>(startupUsers);
            allUsers.addAll(investorUsers);
            seedKycRecords(allUsers);

            // 4. Generate 16 Deals (1 per startup)
            List<Deal> deals = seedDeals(16);

            // 5. Generate ~40 Investment Interests
            seedInvestmentInterests(40, deals);

            log.info("✨ Marketplace Data Seeding Completed Successfully.");
            log.info("📊 Stats: 26 Users | 16 Startups | 10 Investors | 16 Deals | 40 Interests");

        } catch (Exception e) {
            log.error("❌ Error during data seeding: {}", e.getMessage(), e);
        }
    }

    private List<User> seedUsers(int count, Role role, String password) {
        String[] firstNames = { "Rahul", "Priya", "Amit", "Sneha", "Rohit", "Ananya", "Karan", "Neha", "Arjun", "Pooja",
                "Vikram", "Riya", "Aditya", "Kunal", "Meera", "Akshat", "Kush", "Priyanshu", "Shubham" };
        String[] lastNames = { "Sharma", "Patel", "Verma", "Iyer", "Gupta", "Singh", "Mehta", "Kapoor", "Nair", "Shah",
                "Joshi", "Desai" };

        List<User> users = new ArrayList<>();
        int startIndex = (role == Role.STARTUP) ? 1 : 71;

        for (int i = 0; i < count; i++) {
            String fName = firstNames[random.nextInt(firstNames.length)];
            String lName = lastNames[random.nextInt(lastNames.length)];
            String fullName = fName + " " + lName;
            String email = fName.toLowerCase() + "." + lName.toLowerCase() + (startIndex + i) + "@gmail.com";

            User user = User.builder()
                    .name(fullName)
                    .email(email)
                    .password(password)
                    .role(role)
                    .build();
            userRepository.save(user);
            users.add(user);
        }
        return users;
    }

    private void seedStartupProfiles(List<User> users) {
        String[] companies = { "TechNova", "GreenEdge Energy", "FarmAI Solutions", "FinBridge", "EduSmart",
                "NextGen Robotics", "SolarGrid", "QuickLogix", "SecureNet", "DataVista", "AgroBoost", "UrbanMobility",
                "MediTrack", "CodeForge", "EcoWave" };
        String[] sectors = { "AI", "FinTech", "HealthTech", "EdTech", "Clean Energy", "Agritech", "E-commerce",
                "Logistics", "SaaS", "Cybersecurity", "Blockchain", "Robotics" };
        String[] valuations = { "₹2 Crore", "₹5 Crore", "₹10 Crore", "₹25 Crore" };
        String[] locations = { "Bangalore", "Mumbai", "Delhi", "Hyderabad", "Pune", "Chennai", "Gurgaon" };

        for (User user : users) {
            String sector = sectors[random.nextInt(sectors.length)];
            StartupProfile profile = StartupProfile.builder()
                    .user(user)
                    .companyName(companies[random.nextInt(companies.length)] + " " + (100 + random.nextInt(900)))
                    .sector(sector)
                    .valuation(valuations[random.nextInt(valuations.length)])
                    .businessWebsite("https://www." + user.getName().toLowerCase().replace(" ", "") + ".io")
                    .description("Developing cutting-edge solutions in the " + sector + " space to disrupt the market.")
                    .location(locations[random.nextInt(locations.length)])
                    .foundingYear(2018 + random.nextInt(6))
                    .kycStatus(KYCStatus.PENDING)
                    .build();
            startupRepository.save(profile);
        }
    }

    private void seedInvestorProfiles(List<User> users) {
        String[] fundSizes = { "₹1 Crore", "₹5 Crore", "₹10 Crore", "₹25 Crore", "₹50 Crore" };
        String[] preferences = { "AI", "FinTech", "HealthTech", "SaaS", "DeepTech", "ClimateTech", "Agritech" };
        String[] locations = { "Mumbai", "Bangalore", "Singapore", "Dubai", "Delhi" };

        for (User user : users) {
            InvestorProfile profile = InvestorProfile.builder()
                    .user(user)
                    .fundSize(fundSizes[random.nextInt(fundSizes.length)])
                    .investmentPreference(preferences[random.nextInt(preferences.length)])
                    .location(locations[random.nextInt(locations.length)])
                    .experienceYears(3 + random.nextInt(15))
                    .portfolioSize(1 + random.nextInt(12))
                    .kycStatus(KYCStatus.PENDING)
                    .build();
            investorRepository.save(profile);
        }
    }

    private void seedKycRecords(List<User> users) {
        for (User user : users) {
            // Distribution: 50% APPROVED, 30% PENDING, 20% REJECTED
            KYCStatus status;
            int r = random.nextInt(100);
            if (r < 50)
                status = KYCStatus.APPROVED;
            else if (r < 80)
                status = KYCStatus.PENDING;
            else
                status = KYCStatus.REJECTED;

            String bizName = "";
            if (user.getRole() == Role.STARTUP) {
                bizName = startupRepository.findByUser(user).map(StartupProfile::getCompanyName)
                        .orElse("Unknown Startup");
            }

            KycDetails kyc = new KycDetails();
            kyc.setUserId(user.getId());
            kyc.setFullName(user.getName());
            kyc.setEmail(user.getEmail());
            kyc.setPhoneNumber("+91 " + (9000000000L + random.nextInt(999999999)));
            kyc.setDateOfBirth(
                    "19" + (70 + random.nextInt(30)) + "-" + (1 + random.nextInt(12)) + "-" + (1 + random.nextInt(28)));
            kyc.setGender(random.nextBoolean() ? "Male" : "Female");
            kyc.setNationality("Indian");
            kyc.setAddressLine1("H-No " + random.nextInt(500) + ", Main Road");
            kyc.setAddressLine2(user.getRole() == Role.STARTUP ? "Sector 5" : "Bandra West");
            kyc.setCity("Mumbai");
            kyc.setState("Maharashtra");
            kyc.setCountry("India");
            kyc.setPostalCode("400001");
            kyc.setIdentityType("Aadhar Card");
            kyc.setIdentityNumber(String.valueOf(200000000000L + random.nextLong(700000000000L)));
            kyc.setPanNumber("ABCDE" + (1000 + random.nextInt(8999)) + "F");
            kyc.setBankName(random.nextBoolean() ? "HDFC Bank" : "ICICI Bank");
            kyc.setBankAccountNumber(String.valueOf(501000000000L + random.nextLong(400000000000L)));
            kyc.setIfscCode("HDFC000" + (1000 + random.nextInt(8999)));
            kyc.setBusinessName(bizName);
            kyc.setBusinessType(user.getRole() == Role.STARTUP ? "Private Limited" : "N/A");
            kyc.setBusinessRegistrationNumber(
                    user.getRole() == Role.STARTUP
                            ? "U" + (10000 + random.nextInt(89999)) + "MH" + (2010 + random.nextInt(14)) + "PTC"
                                    + (100000 + random.nextInt(899999))
                            : "N/A");
            kyc.setBusinessWebsite(
                    user.getRole() == Role.STARTUP ? "https://www." + bizName.toLowerCase().replace(" ", "") + ".com"
                            : "N/A");
            kyc.setStatus(status);
            kyc.setTermsAccepted(true);
            kyc.setSubmittedAt(LocalDateTime.now().minusDays(random.nextInt(15)));
            kycRepository.save(kyc);

            // Sync with profile
            if (user.getRole() == Role.STARTUP) {
                startupRepository.findByUser(user).ifPresent(p -> {
                    p.setKycStatus(status);
                    startupRepository.save(p);
                });
            } else {
                investorRepository.findByUser(user).ifPresent(p -> {
                    p.setKycStatus(status);
                    investorRepository.save(p);
                });
            }
        }
    }

    private List<Deal> seedDeals(int count) {
        String[] titles = { "AI Healthcare Platform", "Smart Farming System", "EV Charging Network",
                "FinTech Payment Gateway", "Drone Logistics Platform", "Online Learning AI Tutor",
                "Cybersecurity Monitoring SaaS", "Smart City IoT Platform", "Blockchain Supply Chain",
                "Climate Data Analytics", "AI Legal Assistant", "Remote Work Productivity Tool",
                "Smart Retail Analytics", "Digital Insurance Platform", "Green Hydrogen Energy" };
        String[] amounts = { "₹25,00,000", "₹50,00,000", "₹75,00,000", "₹1,00,00,000", "₹2,50,00,000" };

        List<StartupProfile> startups = startupRepository.findAll();
        List<Deal> deals = new ArrayList<>();

        // Ensure each startup has exactly one deal
        for (StartupProfile startup : startups) {
            Deal deal = Deal.builder()
                    .startup(startup)
                    .title(titles[random.nextInt(titles.length)])
                    .description(
                            "We are revolutionizing the marketplace with our proprietary technology. Seeking capital for scale.")
                    .fundingRequired(amounts[random.nextInt(amounts.length)])
                    .equityOffered((5 + random.nextInt(16)) + "%")
                    .status(DealStatus.OPEN)
                    .build();
            dealRepository.save(deal);
            deals.add(deal);
        }
        return deals;
    }

    private void seedInvestmentInterests(int count, List<Deal> deals) {
        String[] messages = { "We are interested in funding your startup.", "Looking forward to discussing investment.",
                "Your startup aligns with our portfolio.", "We would like to schedule a meeting." };
        List<InvestorProfile> investors = investorRepository.findAll();

        for (int i = 0; i < count; i++) {
            Deal deal = deals.get(random.nextInt(deals.size()));
            InvestorProfile investor = investors.get(random.nextInt(investors.size()));

            // Distribution: 60% PENDING, 25% ACCEPTED, 15% REJECTED
            InterestStatus status;
            int r = random.nextInt(100);
            if (r < 60)
                status = InterestStatus.PENDING;
            else if (r < 85)
                status = InterestStatus.ACCEPTED;
            else
                status = InterestStatus.REJECTED;

            InvestmentInterest interest = InvestmentInterest.builder()
                    .deal(deal)
                    .investor(investor)
                    .investmentAmount((double) (10 + random.nextInt(90))) // In Lakhs as per earlier setup
                    .equityRequested((double) (2 + random.nextInt(10)))
                    .message(messages[random.nextInt(messages.length)])
                    .status(status)
                    .build();
            interestRepository.save(interest);
        }
    }

    private void patchExistingData() {
        List<InvestmentInterest> interests = interestRepository.findAll();
        boolean patched = false;
        String[] messages = { "We are interested in funding your startup.", "Looking forward to discussing investment.",
                "Your startup aligns with our portfolio.", "We would like to schedule a meeting." };

        for (InvestmentInterest interest : interests) {
            if (interest.getInvestmentAmount() == null || interest.getEquityRequested() == null
                    || interest.getMessage() == null) {
                if (interest.getInvestmentAmount() == null) {
                    interest.setInvestmentAmount((double) (10 + random.nextInt(90)));
                }
                if (interest.getEquityRequested() == null) {
                    interest.setEquityRequested((double) (2 + random.nextInt(10)));
                }
                if (interest.getMessage() == null) {
                    interest.setMessage(messages[random.nextInt(messages.length)]);
                }
                interestRepository.save(interest);
                patched = true;
            }
        }
        if (patched) {
            log.info("✅ Patched existing investment interests with missing data.");
        }
    }

    private void patchExistingKycData() {
        List<KycDetails> kycList = kycRepository.findAll();
        boolean patched = false;

        for (KycDetails kyc : kycList) {
            boolean currentPatched = false;

            if (kyc.getDateOfBirth() == null || kyc.getDateOfBirth().isEmpty()) {
                kyc.setDateOfBirth("19" + (70 + random.nextInt(30)) + "-" + (1 + random.nextInt(12)) + "-"
                        + (1 + random.nextInt(28)));
                currentPatched = true;
            }
            if (kyc.getGender() == null || kyc.getGender().isEmpty()) {
                kyc.setGender(random.nextBoolean() ? "Male" : "Female");
                currentPatched = true;
            }
            if (kyc.getNationality() == null || kyc.getNationality().isEmpty()) {
                kyc.setNationality("Indian");
                currentPatched = true;
            }
            if (kyc.getIdentityType() == null || kyc.getIdentityType().isEmpty()) {
                kyc.setIdentityType("Aadhar Card");
                currentPatched = true;
            }
            if (kyc.getBankName() == null || kyc.getBankName().isEmpty()) {
                kyc.setBankName(random.nextBoolean() ? "HDFC Bank" : "ICICI Bank");
                currentPatched = true;
            }
            if (kyc.getIfscCode() == null || kyc.getIfscCode().isEmpty()) {
                kyc.setIfscCode("HDFC000" + (1000 + random.nextInt(8999)));
                currentPatched = true;
            }

            // For Startups, ensure business data is present
            User user = userRepository.findById(kyc.getUserId()).orElse(null);
            if (user != null && user.getRole() == Role.STARTUP) {
                if (kyc.getBusinessType() == null || kyc.getBusinessType().isEmpty()
                        || "N/A".equals(kyc.getBusinessType())) {
                    kyc.setBusinessType("Private Limited");
                    currentPatched = true;
                }
                if (kyc.getBusinessRegistrationNumber() == null || kyc.getBusinessRegistrationNumber().isEmpty()
                        || "N/A".equals(kyc.getBusinessRegistrationNumber())) {
                    kyc.setBusinessRegistrationNumber("U" + (10000 + random.nextInt(89999)) + "MH2021PTC123456");
                    currentPatched = true;
                }
                if (kyc.getBusinessWebsite() == null || kyc.getBusinessWebsite().isEmpty()
                        || "N/A".equals(kyc.getBusinessWebsite())) {
                    kyc.setBusinessWebsite(
                            "https://www." + kyc.getBusinessName().toLowerCase().replace(" ", "") + ".com");
                    currentPatched = true;
                }
            } else {
                // For Investors, ensure business data is marked N/A if missing
                if (kyc.getBusinessType() == null || kyc.getBusinessType().isEmpty())
                    kyc.setBusinessType("N/A");
                if (kyc.getBusinessRegistrationNumber() == null || kyc.getBusinessRegistrationNumber().isEmpty())
                    kyc.setBusinessRegistrationNumber("N/A");
                if (kyc.getBusinessWebsite() == null || kyc.getBusinessWebsite().isEmpty())
                    kyc.setBusinessWebsite("N/A");
            }

            if (currentPatched) {
                kycRepository.save(kyc);
                patched = true;
            }
        }

        if (patched) {
            log.info("✅ Patched existing KYC records with missing data.");
        }
    }
}

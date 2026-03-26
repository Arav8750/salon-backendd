package com.salon.config;

import com.salon.model.Barber;
import com.salon.model.SalonService;
import com.salon.model.User;
import com.salon.repository.BarberRepository;
import com.salon.repository.SalonServiceRepository;
import com.salon.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private BarberRepository barberRepository;
    @Autowired private SalonServiceRepository salonServiceRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // ── 1. Admin account ──────────────────────────────
        if (!userRepository.existsByEmail("admin@salon.com")) {
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail("admin@salon.com");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setPhone("9999999999");
            admin.setRole(User.Role.ADMIN);
            admin.setIsActive(true);
            userRepository.save(admin);
            System.out.println(">> Admin created: admin@salon.com / Admin@123");
        }

        // ── 2. Sample Barbers ─────────────────────────────
        seedBarber("Ravi Kumar",   "ravi@salon.com",   "9111111111", "Hair & Beard",  5);
        seedBarber("Suresh Patel", "suresh@salon.com", "9222222222", "Hair Color",    3);
        seedBarber("Arjun Singh",  "arjun@salon.com",  "9333333333", "All Services",  7);

        // ── 3. Sample Services ────────────────────────────
        if (salonServiceRepository.count() == 0) {
            String[][] services = {
                {"Haircut",            "Classic haircut with styling",                    "200", "30"},
                {"Beard Trim",         "Professional beard shaping and trim",             "150", "20"},
                {"Hair + Beard Combo", "Complete haircut and beard grooming package",     "300", "45"},
                {"Head Massage",       "Relaxing scalp massage with oil",                 "250", "30"},
                {"Hair Color",         "Full hair coloring with premium color",           "800", "90"},
                {"Facial",             "Deep cleansing facial treatment",                 "500", "60"},
                {"Kids Haircut",       "Gentle haircut for children under 12",            "150", "20"},
                {"Shave",              "Clean hot-towel straight razor shave",            "100", "15"},
            };
            for (String[] s : services) {
                SalonService svc = new SalonService();
                svc.setName(s[0]);
                svc.setDescription(s[1]);
                svc.setPrice(Double.parseDouble(s[2]));
                svc.setDurationMinutes(Integer.parseInt(s[3]));
                svc.setIsActive(true);
                salonServiceRepository.save(svc);
            }
            System.out.println(">> " + services.length + " sample services created.");
        }

        System.out.println("====================================================");
        System.out.println("  SmartSalon is ready!");
        System.out.println("  Admin  : admin@salon.com  / Admin@123");
        System.out.println("  Barber : ravi@salon.com   / Barber@123");
        System.out.println("  Barber : suresh@salon.com / Barber@123");
        System.out.println("  Barber : arjun@salon.com  / Barber@123");
        System.out.println("  (Register as customer via the app)");
        System.out.println("====================================================");
    }

    private void seedBarber(String name, String email, String phone, String spec, int exp) {
        if (!userRepository.existsByEmail(email)) {
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode("Barber@123"));
            user.setPhone(phone);
            user.setRole(User.Role.BARBER);
            user.setIsActive(true);
            userRepository.save(user);

            Barber barber = new Barber();
            barber.setUser(user);
            barber.setSpecialization(spec);
            barber.setExperienceYears(exp);
            barber.setStatus(Barber.BarberStatus.AVAILABLE);
            barber.setIsActive(true);
            barberRepository.save(barber);

            System.out.println(">> Barber created: " + email + " / Barber@123");
        }
    }
}

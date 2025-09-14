package com.hitanshudhawan.sankshipt.config;

import com.hitanshudhawan.sankshipt.models.Role;
import com.hitanshudhawan.sankshipt.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * DataLoader is responsible for initializing the database with required seed data
 * when the application starts up. This ensures that essential data like roles
 * are available immediately after the application deployment.
 */
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;

    /**
     * This method is automatically executed by Spring Boot after the application context
     * has been loaded and all beans have been initialized. It's the entry point for
     * our data seeding process.
     */
    @Override
    public void run(String... args) throws Exception {
        loadRoles();
    }

    /**
     * Seeds the database with default roles (ADMIN and USER) if they don't already exist.
     * This method checks if roles are already present to avoid duplicate entries
     * and ensures that the application always has the necessary roles available.
     */
    private void loadRoles() {
        // Check if any roles already exist in the database to avoid duplicates
        if (roleRepository.count() == 0) {
            // Create the ADMIN role
            Role adminRole = new Role();
            adminRole.setValue("ADMIN");

            // Create the USER role
            Role userRole = new Role();
            userRole.setValue("USER");

            // Save both roles to the database
            roleRepository.save(adminRole);
            roleRepository.save(userRole);

            System.out.println("Roles seeded successfully");
        } else {
            System.out.println("Roles already exist, skipping seeding");
        }
    }

}

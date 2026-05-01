package hu.okrim.droneprojectmanager.service;

import hu.okrim.droneprojectmanager.dto.ApiResponse;
import hu.okrim.droneprojectmanager.dto.UserRequestDto;
import hu.okrim.droneprojectmanager.model.User;
import hu.okrim.droneprojectmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Long generateAccountNumber() {
        Long accountNumber;

        do {
            // Generate a random 12-digit number
            accountNumber = ThreadLocalRandom.current().nextLong(1_000_000_000_000L, 10_000_000_000_000L);
        } while (userRepository.findByAccountNumber(accountNumber).isPresent());

        return accountNumber;
    }

    @Override
    public Optional<User> findByAccountNumber(Long accountNumber) {
        return userRepository.findByAccountNumber(accountNumber);
    }

    @Override
    @Transactional // Transactional makes sure there are no orphan schemas in case of failure
    public ApiResponse registerUser(UserRequestDto userRequestDto) {
        // Check if account number already exists
        if (userRepository.findByAccountNumber(userRequestDto.accountNumber()).isPresent()) {
            return new ApiResponse(false, "Account number already exists!", null);
        }

        // Create and save user
        User newUser = new User();
        newUser.setAccountNumber(userRequestDto.accountNumber());
        newUser.setPassword(passwordEncoder.encode(userRequestDto.password()));
        newUser.setSchema(UUID.randomUUID());
        newUser.setRegistrationDate(Instant.now());

        createTenantSchema(newUser.getSchema());
        userRepository.save(newUser);

        // Return success response
        return new ApiResponse(true, "Registration successful!", newUser);
    }

    @Override
    @Transactional
    public void updateLastLogin(User user) {
        user.setLastLogin(Instant.now());
        userRepository.save(user);
    }

    /**
     * Creates a new tenant-specific schema in the database and initializes it by executing
     * the create_tenant_schema.sql script. This is used when registering a new user.
     *
     * @param schemaName The unique identifier (UUID) representing the name of the schema to be created.
     */
    private void createTenantSchema(UUID schemaName) {
        // Step 1: Create the schema
        String createSchemaSql = String.format("CREATE SCHEMA IF NOT EXISTS \"%s\"", schemaName);
        jdbcTemplate.execute(createSchemaSql);

        // Step 2: Read "create_tenant_schema.sql" file
        try {
            // Load the SQL script file from resources
            File sqlFile = ResourceUtils.getFile("classpath:create_tenant_schema.sql");
            String tenantSchemaSql = new String(Files.readAllBytes(sqlFile.toPath()));

            // Step 3: Dynamically prefix the tenant schema name to the script
            String initSchemaSql = tenantSchemaSql.replace("{{schema}}", schemaName.toString());

            // Step 4: Execute the tenant-specific SQL
            jdbcTemplate.execute(String.format("SET search_path TO \"%s\"", schemaName)); // Switch schema context
            jdbcTemplate.execute(initSchemaSql);

            // Step 5: Switch back to the public schema
            jdbcTemplate.execute("SET search_path TO public");

        } catch (IOException e) {
            throw new RuntimeException("Failed to read tenant schema SQL file", e);
        }
    }
}

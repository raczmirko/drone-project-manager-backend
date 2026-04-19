package hu.okrim.droneprojectmanager.service;

import hu.okrim.droneprojectmanager.dto.ApiResponse;
import hu.okrim.droneprojectmanager.dto.UserDto;
import hu.okrim.droneprojectmanager.model.User;
import hu.okrim.droneprojectmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

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
    public ApiResponse registerUser(UserDto userDto) {
        // Check if account number already exists
        if (userRepository.findByAccountNumber(Long.valueOf(userDto.getAccountNumber())).isPresent()) {
            return new ApiResponse(false, "Account number already exists!", null);
        }

        // Create and save user
        User newUser = new User();
        newUser.setAccountNumber(Long.valueOf(userDto.getAccountNumber()));
        newUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        newUser.setSchema(UUID.randomUUID());
        newUser.setRegistrationDate(Instant.now());

        userRepository.save(newUser);

        // Return success response
        return new ApiResponse(true, "Registration successful!", newUser);
    }

//    private void createTenantSchema(UUID schemaName) {
//        String createSchemaSql = String.format("CREATE SCHEMA IF NOT EXISTS \"%s\"", schemaName);
//        jdbcTemplate.execute(createSchemaSql);
//
//        // Populate the schema with tables (e.g., Tenant-specific schema.sql)
//        String initSchemaSql = String.format("SET SCHEMA '%s'; <Your Table Creation Script>", schemaName);
//        jdbcTemplate.execute(initSchemaSql);
//    }
}

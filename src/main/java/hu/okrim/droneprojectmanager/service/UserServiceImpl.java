package hu.okrim.droneprojectmanager.service;

import hu.okrim.droneprojectmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Long generateAccountNumber() {
        Long accountNumber;

        do {
            // Generate a random 12-digit number
            accountNumber = ThreadLocalRandom.current().nextLong(1_000_000_000_000L, 10_000_000_000_000L);
        } while (userRepository.findByAccountNumber(accountNumber).isPresent());

        return accountNumber;
    }
}

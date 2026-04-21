package hu.okrim.droneprojectmanager.service;

import hu.okrim.droneprojectmanager.dto.ApiResponse;
import hu.okrim.droneprojectmanager.dto.UserRequestDto;
import hu.okrim.droneprojectmanager.model.User;

import java.util.Optional;

public interface UserService {

    /**
     * Generates a unique 12-digit account number.
     *
     * @return a newly generated unique account number as a Long
     */
    Long generateAccountNumber();

    /**
     * Find a user by account number.
     *
     * @return an Optional containing the User object if found, otherwise empty
     */
    Optional<User> findByAccountNumber(Long accountNumber);

    /**
     * Registers a new user in the system using the provided user details.
     *
     * @param userRequestDto an object containing user details such as account number and password
     * @return an ApiResponse object containing the operation's success status, a descriptive message, and any additional data
     */
    ApiResponse registerUser(UserRequestDto userRequestDto);

}

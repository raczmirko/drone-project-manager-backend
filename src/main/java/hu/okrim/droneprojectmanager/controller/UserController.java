package hu.okrim.droneprojectmanager.controller;

import hu.okrim.droneprojectmanager.dto.ApiResponse;
import hu.okrim.droneprojectmanager.dto.UserRequestDto;
import hu.okrim.droneprojectmanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Generate a new account number.
     * @return The generated account number as a string.
     */
    @GetMapping("/generate-account-number")
    public String generateAccountNumber() {
        return userService.generateAccountNumber().toString();
    }

    /**
     * Register a new user.
     * @param userRequestDto The user data.
     * @return A ResponseEntity indicating success or failure.
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody UserRequestDto userRequestDto) {
        ApiResponse response = userService.registerUser(userRequestDto);
        return ResponseEntity.status(response.isSuccess() ? 201 : 400).body(response);
    }

}

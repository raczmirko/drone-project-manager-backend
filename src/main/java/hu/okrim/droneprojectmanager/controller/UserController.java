package hu.okrim.droneprojectmanager.controller;

import hu.okrim.droneprojectmanager.dto.ApiResponse;
import hu.okrim.droneprojectmanager.dto.UserDto;
import hu.okrim.droneprojectmanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/generate-account-number")
    public String generateAccountNumber() {
        return userService.generateAccountNumber().toString();
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody UserDto userDto) {
        ApiResponse response = userService.registerUser(userDto);
        return ResponseEntity.status(response.isSuccess() ? 201 : 400).body(response);
    }

}

package hu.okrim.droneprojectmanager.controller;

import hu.okrim.droneprojectmanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @RequestMapping("/generate-account-number")
    public String generateAccountNumber() {
        return userService.generateAccountNumber().toString();
    }

}

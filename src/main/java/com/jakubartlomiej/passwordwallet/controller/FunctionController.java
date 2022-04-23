package com.jakubartlomiej.passwordwallet.controller;

import com.jakubartlomiej.passwordwallet.model.FunctionRun;
import com.jakubartlomiej.passwordwallet.model.User;
import com.jakubartlomiej.passwordwallet.service.FunctionRunService;
import com.jakubartlomiej.passwordwallet.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
@AllArgsConstructor
public class FunctionController {
    private final FunctionRunService functionRunService;
    private final UserService userService;

    @GetMapping("/function")
    public String showUserFunction(Principal principal, Model model) {
        String login = principal.getName();
        User user = userService.findByLogin(login);
        List<FunctionRun> functionRuns = functionRunService.findByUserId(user.getId());
        model.addAttribute("username", user.getLogin());
        model.addAttribute("functions", functionRuns);
        return "function/home";
    }
}

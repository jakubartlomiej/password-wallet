package com.jakubartlomiej.passwordwallet.controller;

import com.jakubartlomiej.passwordwallet.model.User;
import com.jakubartlomiej.passwordwallet.service.UserService;
import com.jakubartlomiej.passwordwallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final WalletService walletService;

    @GetMapping("/login")
    public String getLoginPage(@ModelAttribute("success") String success, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "user/login";
        }
        model.addAttribute("success", success);
        return "redirect:/";
    }

    @GetMapping("/register")
    public String getRegisterPage(@ModelAttribute("error") String error, Model model) {
        model.addAttribute("error", error);
        return "user/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String hashType,
                           RedirectAttributes redirectAttrs) {
        if (!userService.checkIfLoginAvailable(username)) {
            redirectAttrs.addFlashAttribute("error", "User login is not available ! ! !");
            return "redirect:/register";
        }
        userService.register(username, password, hashType);
        redirectAttrs.addFlashAttribute("success", "User successfully created ! ! ! Please login.");
        return "redirect:/login";
    }

    @GetMapping("/edit")
    public String getEditPasswordPage() {
        return "user/edit";
    }

    @PostMapping("/edit")
    public String editPassword(@RequestParam String password,
                               @RequestParam String repeatPassword,
                               Principal principal) throws Exception {
        if (!password.equals(repeatPassword)) {
            return "redirect:/edit";
        }
        String username = principal.getName();
        if (userService.editPassword(password, username)) {
            User user = userService.findByLogin(username);
            walletService.editUserMainPasswordInWallet(user.getId(), password);
        } else {
            return "redirect:/edit";
        }
        return "redirect:/";
    }
}

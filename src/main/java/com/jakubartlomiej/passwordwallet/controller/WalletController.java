package com.jakubartlomiej.passwordwallet.controller;

import com.jakubartlomiej.passwordwallet.model.User;
import com.jakubartlomiej.passwordwallet.service.UserService;
import com.jakubartlomiej.passwordwallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.HashMap;

@Controller
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;
    private final UserService userService;

    @GetMapping("/")
    public String getWallet(Model model, Principal principal, @ModelAttribute("decrypt") HashMap<Long, String> decrypt) {
        String login = principal.getName();
        User user = userService.findByLogin(login);
        model.addAttribute("username", login);
        model.addAttribute("wallet", walletService.findByUserId(user.getId()));
        model.addAttribute("decrypt", decrypt);
        return "wallet/home";
    }

    @GetMapping("/wallet/add")
    public String addWebAddressToWalletPage() {
        return "wallet/add";
    }

    @PostMapping("/wallet/add")
    public String addWebAddressToWallet(@RequestParam String webAddress, @RequestParam String description,
                                        @RequestParam String passwordToWebAddress, @RequestParam String loginToWebAddress,
                                        Principal principal) throws Exception {
        String username = principal.getName();
        User user = userService.findByLogin(username);
        walletService.save(webAddress, description, passwordToWebAddress, loginToWebAddress, user);
        return "redirect:/";
    }

    @GetMapping("/wallet/decrypt/{id}")
    public String getDecryptPassword(@PathVariable long id, RedirectAttributes redirectAttrs) throws Exception {
        HashMap<Long, String> decrypt = new HashMap<>();
        decrypt.put(id, walletService.decryptPasswordById(id));
        redirectAttrs.addFlashAttribute("decrypt", decrypt);
        return "redirect:/";
    }
}
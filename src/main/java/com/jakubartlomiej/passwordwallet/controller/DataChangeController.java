package com.jakubartlomiej.passwordwallet.controller;

import com.jakubartlomiej.passwordwallet.model.DataChange;
import com.jakubartlomiej.passwordwallet.model.User;
import com.jakubartlomiej.passwordwallet.model.Wallet;
import com.jakubartlomiej.passwordwallet.service.DataChangeService;
import com.jakubartlomiej.passwordwallet.service.UserService;
import com.jakubartlomiej.passwordwallet.service.WalletService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.util.List;

@Controller
@AllArgsConstructor
public class DataChangeController {
    private final DataChangeService dataChangeService;
    private final UserService userService;
    private final WalletService walletService;

    @GetMapping("/data-change")
    public String getDataChange(Principal principal, Model model) {
        String login = principal.getName();
        User user = userService.findByLogin(login);
        List<DataChange> dataChanges = dataChangeService.findByUser(user);
        model.addAttribute("dataChanges", dataChanges);
        model.addAttribute("username", login);
        return "data-change/home";
    }

    @GetMapping("/data-retrieve-deleted/{id}")
    public String getRetrieveDeletedPassword(@PathVariable long id){
        DataChange dataChange = dataChangeService.findById(id);
        Wallet wallet = walletService.findById(dataChange.getPasswordIdModified());
        walletService.retrievePassword(wallet,dataChange);
        return "redirect:/";
    }
}

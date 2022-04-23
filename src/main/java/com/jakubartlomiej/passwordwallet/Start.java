package com.jakubartlomiej.passwordwallet;

import com.jakubartlomiej.passwordwallet.model.Action;
import com.jakubartlomiej.passwordwallet.model.Function;
import com.jakubartlomiej.passwordwallet.model.enums.ActionType;
import com.jakubartlomiej.passwordwallet.model.enums.FunctionName;
import com.jakubartlomiej.passwordwallet.service.ActionService;
import com.jakubartlomiej.passwordwallet.service.FunctionService;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class Start {
  private final FunctionService functionService;
  private final ActionService actionService;

    @EventListener(ApplicationReadyEvent.class)
    public void run() {
        if (functionService.count() == 0){
            Function viewPassword = new Function();
            Function editPassword = new Function();
            Function createPassword = new Function();
            Function deletePassword = new Function();
            Function login = new Function();

            viewPassword.setName(FunctionName.VIEW_PASSWORD);
            viewPassword.setDescription("User decrypt password.");


            createPassword.setName(FunctionName.CREATE_PASSWORD);
            createPassword.setDescription("User create password.");


            editPassword.setName(FunctionName.EDIT_PASSWORD);
            editPassword.setDescription("User edit password.");

            deletePassword.setName(FunctionName.DELETE_PASSWORD);
            deletePassword.setDescription("User delete password.");

            login.setName(FunctionName.LOGIN);
            login.setDescription("User login.");

            functionService.save(viewPassword);
            functionService.save(editPassword);
            functionService.save(createPassword);
            functionService.save(deletePassword);
            functionService.save(login);
        }

        if (actionService.count() == 0){
            Action create = new Action();
            Action update = new Action();
            Action delete = new Action();
            Action retrieve = new Action();

            create.setName(ActionType.CREATE);
            create.setDescription("Create password.");

            update.setName(ActionType.UPDATE);
            update.setDescription("Update password.");

            delete.setName(ActionType.DELETE);
            delete.setDescription("Delete password.");

            retrieve.setName(ActionType.RETRIEVE);
            retrieve.setDescription("Retrieve password.");

            actionService.save(create);
            actionService.save(update);
            actionService.save(delete);
            actionService.save(retrieve);
        }
    }
}


///VIEW_PASSWORD, CREATE_PASSWORD, EDIT_PASSWORD, DELETE_PASSWORD, LOGIN
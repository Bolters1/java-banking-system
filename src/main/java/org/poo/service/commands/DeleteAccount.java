package org.poo.service.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.main.Singleton;
import org.poo.service.users.Users;

import java.util.ArrayList;

public class DeleteAccount implements Command{
    String account;
    ArrayList<Users> users;
    int timestamp;
    String email;

    public DeleteAccount(String account, ArrayList<Users> users, int timestamp, String email) {
        this.account = account;
        this.users = users;
        this.timestamp = timestamp;
        this.email = email;
    }

    @Override
    public void execute() {
        int ok = 0;
        Singleton singleton = Singleton.getInstance();
        ObjectNode object = singleton.getMapper().createObjectNode();
        for(int i = 0; i < users.size(); i++){
            for(int j = 0; j < users.get(i).getAccounts().size(); j++){
                if(users.get(i).getAccounts().get(j) == null)
                    continue;
                if (account.equals(users.get(i).getAccounts().get(j).getIBAN()) && email.equals(users.get(i).getEmail())){
                    if(users.get(i).getAccounts().get(j).getBalance() > 0){
                        ObjectNode errorNode = singleton.getMapper().createObjectNode();
                        errorNode.put("error", "Account couldn't be deleted - see org.poo.transactions for details");
                        errorNode.put("timestamp", timestamp);

                        object.put("command", "deleteAccount");
                        object.set("output", errorNode);
                        object.put("timestamp", timestamp);
                        singleton.getOutput().add(object);
                        ObjectNode transaction1 = users.get(i).getTransactionManager().getMapper().createObjectNode();
                        transaction1.put("timestamp", timestamp);
                        transaction1.put("description", "Account couldn't be deleted - there are funds remaining");
                        users.get(i).getTransactionManager().addTransaction(transaction1);
                        return;
                    }
                    users.get(i).getAccounts().remove(j);
                    ok = 1;
                }
            }
        }

        if (ok == 1) {
            ObjectNode outputNode = singleton.getMapper().createObjectNode();
            outputNode.put("success", "Account deleted");
            outputNode.put("timestamp", timestamp);

            object.put("command", "deleteAccount");
            object.set("output", outputNode);
            object.put("timestamp", timestamp);
            singleton.getOutput().add(object);
        } else {
            object.put("command", "deleteAccount");
            object.put("output", "Account not found");
            object.put("timestamp", timestamp);
            singleton.getOutput().add(object);
        }
    }
}

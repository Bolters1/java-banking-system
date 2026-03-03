package org.poo.service.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.With;
import org.poo.fileio.*;
import org.poo.main.Singleton;
import org.poo.service.cashback.Cashback;
import org.poo.service.commands.*;
import org.poo.service.commerciants.Commerciants;
import org.poo.service.exchange.CurrencyGraph;
import org.poo.service.split.SplitManager;
import org.poo.service.users.Users;
import org.poo.utils.Utils;

import java.util.ArrayList;

public class Initiliaze {
    ArrayList<Commerciants> commerciants = new ArrayList<>();
    public void start(ObjectInput input, ObjectMapper mapper, ObjectWriter writer, ArrayNode output){
        Singleton singleton = Singleton.getInstance();
        singleton.setMapper(mapper);
        singleton.setWriter(writer);
        singleton.setOutput(output);
        CurrencyGraph graph = new CurrencyGraph();
        ArrayList<Users> users = new ArrayList<Users>();
        for (UserInput user : input.getUsers()) {
            Users new_user = new Users(user.getFirstName(), user.getLastName(), user.getEmail(), user.getBirthDate(), user.getOccupation());
            users.add(new_user);
        }
        for (ExchangeInput exchangeRate : input.getExchangeRates()) {
            graph.addEdge(exchangeRate.getFrom(), exchangeRate.getTo(), exchangeRate.getRate());
        }
        for (CommerciantInput commerciant : input.getCommerciants()) {
            Commerciants new_commerciant = new Commerciants(commerciant.getCommerciant(), commerciant.getId(),commerciant.getAccount(), commerciant.getType(), commerciant.getCashbackStrategy());
            commerciants.add(new_commerciant);
        }

        Utils.resetRandom();
        ArrayList<SplitManager> managers = new ArrayList<>();
        for (CommandInput command : input.getCommands()) {
            switch (command.getCommand()) {
                case "printUsers":
                    PrintUsers print = new PrintUsers(users, command.getTimestamp());
                    print.execute();
                    break;
                case "addAccount":
                    AddAccount add = new AddAccount(users, command.getEmail(), command.getCurrency(), command.getAccountType(), command.getTimestamp(), command.getInterestRate(), graph);
                    add.execute();
                    break;
                case "createCard":
                    CreateCard create = new CreateCard(users, command.getAccount(), command.getEmail(), command.getTimestamp());
                     create.execute();
                     break;
                case "addFunds":
                    AddFunds addFunds = new AddFunds(users, command.getAccount(), command.getAmount(), command.getEmail(), command.getTimestamp());
                    addFunds.execute();
                    break;
                case "deleteAccount":
                    DeleteAccount delete = new DeleteAccount(command.getAccount(), users, command.getTimestamp(), command.getEmail());
                    delete.execute();
                    break;
                case "createOneTimeCard":
                    CreateOneTimeCard createOneTimeCard= new CreateOneTimeCard(users, command.getAccount(), command.getEmail(), command.getTimestamp());
                    createOneTimeCard.execute();
                    break;
                case "deleteCard":
                    DeleteCard deleteCard = new DeleteCard(users, command.getEmail(), command.getCardNumber(), command.getTimestamp());
                    deleteCard.execute();
                    break;
                case "payOnline":
                    PayOnline pay = new PayOnline(command.getCardNumber(), users, command.getAmount(), command.getCurrency(), command.getEmail(), graph, command.getTimestamp(), command.getCommerciant(), commerciants);
                    pay.execute();
                    break;
                case "sendMoney":
                    SendMoney send = new SendMoney(command.getAccount(), command.getReceiver(), command.getAmount(), command.getDescription(), command.getEmail(), users, graph, command.getTimestamp(), commerciants);
                    send.execute();
                    break;
                case "printTransactions":
                    PrintTransactions printTransactions = new PrintTransactions(command.getTimestamp(), users, command.getEmail());
                    printTransactions.execute();
                    break;
//                case "setAlias":
//                    SetAlias alias = new SetAlias(command.getEmail(), command.getAccount(), command.getAlias(), users);
//                    alias.execute();
//                    break;
                case "setMinimumBalance":
                    SetMinimumBalance setMinimumBalance = new SetMinimumBalance(users, command.getAccount(), command.getAmount());
                    setMinimumBalance.execute();
                    break;
                case "checkCardStatus":
                    CheckCardStatus checkCardStatus = new CheckCardStatus(users, command.getCardNumber(), command.getTimestamp());
                    checkCardStatus.execute();
                    break;
                case "changeInterestRate":
                    ChangeInterestRate changeInterestRate = new ChangeInterestRate(command.getAccount(), command.getInterestRate(), users, command.getTimestamp());
                    changeInterestRate.execute();
                    break;
//                case "splitPayment":
//                    SplitPayment splitPayment = new SplitPayment(command.getAccounts(), command.getAmount(),command.getCurrency(), command.getTimestamp(), users, graph);
//                    splitPayment.execute();
//                    break;
                case "report":
                    Report report = new Report(command.getStartTimestamp(), command.getEndTimestamp(), command.getTimestamp(), command.getAccount(), users);
                    report.execute();
                    break;
                case "spendingsReport":
                    SpendingsReport spendingsReport = new SpendingsReport(command.getStartTimestamp(), command.getEndTimestamp(), command.getTimestamp(), command.getAccount(), users);
                    spendingsReport.execute();
                    break;
                case "addInterest":
                    AddInterest addInterest = new AddInterest(command.getAccount(), users, command.getTimestamp());
                    addInterest.execute();
                    break;
                case "withdrawSavings":
                    WithdrawSavings withdrawSavings = new WithdrawSavings(users, command.getAccount(), command.getAmount(), command.getCurrency(), command.getTimestamp());
                    withdrawSavings.execute();
                    break;
                case "upgradePlan":
                    UpgradePlan upgradePlan = new UpgradePlan(command.getAccount(), command.getNewPlanType(), command.getTimestamp(), users, graph);
                    upgradePlan.execute();
                    break;
                case "cashWithdrawal":
                    CashWithdrawal cashWithdrawal = new CashWithdrawal(command.getCardNumber(), command.getAmount(), command.getEmail(), command.getLocation(), command.getTimestamp(), users, graph);
                    cashWithdrawal.execute();
                    break;
                case "splitPayment":
                    SplitManager manager = new SplitManager(command.getAccounts(), command.getAmountForUsers(), command.getSplitPaymentType(), command.getCurrency(), command.getTimestamp(), users, graph, command.getAmount());
                    managers.add(manager);
                    break;
                case "acceptSplitPayment":
                    if(managers.size() > 0)
                        managers.get(0).addAccept(managers, command.getEmail(), users, command.getSplitPaymentType(), command.getTimestamp());
                    break;
                case "addNewBusinessAssociate":
                    AddNewBusinessAssociate addNewBusinessAssociate = new AddNewBusinessAssociate(command.getAccount(), command.getRole(), command.getEmail(), command.getTimestamp(), users);
                    addNewBusinessAssociate.execute();
                    break;
                case "changeSpendingLimit":
                    ChangeSpendingLimit changeSpendingLimit = new ChangeSpendingLimit(command.getEmail(), command.getAccount(), command.getAmount(), command.getTimestamp(), users);
                    changeSpendingLimit.execute();
                    break;
                case "changeDepositLimit":
                    ChangeDepositLimit changeDepositLimit = new ChangeDepositLimit(command.getEmail(), command.getAccount(), command.getAmount(), command.getTimestamp(), users);
                    changeDepositLimit.execute();
                    break;
                case "businessReport":
                    BusinessReport businessReport = new BusinessReport(command.getStartTimestamp(), command.getEndTimestamp(), command.getType(), command.getAccount(), users, command.getTimestamp());
                    businessReport.execute();
                    break;
                case "rejectSplitPayment":
                    managers.get(0).reject(managers, command.getEmail(), users, command.getTimestamp());
                    break;



            }
        }


    }

}

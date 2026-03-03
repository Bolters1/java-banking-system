package org.poo.service.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.poo.service.TransactionManager;
import org.poo.service.users.accounts.Account;
import org.poo.service.users.accounts.ClassicAccount;
import org.poo.service.users.accounts.SavingsAccount;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Users {
    private String firstName;
    private String lastName;
    private String email;
    @JsonIgnore
    private String birthDate;
    @JsonIgnore
    private String occupation;
    private ArrayList<Account> accounts;
    @JsonIgnore
    private TransactionManager transactionManager;
    @JsonIgnore
    private String plan;
    @JsonIgnore
    private double transactionsForGold;
    public Users(String firstName, String lastName, String email, String birthDate, String occupation){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.accounts = new ArrayList<>();
        this.transactionManager = new TransactionManager();
        this.accounts = new ArrayList<>();
        this.birthDate = birthDate;
        this.occupation = occupation;
        if(occupation.equals("student"))
            plan = "student";
        else
            plan = "standard";
    }

    public ArrayList<Account> getAccounts() {
        return accounts;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getOccupation() {
        return occupation;
    }
    @JsonIgnore
    public int getAge(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate birthDateParsed = LocalDate.parse(birthDate, formatter);

        // Get the current date
        LocalDate currentDate = LocalDate.now();

        // Calculate the period between birthDate and currentDate
        return Period.between(birthDateParsed, currentDate).getYears();
    }
    @JsonIgnore
    public double getCommision(double amount){
        if(plan.equals("standard"))
            return 0.002;
        else if(plan.equals("student"))
            return 0;
        else if(plan.equals("silver") && amount>=500)
            return 0.001;
        return 0;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public double getTransactionsForGold() {
        return transactionsForGold;
    }

    public void setTransactionsForGold(double transactionsForGold) {
        this.transactionsForGold = transactionsForGold;
    }
    public void checkForGold(){
        transactionsForGold++;
        if(transactionsForGold == 5)
            this.plan = "gold";
    }
    public String getName(String email, ArrayList<Users> users){
        for (Users user : users) {
            if(user.getEmail().equals(email)){
                return user.getLastName() + " " + user.getFirstName();
            }
        }
        return null;
    }
}

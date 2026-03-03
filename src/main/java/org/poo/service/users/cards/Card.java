package org.poo.service.users.cards;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.poo.utils.Utils;

public class Card{
    @JsonIgnore
    private String account;
    @JsonIgnore
    private String email;
    @JsonIgnore
    private String cardType;
    private String cardNumber;
    private String status;
    public Card(){
        String number = Utils.generateCardNumber();
        this.cardNumber = number;
        this.status = "active";
        this.cardType = "classic";
    }

    public String getEmail() {
        return email;
    }

    public String getAccount() {
        return account;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCardType() {
        return cardType;
    }
}

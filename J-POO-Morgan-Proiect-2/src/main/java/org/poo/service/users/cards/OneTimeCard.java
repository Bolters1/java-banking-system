package org.poo.service.users.cards;

import org.poo.utils.Utils;

public class OneTimeCard extends Card{
    public OneTimeCard(){
        String number = Utils.generateCardNumber();
        this.setCardNumber(number);
        this.setStatus("active");
        this.setCardType("oneTime");
    }
}

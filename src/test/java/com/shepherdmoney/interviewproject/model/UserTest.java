package com.shepherdmoney.interviewproject.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserTest {
    private User user;
    private CreditCard creditCard1;
    private CreditCard creditCard2;

    @BeforeEach
    void setUp() {
        user = new User();
        creditCard1 = new CreditCard();
        creditCard2 = new CreditCard();
    }

    @Test
    public void testAddCreditCard() {
        assertTrue(user.getCreditCards().isEmpty(), 
            "Initially, credit card list should be empty");

        user.addCreditCard(creditCard1);
        assertEquals(1, user.getCreditCards().size(), 
            "Credit card list should have one card after addition");
        assertTrue(user.getCreditCards().contains(creditCard1), 
            "Credit card list should contain the added card");
        assertEquals(user, creditCard1.getOwner(), 
            "Owner of the credit card should be set to the user");

        user.addCreditCard(creditCard2);
        assertEquals(2, user.getCreditCards().size(), 
            "Credit card list should have two cards after adding another");
        assertTrue(user.getCreditCards().contains(creditCard2), 
            "Credit card list should contain the second added card");
    }

    @Test
    public void testRemoveCreditCard() {
        user.addCreditCard(creditCard1);
        user.addCreditCard(creditCard2);

        user.removeCreditCard(creditCard1);
        assertEquals(1, user.getCreditCards().size(), 
            "Credit card list should have one card after removal");
        assertFalse(user.getCreditCards().contains(creditCard1), 
            "Credit card list should not contain the removed card");
        assertNull(creditCard1.getOwner(), 
            "Owner of the removed credit card should be set to null");

        user.removeCreditCard(creditCard2);
        assertTrue(user.getCreditCards().isEmpty(), 
            "Credit card list should be empty after removing all cards");
        assertNull(creditCard2.getOwner(), 
            "Owner of the second removed credit card should be set to null");
    }
}

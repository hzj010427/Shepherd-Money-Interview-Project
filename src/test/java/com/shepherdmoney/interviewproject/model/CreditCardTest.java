package com.shepherdmoney.interviewproject.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

public class CreditCardTest {
    private CreditCard creditCard;

    @BeforeEach
    void setUp() {
        creditCard = new CreditCard();
    }

    @Test
    public void testInsertBalance() {
        assertEquals(creditCard.getBalance(LocalDate.now()), 0.0, 
            "Balance should be null if no records exist for the date");

        LocalDate today = LocalDate.now();
        double balanceToday = 100.0;
        creditCard.insertBalance(today, balanceToday);
        
        assertEquals(balanceToday, creditCard.getBalance(today), 
            "Balance for today should match the inserted value");
    }

    @Test
    public void testGetBalanceExactMatch() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        creditCard.insertBalance(yesterday, 50.0);
        creditCard.insertBalance(today, 100.0);

        assertEquals(100.0, creditCard.getBalance(today), 
            "Should return exact balance for today");
        assertEquals(50.0, creditCard.getBalance(yesterday), 
            "Should return exact balance for yesterday");
    }

    @Test
    public void testGetBalanceMostRecentBeforeDate() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate tomorrow = today.plusDays(1);

        creditCard.insertBalance(yesterday, 50.0);
        creditCard.insertBalance(tomorrow, 150.0);

        assertEquals(50.0, creditCard.getBalance(today), 
            "Should return yesterday's balance if no exact match for today");
        assertEquals(creditCard.getBalance(yesterday.minusDays(1)), 0.0, 
            "Should return 0.0 if no records exist before the given date");
    }

    @Test
    public void testUpdateBalanceExistingDate() {
        LocalDate today = LocalDate.now();
        creditCard.insertBalance(today, 100.0);
        creditCard.updateBalance(today, 200.0);

        assertEquals(200.0, creditCard.getBalance(today), 
            "Should update the balance for the existing date");
    }

    @Test
    public void testUpdateBalanceNewDate() {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        assertEquals(creditCard.getBalance(tomorrow), 0.0);

        creditCard.updateBalance(tomorrow, 300.0);
        assertEquals(300.0, creditCard.getBalance(tomorrow), 
            "Should insert a new balance if no existing record for the date");
    }

    @Test
    public void testBalanceHistoryOrder() {
        creditCard.insertBalance(LocalDate.parse("2023-04-10"), 800.0);
        creditCard.insertBalance(LocalDate.parse("2023-04-11"), 1000.0);
        creditCard.insertBalance(LocalDate.parse("2023-04-12"), 1200.0);
        creditCard.insertBalance(LocalDate.parse("2023-04-13"), 1100.0);
        creditCard.insertBalance(LocalDate.parse("2023-04-16"), 900.0);

        // Ensure that the balance history is sorted in reverse chronological order
        LocalDate previousDate = creditCard.getBalanceHistory().get(0).getDate();
        for (int i = 1; i < creditCard.getBalanceHistory().size(); i++) {
            LocalDate currentDate = creditCard.getBalanceHistory().get(i).getDate();
            assertTrue(previousDate.isAfter(currentDate), 
            "Dates should be in reverse chronological order: " + previousDate + " should be after " + currentDate);
            previousDate = currentDate;
        }
    }

    @Test
    public void testBalanceHistoryFilling() {
        LocalDate start = LocalDate.parse("2023-04-10");
        LocalDate end = LocalDate.parse("2023-04-16");
        creditCard.insertBalance(start, 800.0);
        creditCard.insertBalance(LocalDate.parse("2023-04-11"), 1000.0);
        creditCard.insertBalance(LocalDate.parse("2023-04-12"), 1200.0);
        creditCard.insertBalance(LocalDate.parse("2023-04-13"), 1100.0);
        
        // Filling the gaps up to today's date
        LocalDate currentDate = start.plusDays(1);
        while (!currentDate.isAfter(end)) {
            if (creditCard.getBalance(currentDate) == 0.0) {
                double previousBalance = creditCard.getBalance(currentDate.minusDays(1));
                creditCard.insertBalance(currentDate, previousBalance);
            }
            currentDate = currentDate.plusDays(1);
        }

        // Assert that every date from start to today has a balance record
        currentDate = start;
        while (!currentDate.isAfter(end)) {
            assertNotNull(creditCard.getBalance(currentDate), 
                "Balance should be available for " + currentDate);
            currentDate = currentDate.plusDays(1);
        }
    }
}

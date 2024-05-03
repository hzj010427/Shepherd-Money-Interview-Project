package com.shepherdmoney.interviewproject.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.*;

/**
 * Entity representing a credit card in the system.
 * This class includes details such as the issuance bank and card number, and is associated with a User owner.
 * It handles operations related to the balance history of the card, allowing updates and queries on balances for specific dates.
 *
 * @author Zijie Huang
 * @since 05/02/2024
 */
@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class CreditCard {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String issuanceBank;

    private String number;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;

    @OneToMany(mappedBy = "creditCard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BalanceHistory> balanceHistory = new ArrayList<>();

    /**
     * Inserts a new balance record for a specific date.
     *
     * @param date    The date of the balance record.
     * @param balance The balance amount to record.
     */
    public void insertBalance(LocalDate date, double balance) {
        BalanceHistory history = new BalanceHistory();
        history.setDate(date);
        history.setBalance(balance);
        history.setCreditCard(this);
        balanceHistory.add(history);
        sortBalanceHistory();
    }

    /**
     * Retrieves the balance for a given date. If no exact date match is found, 
     * it returns the most recent balance before the date.
     *
     * @param date The date to retrieve the balance for.
     * @return The balance amount or 0.0 if no records exist before the given date.
     */
    public double getBalance(LocalDate date) {
        for (BalanceHistory history : balanceHistory) {
            if (history.getDate().isEqual(date)) {
                return history.getBalance();
            }
        }

        for (BalanceHistory history : balanceHistory) {
            if (!history.getDate().isAfter(date)) {
                return history.getBalance();
            }
        }

        return 0.0;
    }

    /**
     * Updates the balance for a specific date or inserts a new record 
     * if no existing record is found for that date.
     *
     * @param date    The date for which the balance needs to be updated.
     * @param balance The new balance amount.
     */
    public void updateBalance(LocalDate date, double balance) {
        boolean found = false;
        for (BalanceHistory history : balanceHistory) {
            if (history.getDate().isEqual(date)) {
                history.setBalance(balance);
                history.setCreditCard(this);
                found = true;
                break;
            }
        }

        // If record for the specified date doesn't exist, insert a new record
        if (!found) {
            this.insertBalance(date, balance);
        }
    }

    /**
     * Retrieves the balance history for the credit card.
     * 
     * @return A string representation of the balance history.
     */
    public String getBalanceHistoryString() {
        StringBuilder sb = new StringBuilder();
        for (BalanceHistory history : balanceHistory) {
            sb.append(history.getDate()).append(": ").append(history.getBalance()).append("\n");
        }
        return sb.toString();
    }

    /**
     * Sorts the balance history in reverse chronological order.
     */
    private void sortBalanceHistory() {
        balanceHistory.sort(Comparator.comparing(BalanceHistory::getDate).reversed());
    }

    @Override 
    public String toString() {
        return "CreditCard{" +
                "id=" + id +
                ", issuanceBank='" + issuanceBank + '\'' +
                ", number='" + number + '\'' +
                ", owner=" + owner +
                ", balanceHistory=" + balanceHistory +
                '}';
    }
}

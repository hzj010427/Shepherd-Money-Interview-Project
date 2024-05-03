package com.shepherdmoney.interviewproject.controller;

import com.shepherdmoney.interviewproject.model.CreditCard;
import com.shepherdmoney.interviewproject.model.User;
import com.shepherdmoney.interviewproject.repository.CreditCardRepository;
import com.shepherdmoney.interviewproject.vo.request.AddCreditCardToUserPayload;
import com.shepherdmoney.interviewproject.vo.request.UpdateBalancePayload;
import com.shepherdmoney.interviewproject.vo.response.CreditCardView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller class for managing credit card-related operations.
 * Provides endpoints for adding credit cards to users, retrieving all cards for a user,
 * getting the user ID associated with a specific credit card, and updating credit card balances.
 * 
 * @author Zijie Huang
 * @since 05/02/2024
 */
@RestController
public class CreditCardController {

    private final CreditCardRepository creditCardRepository;

    public CreditCardController(CreditCardRepository creditCardRepository) {
        this.creditCardRepository = creditCardRepository;
    }

    /**
     * Constructs a CreditCardController with a CreditCardRepository.
     *
     * @param creditCardRepository The repository used for credit card data operations.
     */
    @PostMapping("/credit-card")
    public ResponseEntity<Integer> addCreditCardToUser(@RequestBody AddCreditCardToUserPayload payload) {
        CreditCard creditCard = new CreditCard();
        creditCard.setIssuanceBank(payload.getCardIssuanceBank());
        creditCard.setNumber(payload.getCardNumber());

        User user = new User();
        user.setId(payload.getUserId());
        creditCard.setOwner(user);

        CreditCard savedCreditCard = creditCardRepository.save(creditCard);
        return ResponseEntity.ok(savedCreditCard.getId());
    }

    /**
     * Adds a credit card to a user based on the provided payload.
     * 
     * @param payload The payload containing the user ID and credit card details.
     * @return A ResponseEntity containing the ID of the newly added credit card.
     */
    @GetMapping("/credit-card:all")
    public ResponseEntity<List<CreditCardView>> getAllCardOfUser(@RequestParam int userId) {
        User user = new User();
        user.setId(userId);
        List<CreditCard> userCreditCards = creditCardRepository.findByOwner(user);
        List<CreditCardView> cardViews = userCreditCards.stream()
                .map(card -> new CreditCardView(card.getIssuanceBank(), card.getNumber()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(cardViews);
    }

    /**
     * Retrieves all credit cards associated with a specific user ID.
     *
     * @param userId The user ID to search for credit cards.
     * @return A ResponseEntity containing a list of CreditCardView objects representing each card.
     */
    @GetMapping("/credit-card:user-id")
    public ResponseEntity<Integer> getUserIdForCreditCard(@RequestParam String creditCardNumber) {
        CreditCard creditCard = creditCardRepository.findByNumber(creditCardNumber);
        if (creditCard != null) {
            return ResponseEntity.ok(creditCard.getOwner().getId());
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Updates the balance of one or more credit cards based on the provided payloads.
     * 
     * @param payloads An array of UpdateBalancePayload objects containing the credit card number, 
     *                 balance date, and balance amount.
     * @return A ResponseEntity with a success message if the update is successful, 
     *         or an error message if the card number is not found.
     */
    @PostMapping("/credit-card:update-balance")
    public ResponseEntity<String> updateCreditCardBalance(@RequestBody UpdateBalancePayload[] payloads) {
        try {
            for (UpdateBalancePayload payload : payloads) {
                CreditCard creditCard = creditCardRepository.findByNumber(payload.getCreditCardNumber());
                if (creditCard == null) {
                    return ResponseEntity.badRequest().body("Credit card with number " + payload.getCreditCardNumber() + " does not exist.");
                }

                double currentBalance = creditCard.getBalance(payload.getBalanceDate());
                double difference = payload.getBalanceAmount() - currentBalance;

                // Update balance history
                creditCard.updateBalance(payload.getBalanceDate(), payload.getBalanceAmount());

                if (currentBalance > 0) {
                    LocalDate today = LocalDate.now();
                    LocalDate date = payload.getBalanceDate().plusDays(1);
                    while (!date.isAfter(today)) {
                        double updatedBalance = creditCard.getBalance(date) + difference;
                        creditCard.updateBalance(date, updatedBalance);
                        date = date.plusDays(1);
                    }
                }

                creditCardRepository.save(creditCard);
            }
            return ResponseEntity.ok("Credit card balances updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("An error occurred while updating credit card balances: " + e.getMessage());
        }
    }

    /**
     * Retrieves the balance history for a specific credit card.
     * 
     * @param cardNumber The credit card number to retrieve the balance history for.
     * @return A ResponseEntity containing the balance history as a string, 
     *         or an error message if the card number is not found.
     */
    @GetMapping("/credit-card:balance-history")
    public ResponseEntity<String> getBalanceHistory(@RequestParam String cardNumber) {
        CreditCard creditCard = creditCardRepository.findByNumber(cardNumber);
        if (creditCard == null) {
            return ResponseEntity.badRequest().body("Credit card with number " + cardNumber + " does not exist.");
        }
        return ResponseEntity.ok(creditCard.getBalanceHistoryString());
    }
}

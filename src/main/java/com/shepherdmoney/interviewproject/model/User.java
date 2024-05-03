package com.shepherdmoney.interviewproject.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a user in the system. 
 * This class is mapped to the "MyUser" table in the database and includes basic user details such as name and email.
 * Users can own multiple credit cards, which are managed through a one-to-many relationship.
 *
 * @author Zijie Huang
 * @since 05/02/2024
 */
@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "MyUser")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String name;

    private String email;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    private List<CreditCard> creditCards = new ArrayList<>();

    /**
     * Adds a credit card to this user's list of cards.
     * Sets the owner of the credit card to this user.
     *
     * @param creditCard The credit card to be added.
     */
    public void addCreditCard(CreditCard creditCard) {
        creditCards.add(creditCard);
        creditCard.setOwner(this);
    }

    /**
     * Removes a credit card from this user's list of cards.
     * Sets the owner of the credit card to null.
     *
     * @param creditCard The credit card to be removed.
     */
    public void removeCreditCard(CreditCard creditCard) {
        creditCards.remove(creditCard);
        creditCard.setOwner(null);
    }
}

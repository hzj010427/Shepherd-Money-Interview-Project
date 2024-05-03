package com.shepherdmoney.interviewproject.repository;

import com.shepherdmoney.interviewproject.model.CreditCard;
import com.shepherdmoney.interviewproject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Crud repository to store credit cards
 * 
 * @author Zijie Huang
 * @since 05/02/
 */
@Repository("CreditCardRepo")
public interface CreditCardRepository extends JpaRepository<CreditCard, Integer> {

    /**
     * Retrieves a list of CreditCard entities owned by a specified user.
     * 
     * @param owner the User whose credit cards are to be retrieved
     * @return a list of CreditCard entities
     */
    List<CreditCard> findByOwner(User owner);

    /**
     * Retrieves a CreditCard entity based on its credit card number.
     * 
     * @param creditCardNumber the credit card number to search for
     * @return the CreditCard entity if found, or null if not found
     */
    CreditCard findByNumber(String creditCardNumber);
}

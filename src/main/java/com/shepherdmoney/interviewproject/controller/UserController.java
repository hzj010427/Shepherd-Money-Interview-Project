package com.shepherdmoney.interviewproject.controller;

import com.shepherdmoney.interviewproject.model.User;
import com.shepherdmoney.interviewproject.repository.UserRepository;
import com.shepherdmoney.interviewproject.vo.request.CreateUserPayload;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controller class responsible for handling web requests related to user operations.
 * Provides endpoints for creating and deleting users within the system.
 * 
 * @author Zijie Huang
 * @since 05/02/2024
 */
@RestController
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Creates a new user in the system based on the provided payload.
     * 
     * @param payload The data used to create a new user, including name and email.
     * @return A ResponseEntity containing the ID of the newly created user or an error message.
     */
    @PutMapping("/user")
    public ResponseEntity<Integer> createUser(@RequestBody CreateUserPayload payload) {
        User user = new User();
        user.setName(payload.getName());
        user.setEmail(payload.getEmail());
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser.getId());
    }

    /**
     * Deletes an existing user from the system based on the user ID provided.
     *
     * @param userId The ID of the user to delete.
     * @return A ResponseEntity with a success message if the user is found and deleted, or an error message if not found.
     */
    @DeleteMapping("/user")
    public ResponseEntity<String> deleteUser(@RequestParam int userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            userRepository.deleteById(userId);
            return ResponseEntity.ok("User with ID " + userId + " deleted successfully.");
        } else {
            return ResponseEntity.badRequest().body("User with ID " + userId + " does not exist.");
        }
    }
}

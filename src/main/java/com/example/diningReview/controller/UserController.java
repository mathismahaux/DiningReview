package com.example.diningReview.controller;

import com.example.diningReview.model.User;
import com.example.diningReview.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        if (userRepository.existsByDisplayName(user.getDisplayName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Display name already exists.");
        }
        User savedUser = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @PutMapping("/{displayName}")
    public ResponseEntity<?> updateUser(@PathVariable String displayName, @RequestBody User updatedUser) {
        Optional<User> existingUser = userRepository.findByDisplayName(displayName);
        if (existingUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        User user = existingUser.get();
        user.setCity(updatedUser.getCity());
        user.setState(updatedUser.getState());
        user.setZipcode(updatedUser.getZipcode());
        user.setInterestedInPeanutAllergy(updatedUser.getInterestedInPeanutAllergy());
        user.setInterestedInEggAllergy(updatedUser.getInterestedInEggAllergy());
        user.setInterestedInDairyAllergy(updatedUser.getInterestedInDairyAllergy());

        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    @GetMapping("/{displayName}")
    public ResponseEntity<?> getUser(@PathVariable String displayName) {
        Optional<User> user = userRepository.findByDisplayName(displayName);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        return ResponseEntity.ok(user.get());
    }
}
package com.example.wallet.service;

import com.example.wallet.Exceptions.UserNotFoundException;
import com.example.wallet.model.User;
import com.example.wallet.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(String username, String password) {
        User user = new User(username, password);
        return userRepository.save(user);
    }

    @Transactional
    public Double deposit(String username, String password, Double amount) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found"));
        user.validateCredentials(password);
        return user.deposit(amount);
    }

    @Transactional
    public Double withdraw(String username, String password, Double amount) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found"));
        user.validateCredentials(password);
        return user.withdraw(amount);
    }
}

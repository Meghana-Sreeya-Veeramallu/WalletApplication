package com.example.wallet.service;

import com.example.wallet.Exceptions.UserNotFoundException;
import com.example.wallet.model.User;
import com.example.wallet.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WalletService {
    private final UserRepository userRepository;

    @Autowired
    public WalletService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public Double deposit(String username, Double amount) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found"));
        return user.deposit(amount);
    }

    @Transactional
    public Double withdraw(String username, Double amount) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found"));
        return user.withdraw(amount);
    }
}

package com.example.wallet.service;

import com.example.wallet.Exceptions.UserNotFoundException;
import com.example.wallet.model.User;
import com.example.wallet.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class WalletService {
    private final UserRepository userRepository;

    @Autowired
    public WalletService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public BigDecimal deposit(String username, BigDecimal amount) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found"));
        return user.deposit(amount);
    }

    public BigDecimal withdraw(String username, BigDecimal amount) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found"));
        return user.withdraw(amount);
    }
}

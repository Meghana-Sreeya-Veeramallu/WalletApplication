package com.example.wallet.model;

import com.example.wallet.Exceptions.PasswordCannotBeNullOrEmptyException;
import com.example.wallet.Exceptions.UsernameCannotBeNullOrEmptyException;
import jakarta.persistence.*;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Wallet wallet;

    public User(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new UsernameCannotBeNullOrEmptyException("Username cannot be null or empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new PasswordCannotBeNullOrEmptyException("Password cannot be null or empty");
        }
        this.username = username;
        this.password = password;
        this.wallet = new Wallet();
    }

    public Wallet getWallet() {
        return wallet;
    }
}

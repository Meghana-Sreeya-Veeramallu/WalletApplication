package com.example.wallet.model;

import com.example.wallet.Enums.CurrencyType;
import com.example.wallet.Exceptions.CurrencyCannotBeNullException;
import com.example.wallet.Exceptions.PasswordCannotBeNullOrEmptyException;
import com.example.wallet.Exceptions.UsernameCannotBeNullOrEmptyException;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String username;
    private String password;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Wallet wallet;

    public User() {}

    public User(String username, String password) {
        validate(username, password);
        this.username = username;
        this.password = password;
        this.wallet = new Wallet();
        this.wallet.setUser(this);
    }

    public User(String username, String password, CurrencyType currency) {
        validate(username, password);
        if (currency == null) {
            throw new CurrencyCannotBeNullException("Currency cannot be null");
        }
        this.username = username;
        this.password = password;
        this.wallet = new Wallet(currency);
        this.wallet.setUser(this);
    }

    private void validate(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new UsernameCannotBeNullOrEmptyException("Username cannot be null or empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new PasswordCannotBeNullOrEmptyException("Password cannot be null or empty");
        }
    }
}

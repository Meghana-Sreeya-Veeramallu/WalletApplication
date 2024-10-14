package com.example.wallet.model;

import com.example.wallet.Exceptions.CredentialsDoNotMatchException;
import com.example.wallet.Exceptions.PasswordCannotBeNullOrEmptyException;
import com.example.wallet.Exceptions.UsernameCannotBeNullOrEmptyException;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String username;
    private String password;

    @OneToOne(cascade = CascadeType.ALL)
    private Wallet wallet;

    public User() {}

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

    public Double deposit(Double amount) {
        return wallet.deposit(amount);
    }

    public Double withdraw(Double amount) {
        return wallet.withdraw(amount);
    }

    public void validateCredentials(String password) {
        if (!this.password.equals(password)){
            throw new CredentialsDoNotMatchException("Credentials do not match");
        }
    }
}

package com.hitanshudhawan.sankshipt.services;

import com.hitanshudhawan.sankshipt.exceptions.TokenNotFoundException;
import com.hitanshudhawan.sankshipt.exceptions.UserAlreadyExistsException;
import com.hitanshudhawan.sankshipt.exceptions.UserNotFoundException;
import com.hitanshudhawan.sankshipt.models.Token;
import com.hitanshudhawan.sankshipt.models.User;
import com.hitanshudhawan.sankshipt.repositories.TokenRepository;
import com.hitanshudhawan.sankshipt.repositories.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            TokenRepository tokenRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User signUp(String firstName, String lastName, String email, String password) throws UserAlreadyExistsException {

        User existingUser = userRepository.findByEmail(email).orElse(null);
        if (existingUser != null) {
            throw new UserAlreadyExistsException(email, "User with email already exists");
        }

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        return userRepository.save(user);
    }

    public Token signIn(String email, String password) throws UserNotFoundException {

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            throw new UserNotFoundException(email, "User with email not found");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return null;
        }

        Token token = generateToken(user);

        return tokenRepository.save(token);
    }

    public void signOut(String tokenValue) throws TokenNotFoundException {

        Token token = tokenRepository.findByValueAndIsExpired(tokenValue, false).orElse(null);

        if (token == null) {
            throw new TokenNotFoundException(tokenValue, "Token not found");
        }

        token.setExpired(true);
        tokenRepository.save(token);
    }

    public User validateToken(String tokenValue) throws TokenNotFoundException {

        Token token = tokenRepository.findByValueAndIsExpiredAndExpiryDateAfter(tokenValue, false, Calendar.getInstance().getTime()).orElse(null);

        if (token == null) {
            throw new TokenNotFoundException(tokenValue, "Token not found");
        }

        return token.getUser();
    }


    private Token generateToken(User user) {
        Token token = new Token();

        // Generate a random token value of 128 characters
        token.setValue(RandomStringUtils.randomAlphanumeric(128));

        token.setUser(user);

        // Set expiry date to 30 days from now
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 30);
        token.setExpiryDate(calendar.getTime());

        token.setExpired(false);

        return token;
    }

}

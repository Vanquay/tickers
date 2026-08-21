package com.agent.tickers.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.agent.tickers.models.User;
import com.agent.tickers.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User addTicker(Long userId, String ticker) {
        User user = findUser(userId);
        user.addTicker(ticker);
        return userRepository.save(user);
    }

    public User addTickers(Long userId, List<String> tickers) {
        User user = findUser(userId);
        tickers.forEach(user::addTicker);
        return userRepository.save(user);
    }

    public List<String> getWatchlist(Long userId) {
        return findUser(userId).getWatchlist();
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User " + userId + " not found"));
    }
}

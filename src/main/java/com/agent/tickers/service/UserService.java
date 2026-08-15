package com.agent.tickers.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.agent.tickers.models.Stock;
import com.agent.tickers.models.User;
import com.agent.tickers.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Mono<User> createUser(User user) {
        return Mono.fromCallable(() -> persistNewUser(user))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<User> addTicker(Long userId, Stock stock) {
        return addTickers(userId, List.of(stock));
    }

    public Mono<User> addTickers(Long userId, List<Stock> stocks) {
        return Mono.fromCallable(() -> persistTickers(userId, stocks))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private User persistNewUser(User user) {
        List<Stock> incoming = List.copyOf(user.getWatchlist());
        user.getWatchlist().clear();
        incoming.forEach(user::addStock);
        return userRepository.save(user);
    }

    private User persistTickers(Long userId, List<Stock> stocks) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User " + userId + " not found"));
        stocks.forEach(user::addStock);
        return userRepository.save(user);
    }
}

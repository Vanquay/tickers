package com.agent.tickers.controllers;
import io.swagger.v3.oas.annotations.Operation;
import java.net.URI;
import java.util.HashSet;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.agent.tickers.models.StockPrice;
import com.agent.tickers.models.User;
import com.agent.tickers.service.StockPriceService;
import com.agent.tickers.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;


@RestController
@Slf4j
@RequiredArgsConstructor
public class StocksController {

    private final UserService userService;
    private final StockPriceService stockPriceService;

    // Blocking create
    @PostMapping("/users/create")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User created = userService.createUser(user);
        return ResponseEntity.created(URI.create("/users/" + created.getId())).body(created);
    }

    @GetMapping("/users/{userId}/watchlist")
    public List<String> getWatchlist(@PathVariable Long userId) {
        return userService.getWatchlist(userId);
    }

    // Blocking add of a single ticker symbol (JSON string body, e.g. "AAPL")
    @PostMapping("/users/{userId}/tickers")
    public User addTicker(@PathVariable Long userId, @RequestBody String ticker) {
        return userService.addTicker(userId, ticker);
    }

    // Blocking add of multiple ticker symbols
    @PostMapping("/users/{userId}/tickers/batch")
    public User addTickers(@PathVariable Long userId, @RequestBody List<String> tickers) {
        return userService.addTickers(userId, tickers);
    }

    // Kept as a Kafka Streams study reference: live prices for the user's watchlist symbols.
    @GetMapping(value = "/users/{userId}/stocks/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<StockPrice> streamSavedStockPrices(@PathVariable Long userId) {
        return Mono.fromCallable(() -> new HashSet<>(userService.getWatchlist(userId)))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(stockPriceService::streamForSymbols);
    }
}

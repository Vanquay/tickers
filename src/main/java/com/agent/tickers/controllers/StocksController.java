package com.agent.tickers.controllers;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agent.tickers.models.Stock;
import com.agent.tickers.models.StockPrice;
import com.agent.tickers.models.User;
import com.agent.tickers.service.StockPriceService;
import com.agent.tickers.service.StockService;
import com.agent.tickers.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class StocksController {

    private final StockService stockService;
    private final StockPriceService stockPriceService;
    private final UserService userService;

    @PostMapping("/users/create")
    public Mono<ResponseEntity<User>> createUser(@RequestBody User user) {

        return userService.createUser(user)
                .map(created -> ResponseEntity
                        .created(URI.create("/api/users/" + created.getId()))
                        .body(created));
    }

    @Cacheable("savedStocks")
    @GetMapping("/users/{userId}/stocks")
    public Flux<Stock> getSavedStocks(@PathVariable Long userId) {
        return stockService.savedStocks(userId);
    }

    @PostMapping("/users/{userId}/stocks")
    public Mono<User> addTicker(@PathVariable Long userId, @RequestBody Stock stock) {
        return userService.addTicker(userId, stock);
    }

    @PostMapping("/users/{userId}/tickers")
    public Mono<User> addTickers(@PathVariable Long userId, @RequestBody List<Stock> stocks) {
        return userService.addTickers(userId, stocks);
    }

    @GetMapping(value = "/users/{userId}/stocks/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<StockPrice> streamSavedStockPrices(@PathVariable Long userId) {
        return stockService.savedStocks(userId)
                .map(Stock::getSymbol)
                .collect(Collectors.toSet())
                .flatMapMany(this::stream);
    }

    private Flux<StockPrice> stream(Set<String> symbols) {
        return stockPriceService.streamForSymbols(symbols);
    }
}

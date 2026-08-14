package com.agent.tickers.controllers;

import java.util.Set;
import java.util.stream.Collectors;

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
import com.agent.tickers.service.StockPriceService;
import com.agent.tickers.service.StockService;

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

    @GetMapping("/users/{userId}/stocks")
    public Flux<Stock> getSavedStocks(@PathVariable String userId) {
        return stockService.savedStocks(userId);
    }

    @PostMapping("/users/{userId}/stocks")
    public Mono<Stock> saveStock(@PathVariable String userId, @RequestBody Stock stock) {
        stock.setUserId(userId);
        return stockService.save(stock);
    }

    @GetMapping(value = "/users/{userId}/stocks/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<StockPrice> streamSavedStockPrices(@PathVariable String userId) {
        return stockService.savedStocks(userId)
                .map(Stock::getSymbol)
                .collect(Collectors.toSet())
                .flatMapMany(this::stream);
    }

    @PostMapping("/users/create")
    public ResponseEntity<String> postMethodName(@RequestBody String entity) {
        //TODO: process POST request
        
        return ResponseEntity.<String>ok(entity);
    }
    

    private Flux<StockPrice> stream(Set<String> symbols) {
        return stockPriceService.streamForSymbols(symbols);
    }
}

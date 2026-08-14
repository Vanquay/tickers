package com.agent.tickers.service;

import org.springframework.stereotype.Service;

import com.agent.tickers.models.Stock;
import com.agent.tickers.repository.StockRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    /** Loads a user's saved stocks, offloading the blocking JPA call. */
    public Flux<Stock> savedStocks(String userId) {
        return Mono.fromCallable(() -> stockRepository.findByUserId(userId))
                .flatMapMany(Flux::fromIterable)
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Stock> save(Stock stock) {
        return Mono.fromCallable(() -> stockRepository.save(stock))
                .subscribeOn(Schedulers.boundedElastic());
    }
}

package com.agent.tickers.service;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.agent.tickers.models.StockPrice;
import com.agent.tickers.provider.StockPriceStream;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class StockPriceService {

    private final StockPriceStream stockPriceStream;

    /** Live price ticks limited to the given symbols. */
    public Flux<StockPrice> streamForSymbols(Set<String> symbols) {
        return stockPriceStream.prices()
                .filter(price -> symbols.contains(price.symbol()));
    }
}

package com.agent.tickers.provider;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.agent.tickers.models.StockPrice;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * Bridges the Kafka Streams price topology to a reactive stream consumed by WebFlux.
 */
@Component
@Slf4j
public class StockPriceStream {

    public static final String PRICES_TOPIC = "stock-prices";

    private final ObjectMapper mapper;
    private final Sinks.Many<StockPrice> sink = Sinks.many().multicast().onBackpressureBuffer();

    public StockPriceStream(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Autowired
    public void buildPipeline(StreamsBuilder builder) {
        KStream<String, String> stream = builder.stream(
                PRICES_TOPIC, Consumed.with(Serdes.String(), Serdes.String()));

        stream.mapValues(this::parse)
                .filter((symbol, price) -> price != null)
                .foreach((symbol, price) -> sink.tryEmitNext(price));
    }

    /** Live feed of every price tick; filtered per-user downstream. */
    public Flux<StockPrice> prices() {
        return sink.asFlux();
    }

    private StockPrice parse(String json) {
        try {
            return mapper.readValue(json, StockPrice.class);
        } catch (Exception e) {
            log.warn("Skipping malformed price message: {}", json, e);
            return null;
        }
    }
}

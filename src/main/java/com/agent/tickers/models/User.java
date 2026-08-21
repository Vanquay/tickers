package com.agent.tickers.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
    private String role;

    // Watchlist holds ticker symbols only; live prices are fetched by the UI from an external service.
    // @ElementCollection(fetch = FetchType.EAGER)
    // @CollectionTable(name = "user_watchlist", joinColumns = @JoinColumn(name = "user_id"))
    // @Column(name = "ticker")
    private List<String> watchlist = new ArrayList<>();

    public void addTicker(String ticker) {
        this.watchlist.add(ticker);
    }
}

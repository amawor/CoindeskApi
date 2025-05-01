package com.example.coindesk.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class Currency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String currencyCode;
    private String currencyName;
    private Double exchangeRate;

    public Currency() {}

    public Currency(String currencyCode, String currencyName, Double exchangeRate) {
        this.currencyCode = currencyCode;
        this.currencyName = currencyName;
        this.exchangeRate = exchangeRate;
    }
}

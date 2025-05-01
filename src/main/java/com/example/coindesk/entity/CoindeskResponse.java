package com.example.coindesk.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class CoindeskResponse {

    private Time time;
    private String disclaimer;
    private String chartName;
    private Map<String, CurrencyInfo> bpi;


    @Getter
    @Setter
    public static class Time {
        private String updated;
        private String updatedISO;
        private String updateduk;
    }

    @Getter
    @Setter
    public static class CurrencyInfo {
        private String code;
        private String rate;
        private String description;
        private String symbol;
        private double rate_float;
    }
}
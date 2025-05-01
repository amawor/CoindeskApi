package com.example.coindesk.dto;

import com.example.coindesk.entity.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyUpdateResponse {
    private String updated;
    private Currency currency;
}

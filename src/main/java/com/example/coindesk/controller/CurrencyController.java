package com.example.coindesk.controller;

import com.example.coindesk.dto.CurrencyUpdateResponse;
import com.example.coindesk.entity.Currency;
import com.example.coindesk.entity.CoindeskResponse;
import com.example.coindesk.repository.CurrencyRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

@RestController
@RequestMapping("/api/currencies")
public class CurrencyController {

    @Autowired
    private CurrencyRepository currencyRepository;

    private final String COINDESK_API_URL = "https://kengp3.github.io/blog/coindesk.json";

    private RestTemplate restTemplate = new RestTemplate();



    // 呼叫coindesk-api更新所有幣別匯率
    @PutMapping("/update-rate")
    public ResponseEntity<?> updateAllCurrencyRates() {
        CoindeskResponse response = restTemplate.getForObject(COINDESK_API_URL, CoindeskResponse.class);

        if (response != null && response.getBpi() != null) {
            response.getBpi().forEach((code, detail) -> {
                Currency currency = currencyRepository.findByCurrencyCode(code);
                if (currency != null) {
                    double rate = Double.parseDouble(detail.getRate().replace(",", ""));
                    currency.setExchangeRate(rate);
                    currencyRepository.save(currency);
                }
            });
            return ResponseEntity.ok("匯率已全部更新成功");
        } else {
            return ResponseEntity.status(502).body("無法取得 Coindesk API 資料");
        }
    }

    // 1. 幣別資料表 CRUD 等維護功能的 API。

    // 查詢所有幣別資料
    @GetMapping
    public List<Currency> getAllCurrencies() {
        return currencyRepository.findAll();
    }

    // 查詢單一幣別
    @GetMapping("/{id}")
    public ResponseEntity<Currency> getCurrencyById(@PathVariable Long id) {
        return currencyRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 新增幣別
    @PostMapping
    public ResponseEntity<Currency> createCurrency(@RequestBody Currency currency) {
        return ResponseEntity.ok(currencyRepository.save(currency));
    }

    // 更新幣別名稱
    @PutMapping("/{currencyCode}")
    public ResponseEntity<Currency> updateCurrencyName(@PathVariable String currencyCode, @RequestBody String name) {
        Currency currency = currencyRepository.findByCurrencyCode(currencyCode);
        if (currency != null) {
            if (name != null) {
                currency.setCurrencyName(name);
                return ResponseEntity.ok(currencyRepository.save(currency));
            } else {
                return ResponseEntity.badRequest().build(); // 沒有提供名稱
            }
        } else {
            return ResponseEntity.notFound().build(); // 查無此幣別
        }
    }


    //刪除幣別
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCurrency(@PathVariable Long id) {
        if (currencyRepository.existsById(id)) {
            currencyRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 2. 呼叫 coindesk 的 API。 (轉發 Coindesk 的原始 JSON )
    @GetMapping("/coindesk")
    public ResponseEntity<CoindeskResponse> getCoindeskData() {
        CoindeskResponse response = restTemplate.getForObject(COINDESK_API_URL, CoindeskResponse.class);
        if (response != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(502).build(); // Bad Gateway
        }
    }

    // 3. 呼叫 coindesk 的 API，並進行資料轉換，組成新 API。
    //    此新 API 包含以下內容：
    //      A. 更新時間（時間格式範例：1990/01/01 00:00:00）。
    //      B. 幣別相關資訊（幣別，幣別中文名稱，以及匯率）。

    // 更新特定幣別資料
    @PutMapping("/update-rate/{currencyCode}")
    public ResponseEntity<CurrencyUpdateResponse> updateCurrencyRate(@PathVariable String currencyCode) {
        Currency currency = currencyRepository.findByCurrencyCode(currencyCode);
        if (currency != null) {
            // 呼叫 Coindesk API 取得匯率
            CoindeskResponse response = restTemplate.getForObject(COINDESK_API_URL, CoindeskResponse.class);

            if (response != null && response.getBpi().containsKey(currencyCode)) {
                String rate = response.getBpi().get(currencyCode).getRate();
                // 更新匯率
                currency.setExchangeRate(Double.parseDouble(rate.replace(",", "")));  // 去掉匯率中的逗號


                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm:ss z", Locale.ENGLISH);
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(response.getTime().getUpdated(), inputFormatter);
                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

                String lastUpdatedTime = outputFormatter.format(zonedDateTime);
                Currency updatedCurrency = currencyRepository.save(currency);
                // 儲存更新後的幣別資料以及coindeskapi的最後更新時間
                CurrencyUpdateResponse result = new CurrencyUpdateResponse(lastUpdatedTime, updatedCurrency);
                return ResponseEntity.ok(result);

            } else {
                return ResponseEntity.badRequest().body(null);
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
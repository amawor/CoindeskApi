//1. 針對資料轉換相關邏輯作單元測試。

package com.example.coindesk.controller;

import com.example.coindesk.dto.CurrencyUpdateResponse;
import com.example.coindesk.entity.CoindeskResponse;
import com.example.coindesk.entity.Currency;
import com.example.coindesk.repository.CurrencyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class CurrencyControllerTest {

    @Mock
    private CurrencyRepository currencyRepository;
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private CurrencyController currencyController;
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUpdateCurrency() {
        // Coindesk API 回應假資料
        CoindeskResponse coindeskResponse = new CoindeskResponse();
        CoindeskResponse.Time time = new CoindeskResponse.Time();
        time.setUpdated("Sep 2, 2024 07:07:20 UTC");
        coindeskResponse.setTime(time);

        Map<String, CoindeskResponse.CurrencyInfo> bpi = new HashMap<>();
        CoindeskResponse.CurrencyInfo usdInfo = new CoindeskResponse.CurrencyInfo();
        usdInfo.setCode("USD");
        usdInfo.setRate("57,756.298");
        usdInfo.setDescription("United States Dollar");
        bpi.put("USD", usdInfo);

        coindeskResponse.setBpi(bpi);

        // 模擬從 Coindesk API 獲得回應
        when(restTemplate.getForObject(anyString(), eq(CoindeskResponse.class))).thenReturn(coindeskResponse);

        // 模擬從資料庫找到的貨幣
        Currency existingCurrency = new Currency();
        existingCurrency.setCurrencyCode("USD");
        existingCurrency.setCurrencyName("美元");
        existingCurrency.setExchangeRate(1.0);
        when(currencyRepository.findByCurrencyCode("USD")).thenReturn(existingCurrency);

        // 模擬將資料寫入資料庫的回傳值
        when(currencyRepository.save(any(Currency.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 呼叫 Controller 的方法
        ResponseEntity<CurrencyUpdateResponse> response = currencyController.updateCurrencyRate("USD");

        // 驗證回應是否正確
        assertEquals(200, response.getStatusCodeValue());
        CurrencyUpdateResponse responseBody = response.getBody();
        assertNotNull(responseBody);
        System.out.println(responseBody);

        // 驗證時間格式是否正確
        assertEquals("2024/09/02 07:07:20", responseBody.getUpdated());

        // 驗證幣別資料是否正確
        assertNotNull(responseBody.getCurrency());
        assertEquals(57756.298, responseBody.getCurrency().getExchangeRate());  // 驗證匯率是否正確
        assertEquals("USD", responseBody.getCurrency().getCurrencyCode());  // 驗證幣別是否正確
        assertEquals("美元", responseBody.getCurrency().getCurrencyName());  // 驗證幣別名稱是否正確

        // 檢查是否有儲存到資料庫
        verify(currencyRepository, times(1)).save(any(Currency.class));
    }
}

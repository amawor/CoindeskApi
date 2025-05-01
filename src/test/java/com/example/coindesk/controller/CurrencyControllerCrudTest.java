
package com.example.coindesk.controller;

import com.example.coindesk.entity.Currency;
import com.example.coindesk.repository.CurrencyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CurrencyControllerCrudTest {

    @Mock
    private CurrencyRepository currencyRepository;

    @InjectMocks
    private CurrencyController currencyController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 1. 測試查詢所有幣別
    @Test
    public void testGetAllCurrencies() {
        List<Currency> mockList = Arrays.asList(
                new Currency("USD", "美元", 1.0),
                new Currency("TWD", "新台幣", 30.5)
        );
        when(currencyRepository.findAll()).thenReturn(mockList);

        List<Currency> result = currencyController.getAllCurrencies();
        assertEquals(2, result.size());
        assertEquals("USD", result.get(0).getCurrencyCode());
        assertEquals("TWD", result.get(1).getCurrencyCode());
    }

    // 2. 測試查詢單一幣別
    @Test
    public void testGetCurrencyById() {
        Currency mockCurrency = new Currency("USD", "美元", 1.0);
        when(currencyRepository.findById(1L)).thenReturn(Optional.of(mockCurrency));

        ResponseEntity<Currency> response = currencyController.getCurrencyById(1L);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals("USD", response.getBody().getCurrencyCode());
    }

    // 3. 測試新增幣別
    @Test
    public void testCreateCurrency() {
        Currency newCurrency = new Currency("JPY", "日圓", 1.0);
        Currency savedCurrency = new Currency("JPY", "日圓", 1.0);

        when(currencyRepository.save(newCurrency)).thenReturn(savedCurrency);

        ResponseEntity<Currency> response = currencyController.createCurrency(newCurrency);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("JPY", response.getBody().getCurrencyCode());
    }

    // 4. 測試更新幣別名稱
    @Test
    public void testUpdateCurrencyName() {
        Currency existingCurrency = new Currency("USD", "美元", 1.0);
        when(currencyRepository.findByCurrencyCode("USD")).thenReturn(existingCurrency);
        when(currencyRepository.save(any(Currency.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<Currency> response = currencyController.updateCurrencyName("USD", "美金");
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("美金", response.getBody().getCurrencyName());
    }

    // 5. 測試刪除幣別
    @Test
    public void testDeleteCurrency() {
        when(currencyRepository.existsById(1L)).thenReturn(true);

        ResponseEntity<Void> response = currencyController.deleteCurrency(1L);
        assertEquals(204, response.getStatusCodeValue());

        verify(currencyRepository, times(1)).deleteById(1L);
    }

    // 6. 測試刪除不存在的幣別
    @Test
    public void testDeleteCurrency_NotFound() {
        when(currencyRepository.existsById(999L)).thenReturn(false);

        ResponseEntity<Void> response = currencyController.deleteCurrency(999L);
        assertEquals(404, response.getStatusCodeValue());

        verify(currencyRepository, never()).deleteById(anyLong());
    }
}

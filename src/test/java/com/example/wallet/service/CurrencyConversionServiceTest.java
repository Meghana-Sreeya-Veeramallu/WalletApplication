package com.example.wallet.service;

import conversion.ConversionServiceGrpc;
import conversion.Conversion.ConvertResponse;
import conversion.Conversion.ConvertRequest;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CurrencyConversionServiceTest {

    @InjectMocks
    private CurrencyConversionService currencyConversionService;

    @Mock
    private ManagedChannel channel;

    @Mock
    private ConversionServiceGrpc.ConversionServiceBlockingStub blockingStub;

    @Mock
    private ConvertResponse convertResponse;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        setField(currencyConversionService, "channel", channel);
        setField(currencyConversionService, "blockingStub", blockingStub);
    }

    private void setField(Object obj, String fieldName, Object value) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }

    @Test
    void testConvertSuccessful() {
        String fromCurrency = "INR";
        String toCurrency = "USD";
        double amount = 100.0;
        double convertedAmount = 1.2;

        when(blockingStub.convert(any(ConvertRequest.class))).thenReturn(convertResponse);
        when(convertResponse.getConvertedAmount()).thenReturn(convertedAmount);

        Double result = currencyConversionService.convert(fromCurrency, toCurrency, amount);

        assertEquals(convertedAmount, result);
        verify(blockingStub, times(1)).convert(any(ConvertRequest.class));
    }

    @Test
    void testConvertThrowsException() {
        String fromCurrency = "INR";
        String toCurrency = "USD";
        double amount = 100.0;

        when(blockingStub.convert(any(ConvertRequest.class))).thenThrow(new StatusRuntimeException(io.grpc.Status.UNAVAILABLE));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            currencyConversionService.convert(fromCurrency, toCurrency, amount);
        });

        assertEquals("Error occurred while converting currency", exception.getMessage());
        verify(blockingStub, times(1)).convert(any(ConvertRequest.class));
    }
}

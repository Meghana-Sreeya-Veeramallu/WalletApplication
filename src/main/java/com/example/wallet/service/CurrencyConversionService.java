package com.example.wallet.service;

import com.example.wallet.Exceptions.ServerUnavailableException;
import conversion.ConversionServiceGrpc;
import conversion.Conversion.ConvertResponse;
import conversion.Conversion.ConvertRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CurrencyConversionService {
    private ManagedChannel channel;
    private ConversionServiceGrpc.ConversionServiceBlockingStub blockingStub;

    @Value("${grpc.server.host:localhost}")
    private String host;

    @Value("${grpc.server.port:50051}")
    private int port;

    @PostConstruct
    void init() {
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        blockingStub = ConversionServiceGrpc.newBlockingStub(channel);
        log.info("gRPC client initialized, connected to {}:{}", host, port);
    }

    public Double convert(String fromCurrency, String toCurrency, Double amount) {
        try{
            ConvertRequest request = ConvertRequest.newBuilder()
                    .setFromCurrency(fromCurrency)
                    .setToCurrency(toCurrency)
                    .setAmount(amount)
                    .build();

            ConvertResponse response = blockingStub.convert(request);
            return response.getConvertedAmount();
        } catch (Exception e) {
            if (e instanceof StatusRuntimeException && ((StatusRuntimeException) e).getStatus().getCode() == io.grpc.Status.UNAVAILABLE.getCode()) {
                log.error("Currency conversion failed: Server is unavailable. Amount: {} from: {} to: {}", amount, fromCurrency, toCurrency);
                throw new ServerUnavailableException("Currency conversion failed: Server is unavailable");
            }

            log.error("Currency conversion failed: {}. Amount: {} from: {} to: {}", e.getMessage(), amount, fromCurrency, toCurrency);
            throw new RuntimeException("Currency conversion failed: " + e.getMessage());
        }
    }

    @PreDestroy
    private void cleanup() {
        channel.shutdown();
        log.info("gRPC client shutdown");
    }
}

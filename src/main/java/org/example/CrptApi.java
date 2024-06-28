package org.example;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.Serial;
import java.io.Serializable;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Timer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CrptApi {

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .version(Version.HTTP_1_1)
            .authenticator(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("user", "pass".toCharArray());
                }
            })
            .build();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final AtomicInteger requestCount = new AtomicInteger();

    private final int requestLimit;

    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        this.requestLimit = requestLimit;
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                requestCount.set(0);
            }
        };
        timer.schedule(timerTask, 0, timeUnit.toMillis(1));
    }

    public void createDoc(@NonNull Document document, @NonNull String sign) {
        try {
            final HttpRequest request = buildRequest(document, sign);
            if (requestCount.getAndIncrement() >= requestLimit) {
                return;
            }
            HttpResponse<String> response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
            log.info("Http status: " + response.statusCode());
            log.info("Body: " + response.body());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    private static HttpRequest buildRequest(@NonNull Document document, @NonNull String sign) throws
                                                                                              JsonProcessingException {
        return HttpRequest.newBuilder()
                .uri(URI.create("https://ismp.crpt.ru/api/v3/lk/documents/create?sign=" + sign))
                .header("Content-Type", "application/json")
                .header("Authorization", "Basic dXNlcjpwYXNz")
                .POST(BodyPublishers.ofString(CrptApi.OBJECT_MAPPER.writeValueAsString(document)))
                .build();
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    static class Document implements Serializable {

        @Serial
        private static final long serialVersionUID = -3121380552529776638L;

        private Description description;
        @JsonProperty("doc_id")
        private String docId;
        @JsonProperty("doc_status")
        private String docStatus;
        @JsonProperty("doc_type")
        private String docType;
        private boolean importRequest;
        @JsonProperty("owner_inn")
        private String ownerInn;
        @JsonProperty("participant_inn")
        private String participantInn;
        @JsonProperty("producer_inn")
        private String producerInn;
        @JsonProperty("production_date")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private Date productionDate;
        @JsonProperty("production_type")
        private String productionType;
        private List<Product> products;
        @JsonProperty("reg_date")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private Date regDate;
        @JsonProperty("reg_number")
        private String regNumber;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    static class Description implements Serializable {

        @Serial
        private static final long serialVersionUID = 2175558456803717319L;

        private String participantInn;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    static class Product implements Serializable {

        @Serial
        private static final long serialVersionUID = 5759556914801314133L;

        @JsonProperty("certificate_document")
        private String certificateDocument;
        @JsonProperty("certificate_document_date")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private Date certificateDocumentDate;
        @JsonProperty("certificate_document_number")
        private String certificateDocumentNumber;
        @JsonProperty("owner_inn")
        private String ownerInn;
        @JsonProperty("producer_inn")
        private String producerInn;
        @JsonProperty("production_date")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private Date productionDate;
        @JsonProperty("tnved_code")
        private String tnvedCode;
        @JsonProperty("uit_code")
        private String uitCode;
        @JsonProperty("uitu_code")
        private String uituCode;
    }

}

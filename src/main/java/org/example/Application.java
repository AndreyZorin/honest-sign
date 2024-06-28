package org.example;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.example.CrptApi.Description;
import org.example.CrptApi.Document;
import org.example.CrptApi.Product;

/**
 * Hello world!
 */
public class Application {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    private static final Document DOCUMENT;

    static {
        try {
            DOCUMENT = Document.builder()
                    .description(Description.builder()
                            .participantInn("string")
                            .build())
                    .docId("string")
                    .docStatus("string")
                    .docType("LP_INTRODUCE_GOODS")
                    .importRequest(true)
                    .ownerInn("string")
                    .participantInn("string")
                    .producerInn("string")
                    .productionDate(DATE_FORMAT.parse("2020-01-23"))
                    .productionType("string")
                    .products(List.of(Product.builder()
                            .certificateDocument("string")
                            .certificateDocumentDate(DATE_FORMAT.parse("2020-01-23"))
                            .certificateDocumentNumber("string")
                            .ownerInn("string")
                            .producerInn("string")
                            .productionDate(DATE_FORMAT.parse("2020-01-23"))
                            .tnvedCode("string")
                            .uitCode("string")
                            .uituCode("string")
                            .build()))
                    .regDate(DATE_FORMAT.parse("2020-01-23"))
                    .regNumber("string")
                    .build();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        CrptApi api = new CrptApi(TimeUnit.SECONDS, 12);
        while (true) {
            executorService.execute(() -> api.createDoc(DOCUMENT, "sign"));
        }
    }

}

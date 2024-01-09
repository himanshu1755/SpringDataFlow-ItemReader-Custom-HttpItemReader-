package com.spring.data_flow_readers.http_reader.reader;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class HttpItemReader implements ItemReader<JsonObject> {

    private String apiUrl;
    private JsonObject cachedData; // Introduce a variable to cache the fetched data

    @Autowired
    private Gson gson;
    @Value("${http-source.endpoint}")
    private String endpoint;
    @Value("${http-source.request-params}")
    private String requestParams;
    @Value("${http-source.http-method}")
    private String httpMethod;
    @Value("${http-source.request-headers}")
    private String requestHeaders;
    @Value("${http-source.content-type}")
    private String contentType;
    @Value("${http-source.request-body}")
    private String requestBody;

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpItemReader.class);


    private String buildUrl(String endpoint, String requestParams) {
        // Logic to build URL with parameters
        return requestParams == null ? endpoint : endpoint + "?" + requestParams;
    }

    @Override
    public JsonObject read() throws Exception {
        if (cachedData == null) { // Check if data is already cached

            apiUrl = buildUrl(endpoint, requestParams);

            ResponseEntity<String> response;

            LOGGER.info("Parameters===================== ");
            LOGGER.info("httpMethod===================== {}", httpMethod);
            LOGGER.info("endpoint===================== {}", endpoint);
            LOGGER.info("apiUrl===================== {}", apiUrl);
            LOGGER.info("requestBody===================== {}", requestBody);

            RestTemplate restTemplate = new RestTemplate();

            if ("GET".equalsIgnoreCase(httpMethod)) {
                LOGGER.info("HttpItemReader GET ===================== ");
                LOGGER.info("apiUrl===================== {}", apiUrl);

                response = restTemplate.getForEntity(apiUrl, String.class);
                LOGGER.info("GET Response ===================== {}", response.getBody());
            } else if ("POST".equalsIgnoreCase(httpMethod)) {
                LOGGER.info("HttpItemReader POST ===================== ");
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
                ResponseEntity<String> postResponse = restTemplate.postForEntity(apiUrl, requestEntity, String.class);

                LOGGER.info("POST Response Status: {}", postResponse.getStatusCode());
                LOGGER.info("POST Response Body: {}", postResponse.getBody());

                response = postResponse;
            } else {
                throw new IllegalArgumentException("Invalid HTTP method specified");
            }

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new IllegalStateException("Failed to fetch data from API. Status: " + response.getStatusCode());
            }
            cachedData = gson.fromJson(response.getBody(), JsonObject.class);
            return cachedData;
        }
        return null;
    }

}
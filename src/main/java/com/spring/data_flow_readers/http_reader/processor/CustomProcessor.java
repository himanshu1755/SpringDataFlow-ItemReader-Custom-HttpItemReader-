package com.spring.data_flow_readers.http_reader.processor;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.spring.data_flow_readers.http_reader.models.RefreshTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
public class CustomProcessor implements ItemProcessor<JsonObject, ProcessedResponse<?>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomProcessor.class);
    @Autowired
    private Gson gson;

    @Override
    public ProcessedResponse<?>  process(JsonObject httpResponse) throws Exception {
        LOGGER.info("CustomProcessor_called");
        LOGGER.info("httpResponse.keySet() ======= {}",httpResponse.keySet());


        if (httpResponse.keySet().contains("refresh_token")) {
            RefreshTokenResponse refreshTokenResponse = gson.fromJson(httpResponse.toString(), RefreshTokenResponse.class);
            LOGGER.info("CustomProcessor refreshTokenResponse ==============  {}", refreshTokenResponse);
            return new ProcessedResponse<>(refreshTokenResponse);

        }
        LOGGER.info("CustomProcessor JsonObject ==============  {}", httpResponse);

        return new ProcessedResponse<>("null");
    }
}

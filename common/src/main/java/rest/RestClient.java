package rest;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public interface RestClient {

    JsonNode sendGetRequest(String url) throws IOException;
    JsonNode sendPostRequest(String url, String requestBody);
}

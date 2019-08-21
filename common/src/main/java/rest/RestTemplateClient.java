package rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

public class RestTemplateClient implements RestClient {

    private final ObjectMapper mapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public JsonNode sendGetRequest(String url) throws IOException {
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        mapper.readTree(response.getBody());
        return mapper.readTree(response.getBody());
    }

    @Override
    public JsonNode sendPostRequest(String url, String requestBody) {
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestBody, String.class);
        JsonNode result = null;
        try {
            result = mapper.readTree(response.getBody());
        } catch (IOException e) {}
        return result;
    }

}

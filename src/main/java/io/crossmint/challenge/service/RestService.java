package io.crossmint.challenge.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;


import java.util.*;

import static io.crossmint.challenge.util.GlobalVariables.*;

/**
 * RestService to create some 🪐POLYanets and validate them.
 * */
@Service
public class RestService {
    /**
     * ID of candidate to interact with API.
     * */
    @Value("${api.challenge.candidateId}")
    private String candidateId;

    @Autowired
    private final WebClient webClient;

    public RestService(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Method receives the shape to draw.
     * */
    public Mono<String> getGoalMap(String candidateId) {
        return this.webClient.get()
                .uri(GOAL_MAP_URL.replace("{candidateId}", candidateId))
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> Mono.error(new Exception("Error sending GET GOAL MAP")))
                .bodyToMono(String.class);
    }

    public void postPOLYanet(int row, int col) {
        sendHttpRequest(HttpMethod.POST, createUri(POLYANETS_URL, row, col, null, null));
    }

    public void deletePOLYanet(int row, int col) {
        sendHttpRequest(HttpMethod.DELETE, createUri(POLYANETS_URL, row, col, null, null));
    }

    public void postSoloon(int row, int col, String color) {
        sendHttpRequest(HttpMethod.POST, createUri(SOLOONS_URL, row, col, color, null));
    }

    public void deleteSoloon(int row, int col) {
        sendHttpRequest(HttpMethod.DELETE, createUri(SOLOONS_URL, row, col, null, null));
    }

    public void postCometh(int row, int col, String direction) {
        sendHttpRequest(HttpMethod.POST, createUri(COMETHS_URL, row, col, null, direction));
    }

    public void deleteCometh(int row, int col) {
        sendHttpRequest(HttpMethod.DELETE, createUri(COMETHS_URL, row, col, null, null));
    }
    private UriComponents createUri(String url, int row, int col, String color, String direction) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance()
                .path(url)
                .queryParam("row", row)
                .queryParam("column", col)
                .queryParam("candidateId", candidateId)
                .queryParamIfPresent("color", Optional.ofNullable(color))
                .queryParamIfPresent("direction", Optional.ofNullable(direction));
        return uriComponentsBuilder.build();
    }


    private void sendHttpRequest(HttpMethod method, UriComponents uri) {
        WebClient.RequestHeadersUriSpec<?> uriSpec;
        if (method == HttpMethod.POST) {
            uriSpec = webClient.post();
            ((WebClient.RequestBodyUriSpec)uriSpec).contentType(MediaType.APPLICATION_JSON);
        } else if (method == HttpMethod.DELETE) {
            uriSpec = webClient.delete();
        } else {
            throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }

        uriSpec.uri(uri.toUri())
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.createException().flatMap(Mono::error))
                .bodyToMono(Map.class)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}

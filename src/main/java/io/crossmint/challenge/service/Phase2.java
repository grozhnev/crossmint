package io.crossmint.challenge.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.crossmint.challenge.controller.Controller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

import static io.crossmint.challenge.util.GlobalVariables.*;

@Service
public class Phase2 implements ApplicationRunner {

    /**
     * ID of candidate to interact with API.
     * */
    @Value("${api.challenge.candidateId}")
    private String candidateId;

    private final Controller api;
    private final RestService phase2;

    public Phase2() {
        this.phase2 = new RestService(WebClient.builder().build());
        this.api = new Controller(phase2);
    }

    @Override
    public void run(ApplicationArguments args) {
        Mono<String> shapeToDraw = api.getShapeToDraw(candidateId);
        drawMetaverse(shapeToDraw);
    }

    private void drawMetaverse(Mono<String> shapeToDraw) {
        Type formatType = new TypeToken<Map<String, String[][]>>() {}.getType();
        shapeToDraw.map(json -> new Gson().<Map<String, String[][]>>fromJson(json, formatType))
                .map(map -> map.get("goal"))
                .filter(Objects::nonNull)
                .subscribe(arr -> {
                    for (int row = 0; row < arr.length; ++row) {
                        for (int col = 0; col < arr[0].length; ++col) {
                            String[] split = arr[row][col].split("_");
                            String astralObjectType = split[split.length-1];
                            String param = split.length > 1 ? split[0] : null;

                            switch (astralObjectType) {
                                case POLYANET_STR -> api.createPOLYanet(row, col);
                                case SOLOON_STR -> api.createSoloon(row, col, param);
                                case COMETH_STR -> api.createCometh(row, col, param);
                            }
                        }
                    }
                });
    }





}

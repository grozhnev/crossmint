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

import static io.crossmint.challenge.util.GlobalVariables.POLYANET_STR;

@Service
public class Phase1 implements ApplicationRunner {

    /**
     * ID of candidate to interact with API.
     * */
    @Value("${api.challenge.candidateId}")
    private String candidateId;

    private final Controller api;
    private final RestService phase1;

    public Phase1() {
        this.phase1 = new RestService(WebClient.builder().build());
        this.api = new Controller(phase1);
    }

    @Override
    public void run(ApplicationArguments args) {
            Mono<String> shapeToDraw = api.getShapeToDraw(candidateId);
            drawPOLYanetsShape(shapeToDraw);
        }

    private void drawPOLYanetsShape(Mono<String> shapeToDraw) {
        shapeToDraw.subscribe(jsonString -> {
            Type customType = new TypeToken<Map<String, String[][]>>(){}.getType();
            Map<String, String[][]> mapFromJson = new Gson().fromJson(jsonString, customType);

            String[][] arr = mapFromJson.get("goal");
            if (null != arr) {
                for (int row=0; row < arr.length; ++row) {
                    for (int col=0; col < arr[0].length; ++col) {
                        if (arr[row][col].equals(POLYANET_STR)) {
                            api.createPOLYanet(row, col);
                        }
                    }
                }
            }
        });
    }


}

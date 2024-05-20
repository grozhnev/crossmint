package io.crossmint.challenge.controller;

import io.crossmint.challenge.service.RestService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static io.crossmint.challenge.util.GlobalVariables.*;

@RestController
public class Controller {

    private final RestService service;

    public Controller(RestService service) {
        this.service = service;
    }

    /**
     * Sends request to get 2-Dimension array with shape to draw by calling API.
     * @return instance of String[][] with goal to draw using API.
     * */
    @GetMapping(GOAL_MAP_URL)
    public Mono<String> getShapeToDraw(@PathVariable String candidateId) {
        return service.getGoalMap(candidateId);
    }

    @PostMapping(POLYANETS_URL)
    public void createPOLYanet(@RequestParam int row, @RequestParam int col) {
        service.postPOLYanet(row, col);
    }

    @DeleteMapping(POLYANETS_URL)
    public void deletePOLYanet(@RequestParam int row, @RequestParam int col) {
       service.deletePOLYanet(row, col);
    }

    @PostMapping(SOLOONS_URL)
    public void createSoloon(@RequestParam int row, @RequestParam int col, @RequestParam String color) {
        service.postSoloon(row, col, color);
    }

    @DeleteMapping(SOLOONS_URL)
    public void deleteSoloon(@RequestParam int row, @RequestParam int col) {
        service.deleteSoloon(row, col);
    }

    @PostMapping(COMETHS_URL)
    public void createCometh(@RequestParam int row, @RequestParam int col, @RequestParam String direction) {
        service.postCometh(row, col, direction);
    }

    @DeleteMapping(COMETHS_URL)
    public void deleteCometh(@RequestParam int row, @RequestParam int col) {
        service.deleteCometh(row, col);
    }

}

package it.devchallenge.api.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.devchallenge.api.repository.DeputyRepository;

@RestController
public class DeputyController {
    @Autowired
    private DeputyRepository deputyRepository;

    @GetMapping("/deputies/closest")
    public List<Map<String, Object>> closestDeputies(@RequestParam(name = "count", required = false, defaultValue = "5")
                                                             int count) {
        return deputyRepository.findClosestDeputies(count);
    }

    @GetMapping("/deputies/closest/{name}")
    public List<Map<String, Object>> closestToOne(@PathVariable("name") String name,
                                                  @RequestParam(name = "count", required = false, defaultValue = "5")
                                                          int count) {
        return deputyRepository.findClosestToDeputy(name, count);
    }

    @GetMapping("/deputies/farthest/{name}")
    public List<Map<String, Object>> farthestToOne(@PathVariable("name") String name,
                                                   @RequestParam(name = "count", required = false, defaultValue = "5")
                                                           int count) {
        return deputyRepository.findFarthestToDeputy(name, count);
    }
}

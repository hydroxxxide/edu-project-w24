package kg.backend.tinder.controller;

import kg.backend.tinder.dto.FormDto;
import kg.backend.tinder.service.TinderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TinderController {
    private final TinderService tinderService;

    @PostMapping("/form")
    public void createForm(@RequestBody FormDto formDto){
        tinderService.saveForm(formDto);
    }

    @PutMapping("/form")
    public void updateForm(@RequestBody FormDto formDto){
        tinderService.saveForm(formDto);
    }

    @DeleteMapping("/form")
    public void deleteForm(@RequestParam long userId){

    }
}

package com.community.server.controller;

import com.community.server.service.BackpackService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/backpack")
public class BackpackController {

    private final BackpackService backpackService;

    public BackpackController(BackpackService backpackService) {
        this.backpackService = backpackService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getBackpack(HttpServletRequest httpServletRequest){
        return backpackService.getBackpack(httpServletRequest);
    }

    @PostMapping
    public ResponseEntity<?> getDividend(HttpServletRequest httpServletRequest){
        return backpackService.getDividend(httpServletRequest);
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(HttpServletRequest httpServletRequest){
        return backpackService.transfer(httpServletRequest);
    }

}

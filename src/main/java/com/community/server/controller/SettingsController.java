package com.community.server.controller;

import com.community.server.body.PasswordBody;
import com.community.server.service.SettingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    private final SettingsService settingsService;

    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @GetMapping
    public ResponseEntity<?> getSettings(HttpServletRequest httpServletRequest){
        return settingsService.getSettings(httpServletRequest);
    }

    @PostMapping("/photo")
    public ResponseEntity<?> updatePhoto(HttpServletRequest httpServletRequest, @RequestParam MultipartFile file){
        return settingsService.updatePhoto(httpServletRequest, file);
    }

    @PostMapping("/name/{name}")
    public ResponseEntity<?> updateName(HttpServletRequest httpServletRequest, @PathVariable String name){
        return settingsService.updateName(httpServletRequest, name);
    }

    @PostMapping("/password")
    public ResponseEntity<?> updatePassword(HttpServletRequest httpServletRequest, @Valid @RequestBody PasswordBody passwordBody){
        return settingsService.updatePassword(httpServletRequest, passwordBody);
    }
}

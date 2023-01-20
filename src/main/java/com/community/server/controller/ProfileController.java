package com.community.server.controller;

import com.community.server.body.BuyBody;
import com.community.server.dto.ProfileDto;
import com.community.server.service.ProfileService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ProfileDto getProfile(HttpServletRequest httpServletRequest){
        return profileService.getProfile(httpServletRequest);
    }
}

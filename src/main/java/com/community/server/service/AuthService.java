package com.community.server.service;

import com.community.server.body.SignInBody;
import com.community.server.body.SignUpBody;
import com.community.server.entity.RoleEntity;
import com.community.server.entity.RoleNameEntity;
import com.community.server.entity.UserEntity;
import com.community.server.exception.AppException;
import com.community.server.repository.RoleRepository;
import com.community.server.repository.UserRepository;
import com.community.server.security.JwtAuthenticationResponse;
import com.community.server.security.JwtTokenProvider;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collections;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository, JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @SneakyThrows
    public ResponseEntity<?> signup(SignUpBody signUpBody) {

        if(!signUpBody.getUsername().matches("^{6,40}[a-zA-Z0-9]+$"))
            return ResponseEntity.badRequest().body("Invalid username!");

        if(!signUpBody.getPassword().matches("^{6,40}[a-zA-Z0-9]+$"))
            return ResponseEntity.badRequest().body("Wrong password format!");

        if (userRepository.existsByUsername(signUpBody.getUsername()))
            return ResponseEntity.badRequest().body("Username is already taken!");

        if (userRepository.existsByEmail(signUpBody.getEmail()))
            return ResponseEntity.badRequest().body("Email Address already in use!");

        UserEntity userEntity = new UserEntity(
                signUpBody.getName(), signUpBody.getUsername(), signUpBody.getEmail(), passwordEncoder.encode(signUpBody.getPassword()));

        RoleEntity roleEntity = roleRepository.findByName(RoleNameEntity.ROLE_USER).orElseThrow(
                () -> new AppException("User Role not set."));

        userEntity.setRoles(Collections.singleton(roleEntity));

        File defaultPhoto = new File("resources/default.png");
        BufferedImage imagePhoto = ImageIO.read(defaultPhoto);
        File photo = new File("resources/" + signUpBody.getUsername() + ".png");
        if (photo.createNewFile()) {
            ImageIO.write(imagePhoto, "png", photo);
        }
        userRepository.save(userEntity);
        return ResponseEntity.ok("User registered successfully");
    }

    public ResponseEntity<?> signing(SignInBody signInBody) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signInBody.getUsername(), signInBody.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtTokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

}

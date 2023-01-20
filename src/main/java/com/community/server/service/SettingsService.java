package com.community.server.service;

import com.community.server.body.PasswordBody;
import com.community.server.dto.SettingsDto;
import com.community.server.entity.UserEntity;
import com.community.server.repository.UserRepository;
import com.community.server.security.JwtAuthenticationFilter;
import com.community.server.security.JwtTokenProvider;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

@Service
public class SettingsService {

    private final UserRepository userRepository;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public SettingsService(UserRepository userRepository, JwtAuthenticationFilter jwtAuthenticationFilter, JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<?> getSettings(HttpServletRequest httpServletRequest){

        String jwt = jwtAuthenticationFilter.getJwtFromRequest(httpServletRequest);
        Long userId = jwtTokenProvider.getUserIdFromJWT(jwt);

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        SettingsDto settingsDto = new SettingsDto();
        settingsDto.setUsername(userEntity.getUsername());
        settingsDto.setEmail(userEntity.getEmail());
        settingsDto.setName(userEntity.getName());
        if (userEntity.getLastDividend() != null)
            settingsDto.setLastDividend(userEntity.getLastDividend().getTime());
        return ResponseEntity.ok(settingsDto);
    }

    @SneakyThrows
    public ResponseEntity<?> updatePhoto(HttpServletRequest httpServletRequest, MultipartFile file){
        if(file.isEmpty())
            return ResponseEntity.badRequest().body("File is empty!");

        String suffix = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        if(!suffix.equalsIgnoreCase("png"))
            return ResponseEntity.badRequest().body("The file is not a photo! Need png format!");

        String jwt = jwtAuthenticationFilter.getJwtFromRequest(httpServletRequest);
        Long userId = jwtTokenProvider.getUserIdFromJWT(jwt);

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("User is not found!"));

        File directory = new File("resources");
        if(directory.mkdir()) System.out.println("The avatar directory has been created!");

        String fileName = userEntity.getUsername() + "." + suffix;
        String pathToFile = "resources/"+ fileName;
        File photo = new File(pathToFile);

        if(photo.createNewFile()) System.out.println("The avatar file has been created!");

        byte[] bytes = file.getBytes();
        BufferedOutputStream stream = new BufferedOutputStream(Files.newOutputStream(Paths.get(pathToFile)));
        stream.write(bytes);
        stream.close();

        return ResponseEntity.ok("User photo changed!");
    }

    public ResponseEntity<?> updateName(HttpServletRequest httpServletRequest, String name){
        if(name.length() < 6 || name.length() > 40)
            return ResponseEntity.badRequest().body("Bad name format!");
        String jwt = jwtAuthenticationFilter.getJwtFromRequest(httpServletRequest);
        Long userId = jwtTokenProvider.getUserIdFromJWT(jwt);

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("User is not found!"));

        userEntity.setName(name);
        userRepository.save(userEntity);
        return ResponseEntity.ok("User name changed!");
    }

    public ResponseEntity<?> updatePassword(HttpServletRequest httpServletRequest, PasswordBody passwordBody){

        if(!passwordBody.getOldPassword().matches("^{6,40}[a-zA-Z0-9]+$") || !passwordBody.getNewPassword().matches("^{6,40}[a-zA-Z0-9]+$")) {
            return ResponseEntity.badRequest().body("Bad passwords format!");
        }

        String jwt = jwtAuthenticationFilter.getJwtFromRequest(httpServletRequest);
        Long userId = jwtTokenProvider.getUserIdFromJWT(jwt);

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("User is not found!"));

        if(!passwordEncoder.matches(passwordBody.getOldPassword(), userEntity.getPassword())) {
            return ResponseEntity.badRequest().body("The current password is incorrect!");
        }

        userEntity.setPassword(passwordEncoder.encode(passwordBody.getNewPassword()));

        userRepository.save(userEntity);
        return ResponseEntity.ok("Your password changed!");
    }
}

package com.practice.trixter.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.practice.trixter.dto.RegisterFormDto;
import com.practice.trixter.dto.UserDto;
import com.practice.trixter.enums.Buckets;
import com.practice.trixter.exceptions.BadRegisterRequestException;
import com.practice.trixter.exceptions.UserNotFoundException;
import com.practice.trixter.model.Chat;
import com.practice.trixter.model.FilesInfo;
import com.practice.trixter.model.User;
import com.practice.trixter.repo.ChatRepo;
import com.practice.trixter.repo.UserRepo;
import com.practice.trixter.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepo userRepo;
    private final ChatRepo chatRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final BlobStorageService storageService;

    public UserDto register(RegisterFormDto form) {
        User user = userRepo.findByUsername(form.getUsername()).orElse(null);

        if (user != null) {
            throw new BadRegisterRequestException("Пользователь с никнеймом " + form.getUsername() + " уже существует");
        }

        Chat general = chatRepo.findByName("general").orElse(null);
        User newUser = User.builder()
                .username(form.getUsername())
                .password(form.getPassword())
                .chats(List.of(general))
                .build();
        newUser = save(newUser);
        general.addMember(newUser);
        chatRepo.save(general);

        return newUser.convertToDto();
    }

    public UserDto getUser(String authToken) {
        User user = getUserByToken(authToken);

        return user.convertToDto();
    }

    public User save(User user) {
        String password = passwordEncoder.encode(user.getPassword());
        user.setPassword(password);

        return userRepo.insert(user);
    }

    public User find(String id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не был найден"));
    }

    public User findByUsername(String username) {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с никнеймом " + username + " не был найден"));
    }

    public User update(User user) {
        return userRepo.save(user);
    }

    public UserDto update(UserDto userDto) {
        User user = find(userDto.getId());
        user.setStatus(userDto.getStatus());
        update(user);
        return  userDto;
    }

    public UserDto update(MultipartFile file, String authToken) {
        User user = getUserByToken(authToken);

        try {
            FilesInfo filesInfo = storageService.uploadFile(Buckets.AVATARS.getBucketName(), file);
            user.setAvatar(filesInfo);

            update(user);
            return user.convertToDto();
        } catch (Exception e) {
            log.info("DELETE - {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username);

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }

    public User getUserByToken(String authToken) {
        String token = authToken.substring("Bearer ".length());
        DecodedJWT decodedJWT = JWTUtil.verifyToken(token);
        String username = decodedJWT.getSubject();

        return findByUsername(username);
    }

}

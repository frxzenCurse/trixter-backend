package com.practice.trixter.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.practice.trixter.dto.ChatDto;
import com.practice.trixter.dto.LoginFormDto;
import com.practice.trixter.dto.UserDto;
import com.practice.trixter.exceptions.UserNotFoundException;
import com.practice.trixter.model.Chat;
import com.practice.trixter.model.FilesInfo;
import com.practice.trixter.model.User;
import com.practice.trixter.repo.ChatRepo;
import com.practice.trixter.repo.UserRepo;
import com.practice.trixter.util.FileManager;
import com.practice.trixter.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
    private final BCryptPasswordEncoder passwordEncoder;

    public UserDto getUserWithChats(String authToken) {
        User user = getUserByToken(authToken);

        return convertToDto(user);
    }

    public User save(User user) {
        String password = passwordEncoder.encode(user.getPassword());
        log.info("ENCODED PASSWORD - {}", password);
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
            if (user.getAvatar() != null) {
                FileManager.delete(user.getAvatar().getName());
            }

            String fileUrl = FileManager.upload(file, user.getUsername());
            FilesInfo filesInfo = FilesInfo.builder()
                    .name(user.getUsername() + FileManager.getExtension(file.getOriginalFilename()))
                    .size(file.getSize())
                    .url(fileUrl)
                    .build();
            user.setAvatar(filesInfo);

            update(user);
            return convertToDto(user);
        } catch (Exception e) {
            log.info("DELETE ?");
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

    public UserDto convertToDto(User user) {

        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .status(user.getStatus())
                .chatsId(user.getChatsIds())
                .build();

        if (user.getAvatar() != null) {
            userDto.setAvatarUrl(user.getAvatar().getName());
        }

        return userDto;
    }

    public User getUserByToken(String authToken) {
        String token = authToken.substring("Bearer ".length());
        DecodedJWT decodedJWT = JWTUtil.verifyToken(token);
        String username = decodedJWT.getSubject();

        return findByUsername(username);
    }

}

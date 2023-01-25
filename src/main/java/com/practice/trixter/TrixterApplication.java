package com.practice.trixter;

import com.practice.trixter.dto.UserDto;
import com.practice.trixter.model.Chat;
import com.practice.trixter.model.User;
import com.practice.trixter.repo.ChatRepo;
import com.practice.trixter.repo.UserRepo;
import com.practice.trixter.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class TrixterApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrixterApplication.class, args);
	}

//	@Bean
//	CommandLineRunner run(UserService userService, ChatRepo chatRepo) {
//		return args -> {
//			Chat chat = new Chat();
//			chat.setName("general");
//			chat = chatRepo.insert(chat);
//
//			User user1 = User.builder()
//					.username("user1")
//					.password("123")
//					.chatsIds(List.of(chat.getId()))
//					.build();
//			user1 = userService.save(user1);
//			User user2 = User.builder()
//					.username("user2")
//					.password("123")
//					.chatsIds(List.of(chat.getId()))
//					.build();
//			user2 = userService.save(user2);
//
//			List<String> users = List.of(user1.getId(), user2.getId());
//			chat.setMembersId(users);
//			chatRepo.save(chat);
//
//		};
//	}
}

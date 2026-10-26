package com.example.projectdnd.config;

import com.example.projectdnd.model.entity.User;
import com.example.projectdnd.model.enums.CharacterClass;
import com.example.projectdnd.model.enums.Magic;
import com.example.projectdnd.model.enums.Race;
import com.example.projectdnd.model.enums.Realms;
import com.example.projectdnd.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String testUsername = "Frodo";


            if (userRepository.findByUsername(testUsername).isEmpty()) {
                User testUser = User.builder()
                        .username(testUsername)
                        // Encrypt password using BCrypt
                        .password(passwordEncoder.encode("password123"))
                        .role("USER")
                        .characterName("Frodo the elf")
                        .race(Race.ELF)
                        .characterClass(CharacterClass.APPRENTICE)
                        .level(3)
                        .currentHp(25)
                        .maxHp(25)
                        .currentMana(50)
                        .maxMana(50)
                        .intelligence(16)
                        .strength(10)
                        .dexterity(14)
                        .knownMagic(Set.of(Magic.FIREBALL))
                        .knownRealms(Set.of(Realms.FIRE))
                        .build();

                userRepository.save(testUser);
                System.out.println(">>> Test character 'Frodo' created successfully with hashed " +
                        "password!");
            }
        };
    }
}
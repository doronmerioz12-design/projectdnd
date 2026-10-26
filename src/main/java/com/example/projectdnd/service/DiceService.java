package com.example.projectdnd.service;

import org.springframework.stereotype.Service;
import java.util.Random;

@Service
public class DiceService {

    private final Random random = new Random();


    public int rollD20() {
        return random.nextInt(20) + 1;
    }


    public int rollCustomDice(int sides) {
        return random.nextInt(sides) + 1;
    }
}
package it.devchallenge.sharepassword.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

@Service
public class ClockService {
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}

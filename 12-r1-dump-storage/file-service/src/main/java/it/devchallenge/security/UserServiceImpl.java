package it.devchallenge.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Override
    public String getCurrentUserName() {
        Object auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("Current principal: {}", auth);

        return "user";
    }
}

package it.devchallenge.sharepassword.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import it.devchallenge.sharepassword.model.FetchPasswordResponse;
import it.devchallenge.sharepassword.model.SharePasswordRequest;
import it.devchallenge.sharepassword.service.SharedPasswordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@AllArgsConstructor
@Slf4j
public class SharedPasswordController {

    private final SharedPasswordService sharedPasswordService;

    @PostMapping("/password")
    public String sharePassword(@RequestBody SharePasswordRequest requestBody, HttpServletRequest request) {
        log.info("Saving password");
        String linkBody = sharedPasswordService.sharePassword(requestBody);
        return request.getRequestURL() + "/" + linkBody;
    }

    @GetMapping("/password/{linkBody}")
    public FetchPasswordResponse fetchPassword(@PathVariable String linkBody) {
        log.info("Fetching password");
        return sharedPasswordService.fetchPassword(linkBody);
    }
}

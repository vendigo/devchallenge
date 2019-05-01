package com.github.vendigo.callcenter.call;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class CallController {
    @Autowired
    private CallService callService;

    @GetMapping("call")
    public ResponseEntity<CallResponse> call(@RequestParam("area") List<String> areas) {
        return new ResponseEntity<>(callService.handleCall(areas), HttpStatus.OK);
    }
}

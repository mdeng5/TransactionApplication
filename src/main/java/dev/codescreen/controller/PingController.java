package dev.codescreen.controller;

import dev.codescreen.model.Ping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/ping")
public class PingController {

    @GetMapping
    public ResponseEntity<Ping> getPing() {
        Ping ping = new Ping(LocalDateTime.now());
        return ResponseEntity.ok(ping);
    }
}

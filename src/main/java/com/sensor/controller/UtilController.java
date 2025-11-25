package com.sensor.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/util")
@CrossOrigin
@Tag(name = "Util")
public class UtilController {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @GetMapping("/bcrypt")
    @Operation(summary = "生成BCrypt哈希（开发辅助）")
    public String bcrypt(@RequestParam String text) {
        return encoder.encode(text);
    }
}

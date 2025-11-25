package com.sensor.controller;

import com.sensor.entity.Account;
import com.sensor.repository.AccountRepository;
import com.sensor.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 认证接口
 *
 * 提供注册、登录与密码重置。采用 JWT 无状态认证，密码使用 BCrypt 哈希存储。
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin
@Tag(name = "Auth")
public class AuthController {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private JwtService jwtService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * 注册请求 DTO
     */
    public static class RegisterRequest {
        /** 用户名 */
        public String username;
        /** 明文密码（服务端将使用BCrypt存储） */
        public String password;
        /** 显示名称 */
        public String displayName;
    }

    /**
     * 登录请求 DTO
     */
    public static class LoginRequest {
        /** 用户名 */
        public String username;
        /** 明文密码 */
        public String password;
    }

    /**
     * 登录/注册响应 DTO（携带访问令牌）
     */
    public static class TokenResponse {
        /** 访问令牌（JWT） */
        public String token;
        /** 主账号ID */
        public Long accountId;
        /** 用户名 */
        public String username;
        /** 显示名称 */
        public String displayName;
        /** 成员上限 */
        public Integer maxMembers;
    }

    /**
     * 注册主账号（默认成员上限为5）
     */
    @PostMapping("/register")
    @Operation(summary = "注册主账号")
    public TokenResponse register(@RequestBody RegisterRequest req) {
        Optional<Account> existing = accountRepository.findByUsername(req.username);
        if (existing.isPresent()) throw new RuntimeException("username exists");
        Account a = new Account();
        a.setUsername(req.username);
        a.setPasswordHash(encoder.encode(req.password));
        a.setDisplayName(req.displayName);
        a.setMaxMembers(5);
        a.setCreatedAt(ZonedDateTime.now(ZoneId.of("UTC+8")));
        accountRepository.save(a);
        Map<String,Object> claims = new HashMap<>();
        claims.put("accountId", a.getId());
        String token = jwtService.issue(a.getUsername(), claims, 7*24*3600);
        TokenResponse t = new TokenResponse();
        t.token = token; t.accountId = a.getId(); t.username = a.getUsername(); t.displayName = a.getDisplayName(); t.maxMembers = a.getMaxMembers();
        return t;
    }

    /**
     * 主账号登录，返回访问令牌
     */
    @PostMapping("/login")
    @Operation(summary = "主账号登录")
    public TokenResponse login(@RequestBody LoginRequest req) {
        Account a = accountRepository.findByUsername(req.username).orElseThrow(() -> new RuntimeException("not found"));
        if (!encoder.matches(req.password, a.getPasswordHash())) throw new RuntimeException("invalid");
        Map<String,Object> claims = new HashMap<>();
        claims.put("accountId", a.getId());
        String token = jwtService.issue(a.getUsername(), claims, 7*24*3600);
        TokenResponse t = new TokenResponse();
        t.token = token; t.accountId = a.getId(); t.username = a.getUsername(); t.displayName = a.getDisplayName(); t.maxMembers = a.getMaxMembers();
        return t;
    }

    /**
     * 重置密码请求 DTO
     */
    public static class ResetRequest {
        /** 旧密码 */
        public String oldPassword;
        /** 新密码 */
        public String newPassword;
    }

    /**
     * 主账号重置密码（需要携带Authorization: Bearer <token>）
     */
    @PostMapping("/reset")
    @Operation(summary = "重置主账号密码")
    public void reset(@RequestHeader("Authorization") String auth, @RequestBody ResetRequest req) {
        String token = auth != null && auth.startsWith("Bearer ") ? auth.substring(7) : auth;
        Map<String,Object> claims = jwtService.parse(token);
        Long accountId = ((Number)claims.get("accountId")).longValue();
        Account a = accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("not found"));
        if (!encoder.matches(req.oldPassword, a.getPasswordHash())) throw new RuntimeException("invalid");
        a.setPasswordHash(encoder.encode(req.newPassword));
        accountRepository.save(a);
    }
}

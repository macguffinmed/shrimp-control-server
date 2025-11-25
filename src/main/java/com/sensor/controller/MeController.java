package com.sensor.controller;

import com.sensor.entity.Account;
import com.sensor.repository.AccountRepository;
import com.sensor.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 个人信息接口
 *
 * 返回当前登录主账号的基本信息，用于“我的/个人中心”页面展示。
 */
@RestController
@RequestMapping("/api/me")
@CrossOrigin
@Tag(name = "Me")
public class MeController {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private JwtService jwtService;

    /**
     * 个人信息响应 DTO
     */
    public static class MeResponse {
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
     * 获取当前登录账号信息
     */
    @GetMapping
    @Operation(summary = "获取当前登录账号信息")
    public MeResponse me(@RequestHeader("Authorization") String auth) {
        String token = auth != null && auth.startsWith("Bearer ") ? auth.substring(7) : auth;
        Long accountId = ((Number)jwtService.parse(token).get("accountId")).longValue();
        Account a = accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("not found"));
        MeResponse r = new MeResponse();
        r.accountId = a.getId();
        r.username = a.getUsername();
        r.displayName = a.getDisplayName();
        r.maxMembers = a.getMaxMembers();
        return r;
    }
}

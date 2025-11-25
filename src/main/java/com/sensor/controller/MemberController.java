package com.sensor.controller;

import com.sensor.entity.Account;
import com.sensor.entity.Member;
import com.sensor.repository.AccountRepository;
import com.sensor.repository.MemberRepository;
import com.sensor.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 成员管理接口
 *
 * 针对当前登录主账号管理其成员列表，支持增改删与分页查询。
 */
@RestController
@RequestMapping("/api/members")
@CrossOrigin
@Tag(name = "Members")
public class MemberController {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private JwtService jwtService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private Account currentAccount(String auth) {
        String token = auth != null && auth.startsWith("Bearer ") ? auth.substring(7) : auth;
        Long accountId = ((Number)jwtService.parse(token).get("accountId")).longValue();
        return accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("not found"));
    }

    /**
     * 成员分页列表
     */
    @GetMapping
    @Operation(summary = "成员分页列表")
    public Page<Member> list(@RequestHeader("Authorization") String auth,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "20") int size) {
        Account a = currentAccount(auth);
        return memberRepository.findByAccount(a, PageRequest.of(page, size));
    }

    /**
     * 成员创建请求 DTO
     */
    public static class CreateRequest {
        /** 成员用户名 */
        public String username;
        /** 明文密码（服务端BCrypt存储） */
        public String password;
        /** 成员显示名称 */
        public String displayName;
    }

    /**
     * 新增成员（受主账号 maxMembers 限制）
     */
    @PostMapping
    @Operation(summary = "新增成员")
    public Member create(@RequestHeader("Authorization") String auth, @RequestBody CreateRequest req) {
        Account a = currentAccount(auth);
        if (memberRepository.countByAccount(a) >= a.getMaxMembers()) throw new RuntimeException("limit");
        Member m = new Member();
        m.setAccount(a);
        m.setUsername(req.username);
        m.setPasswordHash(encoder.encode(req.password));
        m.setDisplayName(req.displayName);
        return memberRepository.save(m);
    }

    /**
     * 成员更新请求 DTO
     */
    public static class UpdateRequest {
        /** 成员显示名称 */
        public String displayName;
        /** 新密码（可选） */
        public String password;
    }

    /**
     * 更新成员信息（允许改名与改密）
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新成员信息")
    public Member update(@RequestHeader("Authorization") String auth, @PathVariable Long id, @RequestBody UpdateRequest req) {
        Account a = currentAccount(auth);
        Member m = memberRepository.findById(id).orElseThrow(() -> new RuntimeException("not found"));
        if (!m.getAccount().getId().equals(a.getId())) throw new RuntimeException("forbidden");
        m.setDisplayName(req.displayName);
        if (req.password != null && !req.password.isEmpty()) m.setPasswordHash(encoder.encode(req.password));
        return memberRepository.save(m);
    }

    /**
     * 删除成员
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除成员")
    public void delete(@RequestHeader("Authorization") String auth, @PathVariable Long id) {
        Account a = currentAccount(auth);
        Member m = memberRepository.findById(id).orElseThrow(() -> new RuntimeException("not found"));
        if (!m.getAccount().getId().equals(a.getId())) throw new RuntimeException("forbidden");
        memberRepository.deleteById(id);
    }
}

package com.sensor.entity;

import javax.persistence.*;

/**
 * 成员实体
 * 隶属于主账号，用于多人协作登录与权限控制的基础信息。
 */
@Entity
@Table(name = "member")
public class Member {
    /**
     * 成员ID（自增主键）
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 所属主账号
     */
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    /**
     * 成员用户名
     */
    @Column(name = "username", nullable = false)
    private String username;

    /**
     * 成员密码哈希（BCrypt）
     */
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    /**
     * 成员显示名称（昵称）
     */
    @Column(name = "display_name")
    private String displayName;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
}

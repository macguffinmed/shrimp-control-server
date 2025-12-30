package com.sensor.entity;

import javax.persistence.*;
import java.time.ZonedDateTime;

/**
 * 主账号实体
 * 用于登录与成员管理，保存账号基础信息与成员上限。
 */
@Entity
@Table(name = "account")
public class Account {
    /**
     * 主账号ID（自增主键）
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户名（唯一）
     */
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    /**
     * 密码哈希（BCrypt）
     */
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    /**
     * 显示名称（昵称）
     */
    @Column(name = "display_name")
    private String displayName;

    /**
     * 成员上限数量
     */
    @Column(name = "max_members")
    private Integer maxMembers;

    /**
     * 创建时间（含时区）
     */
    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public Integer getMaxMembers() { return maxMembers; }
    public void setMaxMembers(Integer maxMembers) { this.maxMembers = maxMembers; }
    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }
}

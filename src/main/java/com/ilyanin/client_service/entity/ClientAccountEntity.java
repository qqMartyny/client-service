package com.ilyanin.client_service.entity;

import java.time.OffsetDateTime;

import com.ilyanin.client_service.model.ClientStatus;
import com.ilyanin.client_service.model.Gender;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Table(name = "client_account")
@Entity 
public class ClientAccountEntity {

    @Id 
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 1)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private ClientStatus status;

    @Column(name = "create_dttm", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "modify_dttm", nullable = false)
    private OffsetDateTime updatedAt;

    public ClientAccountEntity(String fullName, Gender gender) {
        this.fullName = fullName;
        this.gender = gender;
        this.status = ClientStatus.ACTIVE;
    }

    protected ClientAccountEntity() {}

    @PrePersist
    void onCreate() {
        var now = OffsetDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public ClientStatus getStatus() {
        return status;
    }

    public void setStatus(ClientStatus status) {
        this.status = status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

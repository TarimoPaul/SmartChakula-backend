package com.SmartChakula.Utils;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.GeneratedValue;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.LastModifiedBy;

import jakarta.persistence.Column;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
@MappedSuperclass
@NoArgsConstructor
@Data
public class BaseEntity implements Serializable {

    @Column(name = "uid", unique = true, nullable = false)
    private String uid;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "updated_by")
    @LastModifiedBy
    private String updatedBy;

    @Column(name = "deleted_by")
    private String deletedBy;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @PrePersist
    public void generateUid() {
        if (this.uid == null || this.uid.isEmpty()) {
            this.uid = java.util.UUID.randomUUID().toString();
        }
    }

    public void delete(){
        deletedAt = LocalDateTime.now();
        deletedBy = LoggedUser.getUid();
        isActive = false;
        isDeleted = true;
    }
}
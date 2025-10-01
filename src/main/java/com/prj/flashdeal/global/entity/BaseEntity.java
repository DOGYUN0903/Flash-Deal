package com.prj.flashdeal.global.entity;


import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@MappedSuperclass
public abstract class BaseEntity extends BaseTimeEntity {

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    // soft delete 메서드
    public void delete() {
        this.isDeleted = true;
    }
}

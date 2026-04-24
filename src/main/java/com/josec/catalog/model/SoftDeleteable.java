package com.josec.catalog.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass // Para que las demás entidades hereden este comportamiento
public abstract class SoftDeleteable {

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Para saber si está en la papelera
    public boolean isDeleted() {
        return deletedAt != null;
    }
}

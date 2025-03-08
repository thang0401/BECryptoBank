package com.cryptobank.backend.entity;

import com.cryptobank.backend.utils.IdGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;

/**
 * Chứa thuộc tính cần thiết trong mỗi entity.
 */
@Getter
@Setter
@ToString
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @Column(name = "id", nullable = false)
    private Long id = IdGenerator.generate();

    @Column(name = "delete_yn")
    private Boolean deleted = false;

    @Column(name = "created_date")
    private OffsetDateTime createdDate = OffsetDateTime.now();

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "modified_date")
    private OffsetDateTime modifiedDate;

    @Column(name = "modified_by")
    private String modifiedBy;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseEntity that = (BaseEntity) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

}

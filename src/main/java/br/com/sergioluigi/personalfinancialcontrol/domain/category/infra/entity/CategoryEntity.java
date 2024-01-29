package br.com.sergioluigi.personalfinancialcontrol.domain.category.infra.entity;

import br.com.sergioluigi.personalfinancialcontrol.domain.category.domain.model.CategoryModel;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "category")
@SQLRestriction("was_deleted = false")
@EntityListeners(AuditingEntityListener.class)
public class CategoryEntity {

    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @Column(unique = true)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String color;

    @CreatedDate
    private LocalDateTime createdOn;

    @LastModifiedDate
    private LocalDateTime lastUpdateOn;

    @Column(nullable = false)
    @SoftDelete(columnName = "wasDeleted")
    private boolean wasDeleted;

    public CategoryEntity(CategoryModel categoryModel) {
        this.id = categoryModel.getId();
        this.name = categoryModel.getName();
        this.description = categoryModel.getDescription();
        this.color = categoryModel.getColor();
        this.createdOn = categoryModel.getCreatedOn();
        this.lastUpdateOn = categoryModel.getLastUpdateOn();
    }

    public static CategoryEntity createNewEntityOf(CategoryModel categoryModel) {
        return new CategoryEntity(categoryModel);
    }

    public CategoryModel toModel() {
        return new CategoryModel(id, name, description, color, createdOn, lastUpdateOn);
    }
}

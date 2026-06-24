package com.matheus.estoque.supplier.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.matheus.estoque.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "suppliers",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_suppliers_user_name",
                columnNames = {"user_id", "name"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 180)
    private String name;

    @Column(length = 120)
    private String document;

    @Column(length = 120)
    private String phone;

    @Column(length = 180)
    private String email;

    @Column(length = 2000)
    private String notes;

    @Column
    @Builder.Default
    private Boolean active = true;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}

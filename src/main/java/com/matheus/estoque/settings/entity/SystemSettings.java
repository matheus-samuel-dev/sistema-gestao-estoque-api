package com.matheus.estoque.settings.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.matheus.estoque.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "system_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 180)
    private String companyName;

    @Column(length = 1000)
    private String logoUrl;

    @Column(length = 40)
    private String cnpj;

    @Column(length = 80)
    private String phone;

    @Column(length = 180)
    private String email;

    @Column
    @Builder.Default
    private Integer defaultMinimumStock = 0;

    @Column(length = 80)
    @Builder.Default
    private String businessType = "Outro";

    @Column
    @Builder.Default
    private Boolean batchControl = false;

    @Column
    @Builder.Default
    private Boolean serialControl = false;

    @Column(length = 12)
    @Builder.Default
    private String currency = "BRL";

    @Column(length = 80)
    @Builder.Default
    private String timezone = "America/Sao_Paulo";

    @Column(length = 40)
    @Builder.Default
    private String dateFormat = "dd/MM/yyyy";

    @Column
    @Builder.Default
    private Boolean onboardingCompleted = false;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;
}

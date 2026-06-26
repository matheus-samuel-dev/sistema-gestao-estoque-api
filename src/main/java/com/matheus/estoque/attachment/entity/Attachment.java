package com.matheus.estoque.attachment.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.matheus.estoque.product.entity.Product;
import com.matheus.estoque.stockmovement.entity.StockMovement;
import com.matheus.estoque.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "attachments")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Attachment {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false, length = 255) private String fileName;
    @Column(nullable = false, length = 120) private String contentType;
    @Column(nullable = false) private long size;
    @JsonIgnore
    @Basic(fetch = FetchType.LAZY)
    @JdbcTypeCode(SqlTypes.VARBINARY)
    @Column(name = "data_bytes", columnDefinition = "bytea")
    private byte[] data;
    @JsonIgnore
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "data", insertable = false, updatable = false)
    private byte[] legacyData;
    @JsonIgnore @ManyToOne(fetch = FetchType.LAZY) private Product product;
    @JsonIgnore @ManyToOne(fetch = FetchType.LAZY) private StockMovement movement;
    @JsonIgnore @ManyToOne(fetch = FetchType.LAZY, optional = false) private User user;
    @CreationTimestamp private LocalDateTime createdAt;
}

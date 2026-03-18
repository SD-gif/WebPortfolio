package com.seodong.portfolio.certification;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "certification")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Certification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 100)
    private String issuer;

    @Column(length = 20)
    private String acquiredDate;

    /** CERT(자격증) | AWARD(공모전/수상) */
    @Column(nullable = false, length = 10)
    private String type;

    private int sortOrder;

    public void update(String name, String issuer, String acquiredDate, String type, int sortOrder) {
        this.name         = name;
        this.issuer       = issuer;
        this.acquiredDate = acquiredDate;
        this.type         = type;
        this.sortOrder    = sortOrder;
    }
}

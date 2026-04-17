package com.seodong.portfolio.experience;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "experience_tech_stack")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ExperienceTechStack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "experience_id", nullable = false)
    private Experience experience;

    @Column(nullable = false, length = 50)
    private String tech;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;
}

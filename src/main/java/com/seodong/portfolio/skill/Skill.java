package com.seodong.portfolio.skill;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "skill")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private SkillCategory category;

    @Column(nullable = false, length = 50)
    private String name;

    private int sortOrder;
}

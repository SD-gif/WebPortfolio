package com.seodong.portfolio.skill;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "skill_category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SkillCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    private int sortOrder;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    @Builder.Default
    private List<Skill> skills = new ArrayList<>();

    public void update(String name, int sortOrder) {
        this.name      = name;
        this.sortOrder = sortOrder;
    }

    public Skill addSkill(String name, int sortOrder) {
        Skill skill = Skill.builder()
                .category(this)
                .name(name)
                .sortOrder(sortOrder)
                .build();
        this.skills.add(skill);
        return skill;
    }
}

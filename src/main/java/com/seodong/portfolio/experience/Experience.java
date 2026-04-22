package com.seodong.portfolio.experience;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "experience")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Experience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 10)
    private String icon;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 80)
    private String summary;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(name = "linked_project_id")
    private Long linkedProjectId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @BatchSize(size = 20)
    @OneToMany(mappedBy = "experience", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    @Builder.Default
    private List<ExperienceTechStack> techStacks = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "experience_situation_item",
            joinColumns = @JoinColumn(name = "experience_id")
    )
    @OrderColumn(name = "sort_order")
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    @BatchSize(size = 20)
    @Builder.Default
    private List<String> situation = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "experience_approach_item",
            joinColumns = @JoinColumn(name = "experience_id")
    )
    @OrderColumn(name = "sort_order")
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    @BatchSize(size = 20)
    @Builder.Default
    private List<String> approach = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "experience_learned_item",
            joinColumns = @JoinColumn(name = "experience_id")
    )
    @OrderColumn(name = "sort_order")
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    @BatchSize(size = 20)
    @Builder.Default
    private List<String> learned = new ArrayList<>();

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public void update(String icon, String title, String summary,
                       String imageUrl, int sortOrder, Long linkedProjectId) {
        this.icon             = icon;
        this.title            = title;
        this.summary          = summary;
        this.imageUrl         = imageUrl;
        this.sortOrder        = sortOrder;
        this.linkedProjectId  = linkedProjectId;
    }

    public void replaceTechStacks(List<String> techs) {
        this.techStacks.clear();
        if (techs == null) return;
        for (int i = 0; i < techs.size(); i++) {
            this.techStacks.add(ExperienceTechStack.builder()
                    .experience(this)
                    .tech(techs.get(i))
                    .sortOrder(i + 1)
                    .build());
        }
    }

    public void replaceSituation(List<String> items) { replaceInto(this.situation, items); }
    public void replaceApproach(List<String> items)  { replaceInto(this.approach, items); }
    public void replaceLearned(List<String> items)   { replaceInto(this.learned, items); }

    private static void replaceInto(List<String> target, List<String> items) {
        target.clear();
        if (items == null) return;
        for (String item : items) {
            if (item != null && !item.isBlank()) target.add(item);
        }
    }
}

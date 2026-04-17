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

    @Column(nullable = false, columnDefinition = "TEXT")
    private String situation;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String approach;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String learned;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @BatchSize(size = 20)
    @OneToMany(mappedBy = "experience", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    @Builder.Default
    private List<ExperienceTechStack> techStacks = new ArrayList<>();

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public void update(String icon, String title, String summary, String situation,
                       String approach, String learned, String imageUrl, int sortOrder) {
        this.icon      = icon;
        this.title     = title;
        this.summary   = summary;
        this.situation = situation;
        this.approach  = approach;
        this.learned   = learned;
        this.imageUrl  = imageUrl;
        this.sortOrder = sortOrder;
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
}

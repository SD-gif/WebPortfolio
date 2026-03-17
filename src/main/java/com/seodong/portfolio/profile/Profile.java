package com.seodong.portfolio.profile;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "profile")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 100)
    private String role;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(length = 255)
    private String githubUrl;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private int projectCount;
    private int expYears;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public void update(String name, String role, String email,
                       String githubUrl, String bio,
                       int projectCount, int expYears) {
        this.name         = name;
        this.role         = role;
        this.email        = email;
        this.githubUrl    = githubUrl;
        this.bio          = bio;
        this.projectCount = projectCount;
        this.expYears     = expYears;
    }
}

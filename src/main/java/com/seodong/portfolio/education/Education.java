package com.seodong.portfolio.education;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "education")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Education {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String institution;

    @Column(nullable = false, length = 50)
    private String degree;

    @Column(nullable = false, length = 100)
    private String major;

    @Column(length = 20)
    private String startDate;

    @Column(length = 20)
    private String endDate;

    private int sortOrder;

    public void update(String institution, String degree, String major,
                       String startDate, String endDate, int sortOrder) {
        this.institution = institution;
        this.degree      = degree;
        this.major       = major;
        this.startDate   = startDate;
        this.endDate     = endDate;
        this.sortOrder   = sortOrder;
    }
}

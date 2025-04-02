package com.nashm.ultimate.ultimate_frisbee_ai.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "queries")
public class Query {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String question;

    @Column(nullable = false, length = 10000)
    private String answer;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column
    private Integer rating;

    @Column(name = "user_feedback")
    private String userFeedback;

}

package com.praveen.entities;

import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notice_board")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String heading;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @OneToMany(
            mappedBy = "noticeBoard",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<NoticeAttachment> attachments;
}
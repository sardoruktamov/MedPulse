package api.medpulse.uz.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "post_comment")
@Getter
@Setter
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String postId; // Qaysi postga tegishli

    @Column(nullable = false)
    private Integer profileId; // Kim yozdi

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profileId", insertable = false, updatable = false)
    private ProfileEntity profile;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content; // Izoh matni

    private LocalDateTime createdDate = LocalDateTime.now();
}
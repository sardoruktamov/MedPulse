package api.medpulse.uz.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "university")
@Getter
@Setter
public class UniversityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name; // M: Toshkent Tibbiyot Akademiyasi

    @Column(name = "active")
    private Boolean active = true; // OTM yopilib ketsa false qilinadi
}
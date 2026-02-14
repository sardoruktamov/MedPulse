package api.medpulse.uz.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "regions")
@Data
public class RegionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;
}
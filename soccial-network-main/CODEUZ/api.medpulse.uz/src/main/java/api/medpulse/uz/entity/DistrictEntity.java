package api.medpulse.uz.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "districts")
@Data
public class DistrictEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    // Many-to-One: Ko'p tumanlar bitta viloyatga tegishli
    @ManyToOne
    @JoinColumn(name = "region_id", nullable = false)
    private RegionEntity region;
}

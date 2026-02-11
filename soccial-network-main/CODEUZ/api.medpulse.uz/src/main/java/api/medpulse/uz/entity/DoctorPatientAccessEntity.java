package api.medpulse.uz.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "doctor_patient_access")
@Getter
@Setter
public class DoctorPatientAccessEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "doctor_id")
    private Long doctorId; // Ruxsat olgan Shifokor (Profile ID)

    @Column(name = "patient_id")
    private String patientId; // Ko'rilayotgan Bemor (Patient UUID)

    @Column(name = "expire_date")
    private LocalDateTime expireDate; // Ruxsat tugash vaqti (M: +1 soat)

    @Column(name = "created_date")
    private LocalDateTime createdDate = LocalDateTime.now();
}

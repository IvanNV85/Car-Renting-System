package fu.carrentingsystem.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name ="car_producer")
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CarProducer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "producer_id")
    int producerId;

    @Column(name = "producer_name", length = 100)
    String producerName;

    @Column(name = "address", length = 200)
    String address;

    @Column(name = "country", length = 100)
    String country;

}

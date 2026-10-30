package fu.carrentingsystem.entity;

import fu.carrentingsystem.constant.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "account")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = "customer")
@EqualsAndHashCode(exclude = "customer")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    int accountId;

    @Column(name = "account_name", nullable = false, length = 100)
    String accountName;

    @Column(name = "email", nullable = false, length = 200)
    String email;

    @Column(name = "password", nullable = false, length = 200)
    String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 10)
    Role role;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Customer customer;
}

package com.serezka.database.model.university;

import com.serezka.database.model.telegram.TelegramUser;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Entity @Table(name = "persons")
@NoArgsConstructor @AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Getter
@Setter
@ToString
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String name;

    @Column(name = "isu_id", unique = true, nullable = false)
    Long isuId;

    @OneToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @ToString.Exclude
    TelegramUser telegramUser;

    @Column(nullable = false)
    @Builder.Default
    Role role = Role.STUDENT;

    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
    @RequiredArgsConstructor
    public enum Role {
        STUDENT("студент", 10), TEACHER("преподаватель", 1000), ADMIN("администратор", 10000);

        String name;
        int lvl;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Person person = (Person) o;
        return getId() != null && Objects.equals(getId(), person.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}

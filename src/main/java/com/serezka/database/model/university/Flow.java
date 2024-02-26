package com.serezka.database.model.university;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

@Entity @Table(name = "flows")
@NoArgsConstructor @AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Data
public class Flow {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    String name;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "practice_ids", referencedColumnName = "id")
    @Builder.Default
    List<Practice> practices = Collections.emptyList();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "student_ids", referencedColumnName = "id")
    @Builder.Default
    List<Student> students = Collections.emptyList();
}

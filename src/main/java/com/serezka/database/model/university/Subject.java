package com.serezka.database.model.university;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Table(name = "flows")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Data
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String subjectName;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "flows_ids")
    List<Flow> flows;

    public Subject(String subjectName, List<Flow> flows) {
        this.subjectName = subjectName;
        this.flows = flows;
    }
}

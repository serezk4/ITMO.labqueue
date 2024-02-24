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

    @Column(unique = true)
    String name;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "flows_ids")
    List<Flow> flows;

    public Subject(String name, List<Flow> flows) {
        this.name = name;
        this.flows = flows;
    }
}

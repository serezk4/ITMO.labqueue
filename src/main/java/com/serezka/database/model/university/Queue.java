package com.serezka.database.model.university;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Table(name = "queue")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Data
public class Queue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "queue_id", referencedColumnName = "id")
    List<QueueItem> items;

    @Builder.Default
    @Enumerated(value = EnumType.STRING)
    State state = State.WAITING;

    public enum State {
        WAITING,
        REGISTRATION_OPEN,
        REGISTRATION_CLOSED,
        FINISHED
    }
}

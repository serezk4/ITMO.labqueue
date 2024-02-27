package com.serezka.database.model.university;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity @Table(name = "queue")
@NoArgsConstructor @AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Data
public class Queue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "practice_id", referencedColumnName = "id", unique = true)
    Practice practice;

    @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "queue_item_ids", referencedColumnName = "id")
    List<QueueItem> items;

    @Builder.Default
    State state = State.WAITING;

    public Queue(Practice practice, List<QueueItem> items) {
        this.practice = practice;
        this.items = items;
    }

    public enum State {
        WAITING,
        REGISTRATION_OPEN,
        REGISTRATION_CLOSED,
        FINISHED
    }
}

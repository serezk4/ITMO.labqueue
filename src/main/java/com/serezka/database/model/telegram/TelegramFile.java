package com.serezka.database.model.telegram;

import com.serezka.database.model.university.Flow;
import com.serezka.database.model.university.Person;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "files")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Getter @Setter
@ToString
public class TelegramFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Lob
    byte[] content;

    String name;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    Person person;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    Flow flow;
}

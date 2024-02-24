package com.serezka.database.model.university;

import com.serezka.database.model.telegram.TelegramUser;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity @Table(name = "students")
@NoArgsConstructor @AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Data
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "telegram_id")
    TelegramUser telegramUser;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "group_id")
    Group group;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "subject_ids")
    List<Subject> subjects;

    public Student(TelegramUser telegramUser, Group group, List<Subject> subjects) {
        this.telegramUser = telegramUser;
        this.group = group;
        this.subjects = subjects;
    }
}

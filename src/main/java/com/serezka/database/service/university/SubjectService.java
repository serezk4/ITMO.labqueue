package com.serezka.database.service.university;

import com.serezka.database.model.university.Subject;
import com.serezka.database.repository.university.SubjectRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SubjectService {
    SubjectRepository subjectRepository;

    @Transactional
    public Subject save(Subject subject) {
        return subjectRepository.save(subject);
    }

    @Transactional
    public boolean existsByName(String name) {
        return subjectRepository.existsByName(name);
    }

    @Transactional
    public Subject findByName(String name) {
        return subjectRepository.findByName(name);
    }
}

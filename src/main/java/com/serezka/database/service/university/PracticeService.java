package com.serezka.database.service.university;

import com.serezka.database.model.university.Practice;
import com.serezka.database.model.university.Teacher;
import com.serezka.database.repository.university.PracticeRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PracticeService {
    PracticeRepository practiceRepository;

    @Transactional
    public Practice save(Practice practice) {
        return practiceRepository.save(practice);
    }

    @Transactional
    public void removeById(Long id) {
        practiceRepository.deleteById(id);
    }

    @Transactional
    public Optional<Practice> findById(Long id) {
        return practiceRepository.findById(id);
    }

    @Transactional
    public List<Practice> findPracticesByTimeRange(ZonedDateTime startTime, ZonedDateTime endTime) {
        return practiceRepository.findPracticesByTimeRange(startTime, endTime);
    }

    @Transactional
    public List<Practice> findAllByTeacher(Teacher teacher) {
        return practiceRepository.findAllByTeachersContaining(teacher);
    }
}

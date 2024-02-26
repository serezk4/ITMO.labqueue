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
    public List<Practice> findPracticesByDateAndTimeRange(LocalDate date, ZonedDateTime startTime, ZonedDateTime endTime) {
        return practiceRepository.findPracticesByDateAndTimeRange(date, startTime, endTime);
    }

    @Transactional
    public List<Practice> findAllByTeacher(Teacher teacher) {
        return practiceRepository.findAllByTeacher(teacher);
    }
}

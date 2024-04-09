package com.serezka.database.service.university;

import com.serezka.database.model.university.Flow;
import com.serezka.database.model.university.Practice;
import com.serezka.database.model.university.Queue;
import com.serezka.database.repository.university.PracticeRepository;
import com.serezka.database.repository.university.QueueRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class QueueService {
    QueueRepository queueRepository;

    @Transactional
    public Queue save(Queue queue) {
        return queueRepository.save(queue);
    }

    @Transactional
    public Queue findById(Long id) {
        return queueRepository.findById(id).orElse(null);
    }

    @Transactional
    public void removeById(Long id) {
        queueRepository.deleteById(id);
    }

//    @Transactional
//    public Optional<Queue> findByPractice(Practice practice) {
//        return queueRepository.findByPractice(practice);
//    }
}

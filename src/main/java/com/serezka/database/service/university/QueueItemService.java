package com.serezka.database.service.university;

import com.serezka.database.model.university.Person;
import com.serezka.database.model.university.Queue;
import com.serezka.database.model.university.QueueItem;
import com.serezka.database.repository.university.QueueItemRepository;
import com.serezka.database.repository.university.QueueRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class QueueItemService {
    QueueItemRepository queueItemRepository;

    @Transactional
    public QueueItem save(QueueItem queueItem) {
        return queueItemRepository.save(queueItem);
    }

    @Transactional
    public Optional<QueueItem> findById(Long id) {
        return queueItemRepository.findById(id);
    }

    @Transactional
    public void removeById(Long id) {
        queueItemRepository.deleteById(id);
    }

    @Transactional
    public boolean existsByPersonAndQueue(Person person, Queue queue) {
        return queueItemRepository.existsByPersonAndQueue(person, queue);
    }
}

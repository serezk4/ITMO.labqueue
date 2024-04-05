package com.serezka.database.service.university;

import com.serezka.database.model.university.Flow;
import com.serezka.database.model.university.Student;
import com.serezka.database.repository.university.FlowRepository;
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
public class FlowService {
    FlowRepository flowRepository;

    @Transactional
    public Flow save(Flow flow) {
        return flowRepository.save(flow);
    }

    @Transactional
    public void removeById(Long id) {
        flowRepository.deleteById(id);
    }

    @Transactional
    public Optional<Flow> findById(Long id) {
        return flowRepository.findById(id);
    }

    @Transactional
    public boolean existsByName(String name) {
        return flowRepository.existsByName(name);
    }

    @Transactional
    public Flow findByName(String name) {
        return flowRepository.findByName(name);
    }

    @Transactional
    public List<Flow> findAllByStudentsContaining(Student student) {
        return flowRepository.findAllByStudentsContaining(student);
    }
}

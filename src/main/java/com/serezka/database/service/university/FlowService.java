package com.serezka.database.service.university;

import com.serezka.database.model.university.Flow;
import com.serezka.database.model.university.Person;
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
    public Optional<Flow> findByNameAndSecret(String name, String secret) {
        return flowRepository.findByNameAndSecret(name, secret);
    }

    @Transactional
    public List<Flow> findAllByPeopleContaining(Person person) {
        return flowRepository.findAllByPeopleContaining(person);
    }
}

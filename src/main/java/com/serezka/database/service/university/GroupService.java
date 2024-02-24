package com.serezka.database.service.university;

import com.serezka.database.model.university.Group;
import com.serezka.database.repository.university.GroupRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class GroupService {
    GroupRepository groupRepository;

    @Transactional
    public Group save(Group group) {
        return groupRepository.save(group);
    }

    @Transactional
    public boolean existsByName(String name) {
        return groupRepository.existsByName(name);
    }

    @Transactional
    public Group findByName(String name) {
        return groupRepository.findByName(name);
    }
}

package com.serezka.database.service.telegram;

import com.serezka.database.model.telegram.History;
import com.serezka.database.repository.telegram.MessageRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MessageService {
    MessageRepository messageRepository;

    @Transactional
    public History save(History history) {
        return messageRepository.save(history);
    }

    @Transactional
    public History findTopByChatId(Long chatId) {
        return messageRepository.findTopByChatId(chatId);
    }

    @Transactional
    public List<History> findAllByChatId(Long chatId) {
        return messageRepository.findAllByChatId(chatId);
    }

    @Transactional
    public void removeByMessageId(Integer messageId) {
        messageRepository.removeByMessageId(messageId);
    }
}

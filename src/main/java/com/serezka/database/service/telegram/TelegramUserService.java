package com.serezka.database.service.telegram;

import com.serezka.database.model.telegram.TelegramUser;
import com.serezka.database.repository.telegram.TelegramUserRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor @Log4j2
public class TelegramUserService {
    TelegramUserRepository telegramUserRepository;

    // save
    @Transactional
    public TelegramUser save(TelegramUser TelegramUser) {
        log.debug("saved user {}", TelegramUser.toString());
        return telegramUserRepository.save(TelegramUser);
    }

    // count
    @Transactional
    public long count() {
        log.debug("counted all users");
        return telegramUserRepository.count();
    }

    @Transactional
    public long countByRole(TelegramUser.Role role) {
        log.debug("counted users by role {}", role.getName());
        return telegramUserRepository.countAllByRole(role);
    }

    // find
    @Transactional
    public Optional<TelegramUser> findByChatIdOrUsername(Long chatId, String username) {
        log.debug("trying to found user with chatId {} or username {}", chatId, username);
        return telegramUserRepository.findByChatIdOrUsername(chatId, username);
    }

    @Transactional
    public Optional<TelegramUser> findByChatId(Long chatId) {
        log.debug("trying to find user with chatId {}", chatId);
        return telegramUserRepository.findByChatId(chatId);
    }

//    @Transactional TODO
//    public Optional<User> findByChatId(Update update) {
//        log.debug("trying to find user by update with chatId {}", update.getChatId());
//        return userRepository.findByChatId(update.getChatId());
//    }

    @Transactional
    public Optional<TelegramUser> findByUsername(String username) {
        log.debug("trying to found user with username {}", username);
        return telegramUserRepository.findByUsername(username);
    }

    @Transactional
    public List<TelegramUser> findAllByRole(TelegramUser.Role role) {
        log.debug("trying to found all users with role {}", role.getName());
        return telegramUserRepository.findAllByRole(role);
    }

    // exists
    @Transactional
    public boolean existsByChatIdOrUsername(Long chatId, String username) {
        log.debug("checking if user with chatId {} or username {} existing", chatId, username);
        return telegramUserRepository.existsByChatIdOrUsername(chatId, username);
    }

    @Transactional
    public boolean existsByUsername(String username) {
        log.debug("checking if user with username {} existing", username);
        return telegramUserRepository.existsByUsername(username);
    }

    @Transactional
    public boolean existsByChatId(Long chatId) {
        log.debug("checking if user with chatId {} existing", chatId);
        return telegramUserRepository.existsByChatId(chatId);
    }

    // remove
    public boolean removeByChatId(Long chatId) {
        log.debug("trying to remove user with chatId {}", chatId);
        return telegramUserRepository.removeByChatId(chatId);
    }

    public boolean removeById(Long id) {
        log.debug("trying to remove user with id {}", id);
        return telegramUserRepository.removeById(id);
    }
}

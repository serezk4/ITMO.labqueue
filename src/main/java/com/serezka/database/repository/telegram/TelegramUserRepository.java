package com.serezka.database.repository.telegram;

import com.serezka.database.model.telegram.TelegramUser;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TelegramUserRepository extends JpaRepository<TelegramUser, Long> {
    long count();
    long countAllByRole(@NonNull TelegramUser.Role role);

    Optional<TelegramUser> findByChatIdOrUsername(@NonNull Long chatId, @NonNull String username);
    Optional<TelegramUser> findByChatId(@NonNull Long chatId);
    Optional<TelegramUser> findByUsername(@NonNull String username);
    List<TelegramUser> findAllByRole(@NonNull TelegramUser.Role role);

    boolean existsByChatIdOrUsername(@NonNull Long chatId, @NonNull String username);
    boolean existsByChatId(@NonNull Long chatId);
    boolean existsByUsername(@NonNull String username);

    boolean removeByChatId(@NonNull Long chatId);
    boolean removeById(@NonNull Long id);
}

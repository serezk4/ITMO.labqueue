package com.serezka.database.model.telegram;

import com.serezka.localization.Localization;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

/**
 * Entity for user
 * Related to telegram
 *
 * @version 1.0
 */
@Entity @Table(name = "tg_users")
@NoArgsConstructor @AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Getter @Setter
@ToString
public class TelegramUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    // basic user data
    @Column(name = "chat_id", unique = true, nullable = false)
    @NonNull
    Long chatId;

    @NonNull
    @Column(nullable = false)
    String username;

    // bot settings for user
    @Builder.Default
    @NonNull
    Role role = Role.USER;

    @Builder.Default
    @NonNull
    Localization.Type localization = Localization.Type.DEFAULT;

    @Builder.Default
    @Column(name = "delete_command_summon_messages")
    boolean deleteCommandSummonMessages = true;

    public TelegramUser(@NonNull Long chatId, @NonNull String username) {
        this.chatId = chatId;
        this.username = username;
    }

    public TelegramUser(@NonNull Long chatId, @NonNull String username, @NonNull Role role) {
        this.chatId = chatId;
        this.username = username;
        this.role = role;
    }

    public TelegramUser(@NonNull Long chatId, @NonNull String username, @NonNull Role role, @NonNull Localization.Type localization) {
        this.chatId = chatId;
        this.username = username;
        this.role = role;
        this.localization = localization;
    }

    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @Getter
    public enum Role {
        USER("user", 0), ADMIN_1("admin #1", 100);

        String name;
        int adminLvl;

        public static final Role MAX = Role.ADMIN_1;
        public static final Role MIN = Role.USER;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        TelegramUser that = (TelegramUser) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}

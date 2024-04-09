package com.serezka.telegram.updater;

import com.serezka.database.model.telegram.TelegramUser;
import com.serezka.database.model.university.*;
import com.serezka.database.service.telegram.TelegramUserService;
import com.serezka.database.service.university.*;
import com.serezka.telegram.bot.Bot;
import com.serezka.telegram.session.step.StepSessionConfiguration;
import com.serezka.telegram.util.keyboard.type.Inline;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDice;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackBundle;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Log4j2
public class PracticeUpdater extends Updater {
    PracticeService practiceService;
    TelegramUserService telegramUserService;
    QueueService queueService;
    PersonService personService;
    FlowService flowService;
    QueueItemService queueItemService;

    Bot bot;

    public PracticeUpdater(PracticeService practiceService, TelegramUserService telegramUserService, QueueService queueService, PersonService personService, FlowService flowService, QueueItemService queueItemService,
                           Bot bot) {
        super(0, 1, TimeUnit.MINUTES);

        this.practiceService = practiceService;
        this.telegramUserService = telegramUserService;
        this.queueService = queueService;
        this.personService = personService;
        this.flowService = flowService;
        this.queueItemService = queueItemService;
        this.bot = bot;
    }

    @Override
    public void update() {
        try {
            ZonedDateTime now = ZonedDateTime.now();
            ZonedDateTime future = now.plusMinutes(10);

            List<Practice> registrationOpenPractices = practiceService.findPracticesByTimeRange(now, future);
            List<Practice> closeRegistrationPractices = practiceService.findPracticesByTimeFromAndQueueState(now, Queue.State.REGISTRATION_OPEN);

            log.info("founded {} registrationOpenPractices for {}-{}", registrationOpenPractices.size(), now, future);
            log.info("founded {} closeRegistrationPractices for {}", closeRegistrationPractices.size(), now);

            registrationOpenPractices.forEach(this::handlePractice);
            closeRegistrationPractices.forEach(this::closeRegistrationAndGenerateQueue);
        } catch (Exception ex) {
            log.warn(ex.getMessage());
        }
    }

    private void closeRegistrationAndGenerateQueue(Practice practice) {
//        log.info("closing registration for {}", practice.toString());

        // get params
        final Flow flow = practice.getFlow();
        practice.getQueue().setState(Queue.State.REGISTRATION_CLOSED);
        queueService.save(practice.getQueue());

        // get students
        List<Person> students = practice.getFlow().getPeople();

        students.forEach(student -> {
            TelegramUser telegramUser = student.getTelegramUser();

            if (telegramUser == null) {
                log.warn("student {} has no telegram user", student.getName());
                return;
            }

            long chatId = telegramUser.getChatId();

            double position = bot.execute(SendDice.builder()
                    .chatId(telegramUser.getChatId())
                    .build()).getDice().getValue() + (Math.random() * (Math.random() + 1));

            bot.execute(SendMessage.builder()
                    .text(String.format("<b>Закрыта запись на практику:</b> \n<b>Поток:</b> %s\n<b>Начало:</b> %tT\n<b>Преподаватели:</b> %s\n\n<b>Вам выпало:</b> %.2f",
                            flow.getName(), practice.getBegin(), practice.getTeachers(), position))
                    .chatId(chatId)
                    .parseMode(ParseMode.HTML).build());

            QueueItem queueItem = queueItemService.findByPersonAndQueue(student, practice.getQueue())
                    .orElseGet(() -> queueItemService.save(QueueItem.builder()
                            .person(student)
                            .position(4 * 1000)
                            .queue(practice.getQueue())
                            .build()));

            queueItem.setPosition(queueItem.getPosition() + position);
            queueItemService.save(queueItem);
        });


        Set<QueueItem> queueItems = new TreeSet<>();
        students.forEach(student -> {
            TelegramUser telegramUser = student.getTelegramUser();

            if (telegramUser == null) {
                log.warn("student {} has no telegram user", student.getName());
                return;
            }

//            queueItemService.findByPersonAndQueue(student, practice.getQueue()).ifPresent(queueItems::add);
            // todo fix this
        });

    }

    private void handlePractice(Practice practice) {
        log.info("handling {}", practice.toString());

        // get params
        final Flow flow = practice.getFlow();

        // send info to students
        flow.getPeople().forEach(student -> {
            if (queueItemService.existsByPersonAndQueue(student, practice.getQueue())) {
                log.info("student {} already in queue", student);
                return;
            }

            TelegramUser telegramUser = student.getTelegramUser();

            if (telegramUser == null) {
                log.warn("student {} has no telegram user", student.getName());
                return;
            }

            long chatId = telegramUser.getChatId();

            bot.execute(SendMessage.builder()
                    .text(String.format("<b>Открыта запись на практику:</b> \n<b>Поток:</b> %s\n<b>Начало:</b> %tT\n<b>Преподаватели:</b> %s",
                            flow.getName(), practice.getBegin(), practice.getTeachers()))
                    .chatId(chatId)
                    .replyMarkup(Inline.getResizableKeyboard(List.of(
                            new Inline.Button("Записаться", CallbackBundle.fromData("join"))), 2))
                    .parseMode(ParseMode.HTML).build());


            bot.createStepSession(StepSessionConfiguration.create()
                            .saveUsersMessages(false).saveBotsMessages(false)
                            .execute((s, u) -> {
                                s.send("*Вы успешно записались!*\n*Выберите желаемое время сдачи:*",
                                        Inline.getResizableKeyboard(List.of(
                                                new Inline.Button("Начало", CallbackBundle.fromData("1")),
                                                new Inline.Button("Середина", CallbackBundle.fromData("2")),
                                                new Inline.Button("Конец", CallbackBundle.fromData("3"))
                                        ), 1));
                            })
                            .execute((s, u) -> {
                                if (!u.hasCallbackQuery()) {
                                    s.send("*Вы успешно записались!*\n*Выберите желаемое время сдачи:* `используйте кнопки`",
                                            Inline.getResizableKeyboard(List.of(
                                                    new Inline.Button("[1] Начало", CallbackBundle.fromData("1")),
                                                    new Inline.Button("[2] Середина", CallbackBundle.fromData("2")),
                                                    new Inline.Button("[3] Конец", CallbackBundle.fromData("3"))
                                            ), 1));
                                    s.rollback();
                                    return;
                                }

                                CallbackBundle callbackBundle = CallbackBundle.fromCallback(u.getText());

                                long selected = Long.parseLong(callbackBundle.data().getFirst());
                                QueueItem queueItem = queueItemService.save(QueueItem.builder()
                                        .person(student)
                                        .position(selected * 1000)
                                        .queue(practice.getQueue())
                                        .build());

                                s.append(" [" + callbackBundle.data().getFirst() + "]\n*Ожидайте формирования очереди.*");
                            }),
                    telegramUser.getChatId());
        });
    }
}

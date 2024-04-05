package com.serezka.telegram.updater;

import com.serezka.database.model.university.*;
import com.serezka.database.service.telegram.TelegramUserService;
import com.serezka.database.service.university.*;
import com.serezka.telegram.bot.Bot;
import com.serezka.telegram.session.menu.Page;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Log4j2
public class PracticeUpdater extends Updater {
    PracticeService practiceService;
    TelegramUserService telegramUserService;
    QueueService queueService;
    StudentService studentService;
    FlowService flowService;
    QueueItemService queueItemService;

    Bot bot;

    public PracticeUpdater(PracticeService practiceService, TelegramUserService telegramUserService, QueueService queueService, StudentService studentService, FlowService flowService, QueueItemService queueItemService,
                           Bot bot) {
        super(0, 1, TimeUnit.MINUTES);

        this.practiceService = practiceService;
        this.telegramUserService = telegramUserService;
        this.queueService = queueService;
        this.studentService = studentService;
        this.flowService = flowService;
        this.queueItemService = queueItemService;
        this.bot = bot;
    }

    @Override
    public void update() {
        try {
            ZonedDateTime now = ZonedDateTime.now();
            ZonedDateTime future = now.plusMinutes(10);

            List<Practice> practices = practiceService.findPracticesByTimeRange(now, future);

            log.info("founded {} practices for {}-{}", practices.size(), now, future);

            practices.forEach(this::handlePractice);
        } catch (Exception ex) {
            log.warn(ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void handlePractice(Practice practice) {
        log.info("handling {}", practice.toString());

        // get params
        final Flow flow = practice.getFlow();
        final List<Teacher> teachers = practice.getTeachers();

        // check optionalQueue
        Optional<Queue> optionalQueue = queueService.findByPractice(practice);
        Queue queue;

        queue = optionalQueue.orElseGet(() -> queueService.save(Queue.builder()
                .practice(practice)
                .state(Queue.State.REGISTRATION_OPEN)
                .build()));

        // send info to students
        flow.getStudents()
                .forEach(student -> {
                    StringBuilder text = new StringBuilder();
                    text.append("Привет! Началась запись на практику *").append(flow.getName()).append("*\n");
                    teachers.forEach(teacher -> text.append("*").append(teacher.getName()).append("*\n"));
                    text.append("Начало практики: ").append(practice.getBegin()).append("\n");

                    bot.createMenuSession(
                            (session, callback) -> new Page(text.toString())
                                    .addButtonWithLink("Записаться", "register"),
                            Map.of("register", (session, callback) -> {
//                                QueueItem queueItem = queueItemService.save(QueueItem.builder()
//                                        .student(student)
//                                        .build());
//
//                                queue.getItems().add(queueItem);
//                                queueService.save(queue);

                                        return new Page("Вы успешно записались на практику!")
                                                .addButtonWithLink("Прикрепить отчет", "attach");
                                    },
                                    "attach", (session, callback) -> {
                                        return new Page("Прикрепите отчет")
                                                .addButtonWithLink("Отправить", "send");
                                    }),
                            student.getTelegramUser().getChatId()
                    );
                });

    }
}

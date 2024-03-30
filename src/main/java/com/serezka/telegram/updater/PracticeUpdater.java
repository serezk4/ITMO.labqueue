package com.serezka.telegram.updater;

import com.serezka.database.model.university.Flow;
import com.serezka.database.model.university.Practice;
import com.serezka.database.model.university.Queue;
import com.serezka.database.model.university.Teacher;
import com.serezka.database.service.telegram.TelegramUserService;
import com.serezka.database.service.university.FlowService;
import com.serezka.database.service.university.PracticeService;
import com.serezka.database.service.university.QueueService;
import com.serezka.database.service.university.StudentService;
import com.serezka.telegram.bot.Bot;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;
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

    Bot bot;

    public PracticeUpdater(PracticeService practiceService, TelegramUserService telegramUserService, QueueService queueService, StudentService studentService, FlowService flowService,
                           Bot bot) {
        super(0, 1, TimeUnit.MINUTES);

        this.practiceService = practiceService;
        this.telegramUserService = telegramUserService;
        this.queueService = queueService;
        this.studentService = studentService;
        this.flowService = flowService;
        this.bot = bot;
    }

    @Scheduled(fixedDelay = 30, timeUnit = TimeUnit.SECONDS)
    @Async
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
        }
    }

    private void handlePractice(Practice practice) {
        log.info("handling {}", practice.toString());

        // get params
        final Flow flow = practice.getFlow();
        final List<Teacher> teachers = practice.getTeachers();

        // check queue
        Optional<Queue> queue = queueService.findByPractice(practice);

        if (queue.isEmpty()) {
            queueService.save(Queue.builder()
                    .practice(practice)
                    .state(Queue.State.REGISTRATION_OPEN)
                    .build());

            return;
        }

        // todo create menu
    }
}

package com.serezka.telegram.command.list.teacher;

import com.serezka.database.model.telegram.TelegramUser;
import com.serezka.database.model.university.Person;
import com.serezka.database.model.university.Practice;
import com.serezka.database.service.university.FlowService;
import com.serezka.database.service.university.PersonService;
import com.serezka.database.service.university.PracticeService;
import com.serezka.telegram.bot.Bot;
import com.serezka.telegram.command.Command;
import com.serezka.telegram.session.menu.Page;
import com.serezka.telegram.session.menu.PageGenerator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ManageFlow extends Command {
    FlowService flowService;
    PersonService personService;
    PracticeService practiceService;

    public ManageFlow(FlowService flowService, PersonService personService, PracticeService practiceService) {
        super(List.of("/manage"), "управление потоками", TelegramUser.Role.TEACHER);

        this.flowService = flowService;
        this.personService = personService;
        this.practiceService = practiceService;
    }

    @Override
    public void execute(Bot bot, Update update) {
        Optional<Person> optionalPerson = personService.findByTelegramUser(update.getTelegramUser());
        if (optionalPerson.isEmpty()) {
            bot.execute(SendMessage.builder()
                    .text("*Вы не зарегистрированы в системе*\nиспользуйте /register")
                    .chatId(update)
                    .build());
            return;
        }

        final Person person = optionalPerson.get();

        Map<String, PageGenerator> pages = new HashMap<>();

        PageGenerator root = (menuSession, callbackBundle) -> {
            Page page = new Page("*Управление потоками*");

            page.addButtonWithLink("-", "this");
            page.addButtonWithLink("Закрыть", "close");
            page.addButtonWithLink("-", "this");

            page.setRowSize(3);

            flowService.findAllByPeopleContaining(person)
                    .forEach(flow -> {
                        page.addButtonWithLink(flow.getName(), flow.getName());

                        pages.put(flow.getName(), (m, c) -> new Page(String.format("*Поток* %s\n > *ID:* %d\n > *Ключ:* %s\n > *Кол-во людей:* %d", flow.getName(), flow.getId(), flow.getSecret(), flow.getPeople().size()))
                                .addButton("Студенты", "", "students_" + flow.getId())
                                .addButton("Практики", "", "practices_" + flow.getId())
                                .addButtonWithLink("<", "root")
                                .setRowSize(1));

                        pages.put("practices_" + flow.getId(), (m, c) -> {
                            List<Practice> practices = practiceService.findAllByFlow(flow);

                            Page practicesPage = new Page(String.format("*Практики потока* %s%n%nТекущие даты:%n*%s*%n%nДля добавления - /practice", flow.getName(),
                                    practices.stream().map(Practice::getBegin).map(Object::toString).reduce((a, b) -> a + "\n" + b).orElse("не указаны")))
                                    .addButtonWithLink("<", flow.getName())
                                    .setRowSize(1);

                            return practicesPage;
                        });

                        pages.put("students_" + flow.getId(), (m, c) -> {
                            Page studentsPage = new Page("Студенты потока " + flow.getName());
                            flow.getPeople().forEach(student -> {
                                studentsPage.addButton(student.getName(), "", "student_" + student.getId());

                                pages.put("student_" + student.getId(), (ms, cq) -> {
                                    if (cq.data().contains("delete")) {
                                        flow.getPeople().remove(student);
                                        flowService.save(flow);

                                        return new Page("Студент " + student.getName() + " удален")
                                                .addButtonWithLink("<", flow.getName());
                                    }
                                    return new Page(String.format("*Студент*\n > *Имя:* %s\n > *ISU:* %s", student.getName(), student.getIsuId()))
                                            .addButton("Удалить", "delete", String.valueOf(student.getId()))
                                            .addButton("<", "", "students_" + flow.getId())
                                            .setRowSize(1);
                                });
                            });
                            studentsPage.addButtonWithLink("<", flow.getName());
                            studentsPage.setRowSize(1);
                            return studentsPage;
                        });
                    });

            return page;
        };

        bot.createMenuSession(root, pages, update.getChatId());
    }
}

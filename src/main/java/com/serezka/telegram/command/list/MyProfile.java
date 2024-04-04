package com.serezka.telegram.command.list;

import com.serezka.database.model.telegram.TelegramUser;
import com.serezka.database.model.university.Student;
import com.serezka.database.model.university.Teacher;
import com.serezka.database.service.university.FlowService;
import com.serezka.database.service.university.PracticeService;
import com.serezka.database.service.university.StudentService;
import com.serezka.database.service.university.TeacherService;
import com.serezka.telegram.bot.Bot;
import com.serezka.telegram.command.Command;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Optional;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MyProfile extends Command {
    StudentService studentService;
    TeacherService teacherService;
    PracticeService practiceService;

    public MyProfile(StudentService studentService, TeacherService teacherService, PracticeService practiceService) {
        super(List.of("/me"), "посмотреть профиль", TelegramUser.Role.MIN);

        this.studentService = studentService;
        this.teacherService = teacherService;
        this.practiceService = practiceService;
    }

    @Override
    public void execute(Bot bot, Update update) {
        TelegramUser user = update.getTelegramUser();

        Optional<Student> student = studentService.findByTelegramUser(user);
        Optional<Teacher> teacher = teacherService.findByTelegramUser(user);

        StringBuilder text = new StringBuilder(String.format("""
                telegram:
                > <b>имя</b>: %s
                > <b>роль</b>: %s
                                
                """, user.getUsername(), user.getRole()));

        student.ifPresent(value -> text.append(String.format("""
                student:
                > <b>ФИО</b>: %s
                > <b>ISU ID</b>: %s
                                
                """, value.getName(), value.getIsuId())));

        teacher.ifPresent(value -> text.append(String.format("""
                teacher:
                > <b>ФИО</b>: %s
                > <b>кол-во практик</b>: %d
                                
                """, value.getName(), practiceService.findAllByTeacher(value).size())));


        bot.send(SendMessage.builder()
                .text(text.toString())
                .parseMode(ParseMode.HTML)
                .chatId(update)
                .build());
    }
}

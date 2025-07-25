package ru.yandex.practicum.catsgram.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> findAll() {
        return users.values();
    }

    public Optional<User> findUserById(long authorId) {
        return Optional.ofNullable(users.get(authorId));
    }

    public User create(User user) {
        // проверяем выполнение необходимых условий
        log.info("вызван метод create");
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }

        boolean isMailExist = users.values()
                .stream()
                .map(User::getEmail)
                .anyMatch(mail -> mail.equals(user.getEmail()));
        if (isMailExist) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
        // формируем дополнительные данные
        user.setId(getNextId());
        user.setRegistrationDate(Instant.now());
        // сохраняем новую публикацию в памяти приложения
        users.put(user.getId(), user);
        return user;
    }

    public User update(User newUser) {
        log.info("вызван метод update");
        // проверяем необходимые условия
        if (newUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());

            boolean isMailExist = users.values()
                    .stream()
                    .map(User::getEmail)
                    .anyMatch(mail -> mail.equals(newUser.getEmail()));
            if (isMailExist) {
                throw new DuplicatedDataException("Этот имейл уже используется");
            }

            // если пользователь найден и все условия соблюдены, обновляем его содержимое
            if (newUser.getUsername() != null) oldUser.setUsername(newUser.getUsername());
            if (newUser.getPassword() != null) oldUser.setPassword(newUser.getPassword());
            if (newUser.getEmail() != null) oldUser.setEmail(newUser.getEmail());
            return oldUser;
        }
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    // вспомогательный метод для генерации идентификатора нового пользователя
    private long getNextId() {

        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        log.info("присвоено id = " + (currentMaxId+1));
        return ++currentMaxId;
    }

}

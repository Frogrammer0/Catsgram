package ru.yandex.practicum.catsgram.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.controller.SortOrder;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

// Указываем, что класс PostService - является бином и его
// нужно добавить в контекст приложения
@Slf4j
@Service
public class PostService {
    private final Map<Long, Post> posts = new HashMap<>();
    private final UserService userService;



    @Autowired
    public PostService(UserService userService) {
        this.userService = userService;
        User user = new User("name", "email@email.ru", "pass");
        userService.create(user);
        Post post1 = new Post(1, "11");
        create(post1);
        Post post2 = new Post(1, "12");
        create(post2);
        Post post3 = new Post(1, "13");
        create(post3);
        Post post4 = new Post(1, "14");
        create(post4);
        Post post5 = new Post(1, "15");
        create(post5);
        Post post6 = new Post(1, "16");
        create(post6);
        Post post7 = new Post(1, "17");
        create(post7);
        Post post8 = new Post(1, "18");
        create(post8);
        Post post9 = new Post(1, "19");
        create(post9);
        Post post10 = new Post(1, "110");
        create(post10);
        Post post11 = new Post(1, "111");
        create(post11);
    }

    public Collection<Post> findAll(int from, int size, SortOrder sort) {
        log.info("вызван метод findAll");
        if (sort == SortOrder.ASCENDING) {
            return posts.values().stream()
                    .sorted(Comparator.comparing(Post::getPostDate))
                    .skip(from)
                    .limit(size)
                    .collect(Collectors.toList());
        } else {
            return posts.values().stream()
                    .sorted(Comparator.comparing(Post::getPostDate).reversed() )
                    .skip(from)
                    .limit(size)
                    .collect(Collectors.toList());
        }


    }

    public Optional<Post> findById(long id) {
        return Optional.ofNullable(posts.get(id));
    }

    public Post create(Post post) {
        log.info("вызван метод create Post");
        if (post.getDescription() == null || post.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }
        if (userService.findUserById(post.getAuthorId()).isEmpty()) {
            throw new ConditionsNotMetException("Автор с id = " + post.getAuthorId() + " не найден");
        }

        post.setId(getNextId());
        post.setPostDate(Instant.now());
        posts.put(post.getId(), post);
        return post;
    }

    public Post update(Post newPost) {
        log.info("вызван метод updatePost");
        if (newPost.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (posts.containsKey(newPost.getId())) {
            Post oldPost = posts.get(newPost.getId());
            if (newPost.getDescription() == null || newPost.getDescription().isBlank()) {
                throw new ConditionsNotMetException("Описание не может быть пустым");
            }
            oldPost.setDescription(newPost.getDescription());
            return oldPost;
        }
        throw new NotFoundException("Пост с id = " + newPost.getId() + " не найден");
    }

    private long getNextId() {
        log.info("вызван метод присвоения id для поста");
        long currentMaxId = posts.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
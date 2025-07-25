package ru.yandex.practicum.catsgram.model;

import lombok.*;

import java.time.Instant;

@Data
@Setter
@Getter
@ToString
@EqualsAndHashCode(of = "id")
public class Post {

    Long id;
    long authorId;
    String description;
    Instant postDate;

    public Post(long authorId, String description) {
        this.authorId = authorId;
        this.description = description;
    }
}

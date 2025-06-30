package ru.yandex.practicum.catsgram.model;

import lombok.*;

@Data
@Setter
@Getter
@ToString
@EqualsAndHashCode(of = "id")
public class Image {

    Long id;
    long postId;
    String originalFileName;
    String filePath;

}

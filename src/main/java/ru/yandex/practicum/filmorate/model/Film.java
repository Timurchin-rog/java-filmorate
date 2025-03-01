package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {
    Long id;
    String name;
    String description;
    LocalDate releaseDate;
    Integer duration;
    Set<Long> likes;
    int countLikes;

    public Film(String name, String description, LocalDate releaseDate, Integer duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.likes = new HashSet<>();
    }

    public void addLikedUser(long likedUser) {
        likes.add(likedUser);
    }

    public void removeLikedUser(long likedUser) {
        likes.remove(likedUser);
    }

    public void increaseCountLikes() {
        countLikes++;
    }

    public void reduceCountLikes() {
        countLikes--;
    }
}

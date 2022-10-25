package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Friend {
    private int userId;
    private int friendId;
    private boolean isCross;

    public Friend(int friendId) {
        this.userId = friendId;
    }
}

package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class User {

    private int id;
    @Email
    private String email;
    @Pattern(regexp = "^\\S*$")
    private String login;
    private String name;
    @Past
    private LocalDate birthday;
    private Set<Integer> friends = new HashSet<>();

    public void addFriend(int userId){
        this.friends.add(userId);
    }

    public void removeFriend(int userId){
        this.friends.remove(userId);
    }

    public List<Integer> getCommonFriendList(User friend) {
        return friend.getFriends().stream()
                .filter(friends::contains)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}

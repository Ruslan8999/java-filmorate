package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private int id;
    @Email
    private String email;
    @Pattern(regexp = "^\\S*$")
    private String login;
    private String name;
    @Past
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate birthday;

    //private Set<Integer> friends = new HashSet<>();
    private Set<User> friends = new HashSet<>();

    public User(Integer id, @Valid String email, @Valid String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name.isEmpty() || name.isBlank() ? login : name;
        this.birthday = birthday;
        this.friends = new HashSet<>();
    }

    public void addFriend(User user) {
        this.friends.add(user);
    }

    public void removeFriend(int userId){
        this.friends.remove(userId);
    }
}

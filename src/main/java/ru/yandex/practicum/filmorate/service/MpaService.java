package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.MpaDao;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaDao mpaDao;

    public List<Mpa> getAll(){
        return mpaDao.getAll();
    }

    public Optional<Mpa> getById(int id){
        return mpaDao.getById(id);
    }
}

package com.enigmaschool.enigmaschool3springjpa.Service;

import java.util.List;
import java.util.Optional;

public interface ISchoolService<T> {
    List<T> getAll();
    T create(T t);
    List<T> createBulk(List<T> t);
    Optional<T> findById(Integer id);

    void update(T t , Integer id);

    void delete(Integer id);
}

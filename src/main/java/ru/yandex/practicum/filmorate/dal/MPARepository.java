package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;
import java.util.Optional;

@Repository
public class MPARepository extends BaseRepository<MPA> {
    private static final String FIND_All_RATING = "SELECT * FROM ratings";
    private static final String FIND_RATING_BY_ID = "SELECT * FROM ratings WHERE id = ?";
    private static final String FIND_RATING_BY_NAME = "SELECT * FROM ratings WHERE name = ?";

    public MPARepository(JdbcTemplate jdbc, RowMapper<MPA> mapper) {
        super(jdbc, mapper);
    }

    public List<MPA> getAllMPA() {
        return findMany(FIND_All_RATING);
    }

    public MPA getMpaById(int mpaId) {
        Optional<MPA> mpaOpt = findOne(FIND_RATING_BY_ID, mpaId);
        if (mpaOpt.isEmpty())
            throw new NotFoundException(String.format("Возрастной рейтинг id = %d не найден", mpaId));
        return mpaOpt.get();
    }

    public MPA getMpaByName(String mpaName) {
        Optional<MPA> mpaOpt = findOne(FIND_RATING_BY_NAME, mpaName);
        if (mpaOpt.isEmpty())
            throw new NotFoundException(String.format("Возрастной рейтинг name = %s не найден", mpaName));
        return mpaOpt.get();
    }

    public void isExistMpa(int mpaId) {
        Optional<MPA> mpaOpt = findOne(FIND_RATING_BY_ID, mpaId);
        if (mpaOpt.isEmpty())
            throw new NotFoundException(String.format("Возрастной рейтинг id = %d не найден", mpaId));
    }
}

package ru.innopolis.dao;

import org.springframework.jdbc.core.RowMapper;
import ru.innopolis.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(final ResultSet rs, final int rowNum) throws SQLException {
        final User user = new User();

        user.setId(rs.getInt("ID"));
        user.setFirstName(rs.getString("FIRST_NAME"));
        user.setLastName(rs.getString("LAST_NAME"));
        user.setAddress(rs.getString("ADDRESS"));

        return user;
    }
}
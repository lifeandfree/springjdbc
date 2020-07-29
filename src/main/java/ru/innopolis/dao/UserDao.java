package ru.innopolis.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.innopolis.model.User;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserDao {

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert simpleJdbcInsert;

    private final SimpleJdbcCall simpleJdbcCall;

    @Autowired
    public UserDao(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                   SimpleJdbcInsert simpleJdbcInsert, SimpleJdbcCall simpleJdbcCall) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.simpleJdbcInsert = simpleJdbcInsert;
        this.simpleJdbcInsert.withTableName("users");
        this.simpleJdbcCall = simpleJdbcCall;
    }

    public int getCountOfUsers() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
    }

    public User getUser(final int id) {
        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE id = ?", new Object[]{id}, new UserRowMapper());
    }

    public List<User> getAllUsers() {
        return jdbcTemplate.query("SELECT * FROM users", new UserRowMapper());
    }

    public int addUser(final int id) {
        return jdbcTemplate.update("INSERT INTO users VALUES (?, ?, ?, ?)", id, "Bill", "Gates", "USA");
    }

    public int addUserUsingSimpleJdbcInsert(final User emp) {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", emp.getId());
        parameters.put("first_name", emp.getFirstName());
        parameters.put("last_name", emp.getLastName());
        parameters.put("address", emp.getAddress());

        return simpleJdbcInsert.execute(parameters);
    }


    public void addUserUsingExecuteMethod() {
        jdbcTemplate.execute("INSERT INTO users VALUES (6, 'Bill', 'Gates', 'USA')");
    }

    public String getUserUsingMapSqlParameterSource() {
        final SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", 1);

        return namedParameterJdbcTemplate
                .queryForObject("SELECT first_name FROM users WHERE id = :id", namedParameters, String.class);
    }

    public int getUserUsingBeanPropertySqlParameterSource() {
        final User user = new User();
        user.setFirstName("James");

        final String SELECT_BY_FIRSTNAME = "SELECT COUNT(*) FROM users WHERE first_name = :firstName";

        final SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(user);

        return namedParameterJdbcTemplate.queryForObject(SELECT_BY_FIRSTNAME, namedParameters, Integer.class);
    }

    public int[] batchUpdateUsingJDBCTemplate(final List<User> users) {
        return jdbcTemplate.batchUpdate("INSERT INTO users VALUES (?, ?, ?, ?)", new BatchPreparedStatementSetter() {

            @Override
            public void setValues(final PreparedStatement ps, final int i) throws SQLException {
                ps.setInt(1, users.get(i).getId());
                ps.setString(2, users.get(i).getFirstName());
                ps.setString(3, users.get(i).getLastName());
                ps.setString(4, users.get(i).getAddress());
            }

            @Override
            public int getBatchSize() {
                return 3;
            }
        });
    }

    public int[] batchUpdateUsingNamedParameterJDBCTemplate(final List<User> users) {
        final SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(users.toArray());
        return namedParameterJdbcTemplate.batchUpdate("INSERT INTO users VALUES (:id, :firstName, :lastName, :address)",
                batch);
    }

    public User getUserUsingSimpleJdbcCall(int id) {
        SqlParameterSource in = new MapSqlParameterSource().addValue("in_id", id);
        simpleJdbcCall.withProcedureName("READ_USER");
        Map<String, Object> out = simpleJdbcCall.execute(in);
        User emp = new User();
        emp.setFirstName((String) out.get("first_name"));
        emp.setLastName((String) out.get("last_name"));

        return emp;
    }

    public void clearTable() {
        jdbcTemplate.execute("TRUNCATE TABLE users");
    }
}

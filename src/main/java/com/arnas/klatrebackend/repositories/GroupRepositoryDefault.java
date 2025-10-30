package com.arnas.klatrebackend.repositories;

import com.arnas.klatrebackend.interfaces.repositories.GroupRepositoryInterface;
import com.arnas.klatrebackend.dataclasses.AddGroupRequest;
import com.arnas.klatrebackend.dataclasses.Group;
import com.arnas.klatrebackend.dataclasses.GroupUser;
import jakarta.annotation.Nullable;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.List;
import java.util.Objects;

@Primary
@Repository
public class GroupRepositoryDefault implements GroupRepositoryInterface {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    public GroupRepositoryDefault(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        System.out.println("GroupRepositoryDefault created");
    }

    @Override
    @NotNull
    public List<Group> getGroups(long userID) {
        var sql = "SELECT kg.id AS klatreID, kg.name AS klatreName, kg.personal AS personal, kg.description AS description, kg.uuid AS uuid, " +
                "kg.owner AS owner " +
                "FROM klatre_groups AS kg " +
                "INNER JOIN user_groups AS ug ON kg.id = ug.group_id " +
                "WHERE ug.user_id = :userID";
        return jdbcTemplate.query(
                sql,
                new MapSqlParameterSource().addValue("userID", userID),
                (ResultSet rs, int index) -> new Group(
                        rs.getLong("klatreID"),
                        rs.getLong("owner"),
                        rs.getString("klatreName"),
                        rs.getBoolean("personal"),
                        rs.getString("uuid"),
                        rs.getString("description")
                ));
    }

    @Override
    public long addGroup(@NonNull AddGroupRequest group) {
        var keyholder = new GeneratedKeyHolder();
        var sql = "INSERT INTO klatre_groups(name, personal, description, owner) VALUES (:name, :personal, :description, :owner)";
        var parameters = new MapSqlParameterSource()
                .addValue("name", group.name())
                .addValue("personal", group.personal())
                .addValue("description", group.description())
                .addValue("owner", group.owner());
        jdbcTemplate.update(sql, parameters, keyholder);
        return Objects.requireNonNull(keyholder.getKey()).longValue();
    }

    @Override
    public int deleteGroup(long groupId) {
        var sql = "DELETE FROM klatre_groups WHERE id = :groupId";
        var parameters = new MapSqlParameterSource().addValue("groupId", groupId);
        return jdbcTemplate.update(sql, parameters);
    }

    @Override
    public @NotNull List<GroupUser> getGroupUsers(long groupId) {
        var sql = "SELECT * FROM user_groups AS ug INNER JOIN users AS u ON ug.user_id = u.id WHERE group_id = :groupId";
        var parameters = new MapSqlParameterSource().addValue("groupId", groupId);
        return jdbcTemplate.query(sql, parameters,
                (ResultSet rs, int index) -> new GroupUser(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            Objects.equals(rs.getString("role"), "0"),
                            Objects.equals(rs.getString("role"), "1") || Objects.equals(rs.getString("role"), "0"),
                            groupId
                ));
    }

    @Override
    public @Nullable Group getGroupById(long id) {
        var sql = "SELECT * FROM klatre_groups WHERE id = :id";
        var parameters = new MapSqlParameterSource().addValue("id", id);
        var group = jdbcTemplate.query(sql, parameters, (ResultSet rs, int index) -> new Group(
                rs.getLong("id"),
                rs.getLong("owner"),
                rs.getString("name"),
                rs.getBoolean("personal"),
                rs.getString("description"),
                rs.getString("uuid")
        ));
        return group.getFirst();
    }

    @Nullable
    @Override
    public Group getGroupByUuid(@NonNull String groupUuid) {
        var sql = "SELECT * FROM klatre_groups WHERE uuid = :uuid";
        var parameters = new MapSqlParameterSource().addValue("uuid", groupUuid);
        var group = jdbcTemplate.query(sql, parameters, (ResultSet rs, int index) -> new Group(
                rs.getLong("id"),
                rs.getLong("owner"),
                rs.getString("name"),
                rs.getBoolean("personal"),
                rs.getString("description"),
                rs.getString("uuid")
        ));
        return group.getFirst();
    }

    @Override
    public int addUserToGroup(long userId, long groupId, int role) {
        var sql = "INSERT INTO user_groups(user_id, group_id, role) VALUES (:userId, :groupId, :role)";
        var parameters = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("groupId", groupId)
                .addValue("role", role);
        return jdbcTemplate.update(sql, parameters);
    }

    @Nullable
    @Override
    public Integer getUserGroupRole(long userId, long groupId) {
        var sql = "SELECT role FROM user_groups WHERE user_id = :userId AND group_id = :groupId";
        var parameters = new MapSqlParameterSource().addValue("userId", userId).addValue("groupId", groupId);
        var role = jdbcTemplate.query(sql, parameters, (ResultSet rs, int index) -> rs.getInt("role"));
        return role.getFirst();
    }

    @Override
    public int updateUserGroupRole(long userId, long groupId, int newRoleId) {
        var sql = "UPDATE user_groups SET role = :newRoleId WHERE user_id = :userId AND group_id = :groupId";
        var parameters = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("groupId", groupId)
                .addValue("newRoleId", newRoleId);
        return jdbcTemplate.update(sql, parameters);
    }

    @Override
    public int deleteUserFromGroup(long userId, long groupId) {
        return jdbcTemplate.update("DELETE FROM user_groups WHERE user_id = :userId AND group_id = :groupId",
                new MapSqlParameterSource().addValue("userId", userId).addValue("groupId", groupId));
    }
}

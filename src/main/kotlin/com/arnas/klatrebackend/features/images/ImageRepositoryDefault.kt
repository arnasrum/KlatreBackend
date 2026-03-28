package com.arnas.klatrebackend.features.images

import org.springframework.context.annotation.Primary
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository

@Primary
@Repository
class ImageRepositoryDefault(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) : ImageRepository {

    override fun getImageById(id: String): Image? {
        val sql = "SELECT * FROM images WHERE id = :id"
        val parameters = MapSqlParameterSource().addValue("id", id)
        return jdbcTemplate.query(sql, parameters) { rs, _ ->
            Image(
                id = rs.getString("id"),
                contentType = rs.getString("content_type"),
                fileSize = rs.getLong("file_size")
            )
        }.firstOrNull()
    }

    override fun deleteImage(id: String): Int {
        val sql = "DELETE FROM images WHERE id = :id"
        val parameters = MapSqlParameterSource().addValue("id", id)
        return jdbcTemplate.update(sql, parameters)
    }

    override fun storeImageMetaData(contentType: String, size: Long, userId: Long): String {
        val sql = "INSERT INTO images(user_id, content_type, file_size) VALUES (:userId, :contentType, :fileSize)"
        val keyHolder = GeneratedKeyHolder()
        val parameters = MapSqlParameterSource()
            .addValue("userId", userId)
            .addValue("contentType", contentType)
            .addValue("fileSize", size)
        jdbcTemplate.update(sql, parameters, keyHolder)
        return keyHolder.keys!!["id"].toString()
    }

    override fun getImageMetadata(id: String): Image? {
        val sql = "SELECT * FROM images WHERE id = :id"
        val parameters = MapSqlParameterSource().addValue("id", id)
        return jdbcTemplate.query(sql, parameters) { rs, _ ->
            Image(
                id = rs.getString("id"),
                contentType = rs.getString("content_type"),
                fileSize = rs.getLong("file_size")
            )
        }.firstOrNull()
    }
}


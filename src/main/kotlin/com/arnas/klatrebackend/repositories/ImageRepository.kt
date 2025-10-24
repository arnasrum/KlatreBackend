package com.arnas.klatrebackend.repositories

import com.arnas.klatrebackend.dataclasses.Image
import com.arnas.klatrebackend.interfaces.repositories.ImageRepositoryInterface
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.jdbc.support.KeyHolder
import org.springframework.stereotype.Repository

@Repository
class ImageRepository(
    private var jdbcTemplate: NamedParameterJdbcTemplate
): ImageRepositoryInterface {

    override fun getImageById(id: String): Image? {
        val result = jdbcTemplate.query("SELECT * FROM image WHERE id=:id",
            MapSqlParameterSource()
                .addValue("id", id),
            ) { rs, _ ->
                Image(
                    id = rs.getString("id"),
                    contentType = rs.getString("content_type"),
                    fileSize = rs.getLong("file_size"),
                )
            }
        if (result.isEmpty()) {
            return null
        }
        return result[0]
    }

    override fun deleteImage(id: String): Int {
        val rowsAffected = jdbcTemplate.update("DELETE FROM image WHERE id = :id",
            mapOf("id" to id)
        )
        return rowsAffected
    }

    override fun storeImageMetaData(
        contentType: String,
        size: Long,
        userId: Long,
    ): String {
        val keyHolder: KeyHolder = GeneratedKeyHolder()
        jdbcTemplate.update("INSERT INTO image(content_type, file_size, user_id) VALUES " +
                "(:content_type, :size, :user_id)",
            MapSqlParameterSource()
                .addValue("content_type", contentType)
                .addValue("size", size)
                .addValue("user_id", userId),
            keyHolder)
        return keyHolder.keys!!["id"].toString()
    }
    
    override fun getImageMetadata(id: String): Image? {
        val result = jdbcTemplate.query("SELECT * FROM image WHERE id=:id",
            MapSqlParameterSource()
                .addValue("id", id),
            ) { rs, _ ->
                Image(
                    id = rs.getString("id"),
                    contentType = rs.getString("content_type"),
                    fileSize = rs.getLong("file_size"),
                )
            }
        return result.firstOrNull()
    }

}
package com.arnas.klatrebackend.repository

import com.arnas.klatrebackend.dataclass.Image
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

    override fun getImageByBoulderId(boulderId: Long): Image? {
        val result = jdbcTemplate.query("SELECT * FROM image WHERE boulder_id=:boulderID",
            MapSqlParameterSource()
                .addValue("boulderID", boulderId),
            ) { rs, _ ->
                Image(
                    id = rs.getString("id"),
                    contentType = rs.getString("content_type"),
                    boulder = rs.getLong("boulder_id"),
                    fileSize = rs.getLong("file_size"),
                )
            }
        if (result.isEmpty()) {
            return null
        }
        return result[0]
    }

    override fun deleteImage(imageID: String): Int {
        val rowsAffected = jdbcTemplate.update("DELETE FROM image WHERE id = :imageID",
            mapOf("imageID" to imageID)
        )
        return rowsAffected
    }

    override fun storeImageMetaData(
        boulderId: Long,
        contentType: String,
        size: Long,
        userId: Long,
    ): String {
        val keyHolder: KeyHolder = GeneratedKeyHolder()
        jdbcTemplate.update("INSERT INTO image(boulder_id, content_type, file_size, user_id) VALUES " +
                "(:boulder_id, :content_type, :size, :user_id)",
            MapSqlParameterSource()
                .addValue("boulder_id", boulderId)
                .addValue("content_type", contentType)
                .addValue("size", size)
                .addValue("user_id", userId),
            keyHolder)
        return keyHolder.keys!!["id"].toString()
    }
    
    override fun getImageMetadata(imageId: String): Image? {
        val result = jdbcTemplate.query("SELECT * FROM image WHERE id=:imageID",
            MapSqlParameterSource()
                .addValue("imageID", imageId),
            ) { rs, _ ->
                Image(
                    id = rs.getString("id"),
                    contentType = rs.getString("content_type"),
                    boulder = rs.getLong("boulder_id"),
                    fileSize = rs.getLong("file_size"),
                )
            }
        return result.firstOrNull()
    }

    override fun getImageMetadataByBoulder(boulderId: Long): Image? {
        val result = jdbcTemplate.query(
            "SELECT * FROM image WHERE boulder_id=:boulderID",
            MapSqlParameterSource()
                .addValue("boulderID", boulderId),
        ) { rs, _ ->
            Image(
                id = rs.getString("id"),
                contentType = rs.getString("content_type"),
                boulder = rs.getLong("boulder_id"),
                fileSize = rs.getLong("file_size"),
            )
        }
        return result.firstOrNull()
    }
}
package com.arnas.klatrebackend.repository

import com.arnas.klatrebackend.dataclass.Image
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.jdbc.support.KeyHolder
import org.springframework.stereotype.Repository
import org.springframework.web.multipart.MultipartFile

@Repository
class ImageRepository(private var jdbcTemplate: NamedParameterJdbcTemplate) {

    fun getImageByID(boulderID: Long): Image? {
        val result = jdbcTemplate.query("SELECT * FROM image WHERE boulder_id=:boulderID",
            MapSqlParameterSource()
                .addValue("boulderID", boulderID),
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

    fun deleteImage(imageID: String) {
        jdbcTemplate.update("DELETE FROM image WHERE id = :imageID",
            mapOf("imageID" to imageID)
        )
    }

    fun deleteBoulderImage(boulderID: Long) {
        jdbcTemplate.update("DELETE FROM image WHERE boulder_id = :boulderID",
            mapOf("boulderID" to boulderID)
        )
    }

    open fun storeImageMetaData(
        boulderID: Long,
        contentType: String,
        size: Long,
        userID: Long,
    ): String {
        val keyHolder: KeyHolder = GeneratedKeyHolder()
        jdbcTemplate.update("INSERT INTO image(boulder_id, content_type, file_size, user_id) VALUES " +
                "(:boulder_id, :content_type, :size, :user_id)",
            MapSqlParameterSource()
                .addValue("boulder_id", boulderID)
                .addValue("content_type", contentType)
                .addValue("size", size)
                .addValue("user_id", userID),
            keyHolder)
        return keyHolder.keys!!["id"].toString()
    }
    
    open fun getImageMetaData(imageID: String): Image? {
        val result = jdbcTemplate.query("SELECT * FROM image WHERE id=:imageID",
            MapSqlParameterSource()
                .addValue("imageID", imageID),
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
    open fun getImageMetaDataByBoulder(boulderID: Long): Image? {
        val result = jdbcTemplate.query(
            "SELECT * FROM image WHERE boulder_id=:boulderID",
            MapSqlParameterSource()
                .addValue("boulderID", boulderID),
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
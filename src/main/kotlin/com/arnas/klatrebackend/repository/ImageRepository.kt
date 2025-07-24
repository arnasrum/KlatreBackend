package com.arnas.klatrebackend.repository

import com.arnas.klatrebackend.dataclass.Image
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.web.multipart.MultipartFile

@Repository
class ImageRepository(private var jdbcTemplate: NamedParameterJdbcTemplate) {

    fun storeImage(image: String, boulderID: Long) {
        jdbcTemplate.update("INSERT INTO image(boulderid, image_base64)" +
                "VALUES(:boulderid, :image)", MapSqlParameterSource()
                    .addValue("boulderid", boulderID)
                    .addValue("image", image)
        )
    }

    fun getImageByID(boulderID: Long): Image? {
        val result = jdbcTemplate.query("SELECT * FROM image WHERE boulderid=:boulderID",
            MapSqlParameterSource()
                .addValue("boulderID", boulderID),
            ) { rs, _ ->
                Image(rs.getLong("id"),
                    rs.getString("image_base64"),
                    rs.getLong("boulderid")
                )
            }
        if (result.isEmpty()) {
            return null
        }
        return result[0]
    }

    fun deleteImage(imageID: Long) {
        jdbcTemplate.update("DELETE FROM image WHERE id=:imageid",
            MapSqlParameterSource()
                .addValue("imageid", imageID)
        )
    }
}
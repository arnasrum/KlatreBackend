package com.arnas.klatrebackend.repositories;

import com.arnas.klatrebackend.dataclasses.Image;
import com.arnas.klatrebackend.interfaces.repositories.ImageRepositoryInterface;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Primary
@Repository
public class ImageRepositoryJava implements ImageRepositoryInterface {

    NamedParameterJdbcTemplate jdbcTemplate;

    public ImageRepositoryJava(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public @Nullable Image getImageById(@NotNull String id) {
        System.out.println("Getting image with id: " + id);
        return null;
    }

    @Override
    public int deleteImage(@NotNull String id) {
        System.out.println("Getting image with id: " + id);
        return 0;
    }

    @Override
    @NotNull
    public String storeImageMetaData(@NotNull String contentType, long size, long userId) {
        System.out.println("Getting image with id: " + size);
        return "";
    }

    @Override
    @Nullable
    public Image getImageMetadata(@NotNull String id) {
        System.out.println("Getting image with id: " + id);
        return null;
    }
}

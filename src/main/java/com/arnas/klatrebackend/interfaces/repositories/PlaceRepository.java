package com.arnas.klatrebackend.interfaces.repositories;

import com.arnas.klatrebackend.dataclasses.Place;
import com.arnas.klatrebackend.dataclasses.PlaceUpdateDTO;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface PlaceRepository {
    Place[] getPlacesByGroupId(long groupId);
    long addPlaceToGroup(long groupId, String name, @NonNull String description);
    Optional<Place> getPlaceById(long placeId);
    int updatePlace(long placeId, String name, String description, Long groupId, Long gradingSystem);
    int updatePlace(PlaceUpdateDTO placeUpdateDTO);
    int deletePlace(Long placeId);
}

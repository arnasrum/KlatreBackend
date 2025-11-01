package com.arnas.klatrebackend.interfaces.services;

import com.arnas.klatrebackend.dataclasses.Place;
import com.arnas.klatrebackend.dataclasses.PlaceWithGrades;
import com.arnas.klatrebackend.records.PlaceDTO;

import java.util.List;

public interface PlaceService {
    Place getPlaceById(long placeId);
    List<PlaceWithGrades> getPlacesByGroupId(long groupId, long userId);
    int updatePlace(long userId, PlaceDTO placeUpdateDTO);
    int updatePlaceGradingSystem(long userId, long placeId, long newGradingSystemId);
}

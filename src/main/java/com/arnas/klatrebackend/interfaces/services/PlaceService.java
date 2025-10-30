package com.arnas.KlatreBackend.interfaces.services;

import com.arnas.KlatreBackend.records.Place;
import com.arnas.KlatreBackend.records.PlaceDTO;
import com.arnas.KlatreBackend.records.PlaceWithGrades;

public interface PlaceService {
    Place getPlaceById(long placeId);
    PlaceWithGrades[] getPlacesByGroupId(long groupId, long userId);
    int updatePlace(long userId, PlaceDTO placeUpdateDTO);
    int updatePlaceGradingSystem(long userId, long placeId, long newGradingSystemId);
}

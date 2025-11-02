package com.arnas.klatrebackend.services;

import com.arnas.klatrebackend.annotation.RequireGroupAccess;
import com.arnas.klatrebackend.dataclasses.GradingSystemWithGrades;
import com.arnas.klatrebackend.dataclasses.Place;
import com.arnas.klatrebackend.dataclasses.PlaceUpdateDTO;
import com.arnas.klatrebackend.dataclasses.PlaceWithGrades;
import com.arnas.klatrebackend.interfaces.repositories.GradingSystemRepositoryInterface;
import com.arnas.klatrebackend.interfaces.repositories.PlaceRepositoryInterface;
import com.arnas.klatrebackend.interfaces.services.PlaceServiceInterface;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaceServiceDefault implements PlaceServiceInterface {

    final private PlaceRepositoryInterface placeRepository;
    final private GradingSystemRepositoryInterface gradingSystemRepository;

    @Autowired
    public PlaceServiceDefault(
            PlaceRepositoryInterface placeRepository,
            GradingSystemRepositoryInterface gradingSystemRepository
    ) {
        this.placeRepository = placeRepository;
        this.gradingSystemRepository = gradingSystemRepository;
    }


    @Override
    public @Nullable Place getPlaceById(long placeId) {
        return placeRepository.getPlaceById(placeId);
    }

    @Override
    @RequireGroupAccess
    public @NotNull List<PlaceWithGrades> getPlacesByGroupId(long groupId, long userId) {
        var places = placeRepository.getPlacesByGroupId(groupId);
        return places.stream().map((Place place) -> new PlaceWithGrades(
            place.getId(),
            place.getName(),
            place.getDescription(),
            place.getGroupId(),
            new GradingSystemWithGrades(
                place.getGradingSystemId(),
                gradingSystemRepository.getGradesBySystemId(place.getGradingSystemId())
            )
        )).toList();
    }

    @Override
    public void updatePlace(long userId, @NotNull PlaceUpdateDTO placeUpdateDTO) {
        var place = placeRepository.getPlaceById(placeUpdateDTO.getPlaceId());
        if(place == null) throw new RuntimeException("Cannot find the place in the update request");
        var newPlace = new Place(
                place.getId(),
                placeUpdateDTO.getName(),
                placeUpdateDTO.getDescription(),
                place.getGroupId(),
                placeUpdateDTO.getGradingSystemId());

    }

    @Override
    public long updatePlaceGradingSystem(long placeId, long oldGradingSystemId, @Nullable Long newGradingSystemId) {
        return 0;
    }
}

package com.arnas.klatrebackend.features.places;

import com.arnas.klatrebackend.annotation.RequireGroupAccess;
import com.arnas.klatrebackend.features.gradesystems.Grade;
import com.arnas.klatrebackend.features.gradesystems.GradingSystemWithGrades;
import com.arnas.klatrebackend.features.routes.Route;
import com.arnas.klatrebackend.features.routes.RouteRepository;
import com.arnas.klatrebackend.features.gradesystems.GradeSystemRepositoryInterface;
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static java.lang.Math.abs;

@Primary
@Service
public class PlaceServiceDefault implements PlaceServiceInterface {

    private final PlaceRepositoryInterface placeRepository;
    private final GradeSystemRepositoryInterface gradingSystemRepository;
    private final RouteRepository routeRepository;

    @Autowired
    public PlaceServiceDefault(
            PlaceRepositoryInterface placeRepository,
            GradeSystemRepositoryInterface gradingSystemRepository,
            RouteRepository routeRepository) {
        this.placeRepository = placeRepository;
        this.gradingSystemRepository = gradingSystemRepository;
        this.routeRepository = routeRepository;
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
    @Transactional
    public void updatePlace(long userId, @NotNull PlaceUpdateDTO placeUpdateDTO) {
        var place = placeRepository.getPlaceById(placeUpdateDTO.getPlaceId());
        if(place == null) throw new RuntimeException("Cannot find the place in the update request");
        var newPlace = new Place(
                place.getId(),
                Optional.ofNullable(placeUpdateDTO.getName()).orElse(place.getName()),
                Optional.ofNullable(placeUpdateDTO.getDescription()).orElse(place.getDescription()),
                place.getGroupId(),
                updatePlaceGradingSystem(place.getId(), place.getGradingSystemId(), placeUpdateDTO.getGradingSystemId())
        );
        placeRepository.updatePlace(newPlace);
    }

    @Override
    public long updatePlaceGradingSystem(long placeId, long oldGradingSystemId, @Nullable Long newGradingSystemId) {
        if(newGradingSystemId == null || oldGradingSystemId == newGradingSystemId) return oldGradingSystemId;
        var oldGradingSystem = gradingSystemRepository.getGradesBySystemId(oldGradingSystemId);
        var newGradingSystem = gradingSystemRepository.getGradesBySystemId(newGradingSystemId);
        var routes = routeRepository.getRoutesByPlace(placeId);
        routes.forEach((route) -> {
            var oldGrade = oldGradingSystem.stream().filter((grade) -> grade.getId() == route.getGradeId()).findFirst().orElse(null);
            var newGradeId = findClosestGrade(oldGrade, newGradingSystem);
            if(newGradeId == null) throw new RuntimeException("Cannot find a grade that matches the old grade");
            var newRoute = new Route(
                    route.getId(),
                    route.getName(),
                    newGradeId,
                    route.getPlaceId(),
                    route.getDescription(),
                    route.getActive(),
                    route.getImageId()
            );
            routeRepository.updateRoute(newRoute);
        });
        return newGradingSystemId;
    }

    @Nullable
    private Long findClosestGrade(Grade grade, List<Grade> newReferenceGrades) {
        var closestGradeDifferencePair = newReferenceGrades.stream()
                .map((Grade referenceGrade) -> new Pair<Long, Integer>(referenceGrade.getId(), abs(referenceGrade.getNumericalValue() - grade.getNumericalValue())))
                .min(Comparator
                        .comparing((Pair<Long, Integer> pair) -> pair.getSecond())
                        .thenComparing(Pair::getFirst)
                );
        return closestGradeDifferencePair.map(Pair::getFirst).orElse(null);
    }
}

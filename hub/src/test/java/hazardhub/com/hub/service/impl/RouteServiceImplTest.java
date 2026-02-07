package hazardhub.com.hub.service.impl;

import hazardhub.com.hub.exception.ResourceNotFoundException;
import hazardhub.com.hub.model.dto.RouteDTO;
import hazardhub.com.hub.model.entity.Route;
import hazardhub.com.hub.repository.RouteRepository;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationUpdate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RouteServiceImplTest {

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private RouteServiceImpl routeService;

    @Test
    void selectRoute_WhenRouteExists_UsesSingleAtomicUpdateAndReturnsSelectedRoute() {
        String routeId = "65f50c31a683cb4e7d20f4a1";
        String tripId = "trip-001";

        Route target = Route.builder()
                .id(routeId)
                .tripId(tripId)
                .isSelected(false)
                .build();

        Route selected = Route.builder()
                .id(routeId)
                .tripId(tripId)
                .isSelected(true)
                .build();

        when(routeRepository.findById(routeId)).thenReturn(Optional.of(target), Optional.of(selected));

        RouteDTO result = routeService.selectRoute(routeId);

        assertEquals(routeId, result.getId());
        assertEquals(tripId, result.getTripId());
        assertTrue(Boolean.TRUE.equals(result.getIsSelected()));

        ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
        ArgumentCaptor<AggregationUpdate> updateCaptor = ArgumentCaptor.forClass(AggregationUpdate.class);

        InOrder inOrder = inOrder(routeRepository, mongoTemplate);
        inOrder.verify(routeRepository).findById(routeId);
        inOrder.verify(mongoTemplate).updateMulti(queryCaptor.capture(), updateCaptor.capture(), eq(Route.class));
        inOrder.verify(routeRepository).findById(routeId);

        Document query = queryCaptor.getValue().getQueryObject();
        assertEquals(tripId, query.getString("trip_id"));

        String updateJson = updateCaptor.getValue().getUpdateObject().toJson();
        assertTrue(updateJson.contains("is_selected"));
        assertTrue(updateJson.contains(routeId));

        verify(routeRepository, never()).findByTripId(anyString());
        verify(routeRepository, never()).saveAll(anyList());
    }

    @Test
    void selectRoute_WhenRouteDoesNotExist_ThrowsAndSkipsMongoUpdate() {
        String routeId = "65f50c31a683cb4e7d20f4a1";

        when(routeRepository.findById(routeId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> routeService.selectRoute(routeId));

        assertEquals("Route not found with id: " + routeId, exception.getMessage());
        verify(routeRepository).findById(routeId);
        verifyNoInteractions(mongoTemplate);
    }
}

package org.example.database.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.database.entity.Imu;
import org.example.database.entity.ImuCoordinate;
import org.example.database.repository.ImuCoordinateRepository;
import org.example.database.repository.ImuRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class ImuServiceTest {
    private ObjectMapper objectMapper = new ObjectMapper();
    private String json;

    @Mock
    private PicoService picoService;

    @Mock
    private MovesenseService movesenseService;

    @Mock
    private ImuRepository imuRepository;

    @Mock
    ImuCoordinateRepository imuCoordinateRepository;

    @InjectMocks
    private ImuService imuService;

    @BeforeAll
    public void setupJsonFile() throws IOException {
        File jsonFile = new File("src/test/java/org/example/mock/data/imu.json");
        json = objectMapper.readTree(jsonFile).toString();
    }

    @Test
    public void handleJsonSavesNewPico() throws IOException {
        when(picoService.existsInDatabase(any())).thenReturn(false);
        imuService.handleJson(json);
        verify(picoService, times(1)).save(any());
    }

    @Test
    public void handleJsonDoesNotSaveExistingPico() throws JsonProcessingException {
        when(picoService.existsInDatabase(any())).thenReturn(true);
        imuService.handleJson(json);
        verify(picoService, times(0)).save(any());
    }

    @Test
    public void handleJsonSavesNewMovesense() throws IOException {
        when(movesenseService.existsInDatabase(any())).thenReturn(false);
        imuService.handleJson(json);
        verify(movesenseService, times(1)).save(any());
    }

    @Test
    public void handleJsonDoesNotSaveExistingMovesense() throws JsonProcessingException {
        when(movesenseService.existsInDatabase(any())).thenReturn(true);
        imuService.handleJson(json);
        verify(movesenseService, times(0)).save(any());
    }

    @Test
    public void handleJsonSavesImuData() throws JsonProcessingException {
        imuService.handleJson(json);
        verify(imuRepository, times(1)).save(any());
    }

    @Test
    public void handleJsonSavesAllImuCoordinates() throws JsonProcessingException {
        imuService.handleJson(json);
        verify(imuCoordinateRepository, times(6)).save(any());
    }

    @Test
    public void findByEcgIdTest() throws JsonProcessingException {
        Imu imu = new Imu();
        ImuCoordinate coordinate1 = new ImuCoordinate();
        ImuCoordinate coordinate2 = new ImuCoordinate();

        List<ImuCoordinate> coordinates = new ArrayList<>();
        coordinates.add(coordinate1);
        coordinates.add(coordinate2);

        when(imuRepository.findById(1)).thenReturn(imu);
        when(imuCoordinateRepository.findByImuId(1)).thenReturn(coordinates);

        Imu foundImu = imuService.findImuById(1);
        assertEquals(foundImu, imu, "Incorrect IMU found");
        assertEquals(foundImu.getImuCoordinates(), coordinates, "Incorrect IMU coordinates found");
    }

    @Test
    public void findEcgByTimestampUtcBetweenTest() {
        Imu imu1 = new Imu();
        imu1.setId(1L);

        Imu imu2 = new Imu();
        imu2.setId(2L);

        List<Imu> imuList = new ArrayList<>();
        imuList.add(imu1);
        imuList.add(imu2);

        ImuCoordinate coordinate1 = new ImuCoordinate();
        ImuCoordinate coordinate2 = new ImuCoordinate();

        List<ImuCoordinate> coordinates = new ArrayList<>();
        coordinates.add(coordinate1);
        coordinates.add(coordinate2);

        when(imuRepository.findByTimestampUtcBetween(1L, 1000L)).thenReturn(imuList);
        when(imuCoordinateRepository.findByImuId(1L)).thenReturn(coordinates);
        when(imuCoordinateRepository.findByImuId(2L)).thenReturn(null);

        List<Imu> foundImuList = imuService.findImuByTimestampUtcBetween(1L, 1000L);
        assertEquals(foundImuList.get(0).getImuCoordinates(), coordinates, "Incorrect coordinates");
        assertNull(foundImuList.get(1).getImuCoordinates(), "Incorrect coordinates");
    }
}

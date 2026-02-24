package org.example.database.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.database.entity.Imu;
import org.example.database.entity.ImuCoordinate;
import org.example.database.repository.ImuCoordinateRepository;
import org.example.database.repository.ImuRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class ImuServiceTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private String json;

    @Mock private PicoService picoService;
    @Mock private MovesenseService movesenseService;
    @Mock private ImuRepository imuRepository;
    @Mock private ImuCoordinateRepository imuCoordinateRepository;

    private ImuService imuService;

    @BeforeAll
    public void setupJsonFile() throws IOException {
        File jsonFile = new File("src/test/java/org/example/mock/data/imu.json");
        json = objectMapper.readTree(jsonFile).toString();
    }

    @BeforeEach
    public void setupService() {
        imuService = new ImuService(picoService, movesenseService, imuRepository, imuCoordinateRepository);
    }

    @Test
    public void handleJsonSavesNewPico() throws Exception {
        when(picoService.existsInDatabase(any())).thenReturn(false);
        when(movesenseService.existsInDatabase(any())).thenReturn(true);

        imuService.handleJson(json);

        verify(picoService, times(1)).save(any());
    }

    @Test
    public void handleJsonDoesNotSaveExistingPico() throws Exception {
        when(picoService.existsInDatabase(any())).thenReturn(true);
        when(movesenseService.existsInDatabase(any())).thenReturn(true);

        imuService.handleJson(json);

        verify(picoService, never()).save(any());
    }

    @Test
    public void handleJsonSavesNewMovesense() throws Exception {
        when(picoService.existsInDatabase(any())).thenReturn(true);
        when(movesenseService.existsInDatabase(any())).thenReturn(false);

        imuService.handleJson(json);

        verify(movesenseService, times(1)).save(any());
    }

    @Test
    public void handleJsonDoesNotSaveExistingMovesense() throws Exception {
        when(picoService.existsInDatabase(any())).thenReturn(true);
        when(movesenseService.existsInDatabase(any())).thenReturn(true);

        imuService.handleJson(json);

        verify(movesenseService, never()).save(any());
    }

    @Test
    public void handleJsonSavesImuData() throws Exception {
        when(picoService.existsInDatabase(any())).thenReturn(true);
        when(movesenseService.existsInDatabase(any())).thenReturn(true);

        imuService.handleJson(json);

        verify(imuRepository, times(1)).save(any());
    }

    @Test
    public void handleJsonSavesAllImuCoordinates() throws Exception {
        when(picoService.existsInDatabase(any())).thenReturn(true);
        when(movesenseService.existsInDatabase(any())).thenReturn(true);

        imuService.handleJson(json);

        // force batch flush
        imuService.flushCoordinateBufferOnTimer();

        verify(imuCoordinateRepository, times(1)).saveAll(anyList());
        verify(imuCoordinateRepository, times(1)).flush();
        verify(imuCoordinateRepository, never()).save(any(ImuCoordinate.class));
    }

    @Test
    public void findByEcgIdTest() {
        Imu imu = new Imu();

        List<ImuCoordinate> coords = new ArrayList<>();
        coords.add(new ImuCoordinate());
        coords.add(new ImuCoordinate());

        when(imuRepository.findById(1L)).thenReturn(imu);
        when(imuCoordinateRepository.findByImuId(1L)).thenReturn(coords);

        Imu found = imuService.findImuById(1L);

        assertEquals(imu, found, "Incorrect IMU found");
        assertEquals(coords, found.getImuCoordinates(), "Incorrect IMU coordinates found");
    }

    @Test
    public void findEcgByTimestampUtcBetweenTest() {
        Imu imu1 = new Imu(); imu1.setId(1L);
        Imu imu2 = new Imu(); imu2.setId(2L);

        List<Imu> list = new ArrayList<>();
        list.add(imu1);
        list.add(imu2);

        List<ImuCoordinate> coords = new ArrayList<>();
        coords.add(new ImuCoordinate());
        coords.add(new ImuCoordinate());

        when(imuRepository.findByTimestampUtcBetween(1L, 1000L)).thenReturn(list);
        when(imuCoordinateRepository.findByImuId(1L)).thenReturn(coords);
        when(imuCoordinateRepository.findByImuId(2L)).thenReturn(null);

        List<Imu> found = imuService.findImuByTimestampUtcBetween(1L, 1000L);

        assertEquals(coords, found.get(0).getImuCoordinates());
        assertNull(found.get(1).getImuCoordinates());
    }
}
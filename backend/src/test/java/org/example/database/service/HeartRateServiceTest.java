package org.example.database.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.database.entity.HeartRate;
import org.example.database.entity.RrData;
import org.example.database.repository.HeartRateRepository;
import org.example.database.repository.RrDataRepository;
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
public class HeartRateServiceTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private String json;

    @Mock private PicoService picoService;
    @Mock private MovesenseService movesenseService;
    @Mock private HeartRateRepository heartRateRepository;
    @Mock private RrDataRepository rrDataRepository;

    private HeartRateService heartRateService;

    @BeforeAll
    public void setupJsonFile() throws IOException {
        File jsonFile = new File("src/test/java/org/example/mock/data/heart_rate.json");
        json = objectMapper.readTree(jsonFile).toString();
    }

    @BeforeEach
    public void setupService() {
        heartRateService = new HeartRateService(picoService, movesenseService, heartRateRepository, rrDataRepository);
    }

    @Test
    public void handleJsonSavesNewPico() throws Exception {
        when(picoService.existsInDatabase(any())).thenReturn(false);
        when(movesenseService.existsInDatabase(any())).thenReturn(true);

        heartRateService.handleJson(json);

        verify(picoService, times(1)).save(any());
    }

    @Test
    public void handleJsonDoesNotSaveExistingPico() throws Exception {
        when(picoService.existsInDatabase(any())).thenReturn(true);
        when(movesenseService.existsInDatabase(any())).thenReturn(true);

        heartRateService.handleJson(json);

        verify(picoService, never()).save(any());
    }

    @Test
    public void handleJsonSavesNewMovesense() throws Exception {
        when(picoService.existsInDatabase(any())).thenReturn(true);
        when(movesenseService.existsInDatabase(any())).thenReturn(false);

        heartRateService.handleJson(json);

        verify(movesenseService, times(1)).save(any());
    }

    @Test
    public void handleJsonDoesNotSaveExistingMovesense() throws Exception {
        when(picoService.existsInDatabase(any())).thenReturn(true);
        when(movesenseService.existsInDatabase(any())).thenReturn(true);

        heartRateService.handleJson(json);

        verify(movesenseService, never()).save(any());
    }

    @Test
    public void handleJsonSavesHeartRateData() throws Exception {
        when(picoService.existsInDatabase(any())).thenReturn(true);
        when(movesenseService.existsInDatabase(any())).thenReturn(true);

        heartRateService.handleJson(json);

        verify(heartRateRepository, times(1)).save(any());
    }

    @Test
    public void handleJsonSavesAllRrData() throws Exception {
        when(picoService.existsInDatabase(any())).thenReturn(true);
        when(movesenseService.existsInDatabase(any())).thenReturn(true);

        heartRateService.handleJson(json);
        heartRateService.flushRrBufferOnTimer();

        verify(rrDataRepository, times(1)).saveAll(anyList());
        verify(rrDataRepository, times(1)).flush();
        verify(rrDataRepository, never()).save(any(RrData.class));
    }

    @Test
    public void findByHeartRateIdTest() {
        HeartRate heartRate = new HeartRate();

        List<RrData> rrList = new ArrayList<>();
        rrList.add(new RrData());
        rrList.add(new RrData());

        when(heartRateRepository.findById(1L)).thenReturn(heartRate);
        when(rrDataRepository.findByHeartRateId(1L)).thenReturn(rrList);

        HeartRate found = heartRateService.findHeartRateById(1L);

        assertEquals(heartRate, found, "Incorrect heart rate object found");
        assertEquals(rrList, found.getRrData(), "Incorrect RR data found");
    }

    @Test
    public void findEcgByTimestampUtcBetweenTest() {
        HeartRate hr1 = new HeartRate(); hr1.setId(1L);
        HeartRate hr2 = new HeartRate(); hr2.setId(2L);

        List<HeartRate> list = new ArrayList<>();
        list.add(hr1);
        list.add(hr2);

        List<RrData> rrList = new ArrayList<>();
        rrList.add(new RrData());
        rrList.add(new RrData());

        when(heartRateRepository.findByTimestampUtcBetween(1L, 1000L)).thenReturn(list);
        when(rrDataRepository.findByHeartRateId(1L)).thenReturn(rrList);
        when(rrDataRepository.findByHeartRateId(2L)).thenReturn(null);

        List<HeartRate> found = heartRateService.findHeartRateByTimestampUtcBetween(1L, 1000L);

        assertEquals(rrList, found.get(0).getRrData());
        assertNull(found.get(1).getRrData());
    }
}
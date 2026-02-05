package org.example.database.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.database.entity.HeartRate;
import org.example.database.entity.RrData;
import org.example.database.repository.HeartRateRepository;
import org.example.database.repository.RrDataRepository;
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
public class HeartRateServiceTest {
    private ObjectMapper objectMapper = new ObjectMapper();
    private String json;

    @Mock
    private PicoService picoService;

    @Mock
    private MovesenseService movesenseService;

    @Mock
    private HeartRateRepository heartRateRepository;

    @Mock
    private RrDataRepository rrDataRepository;

    @InjectMocks
    private HeartRateService heartRateService;

    @BeforeAll
    public void setupJsonFile() throws IOException {
        File jsonFile = new File("src/test/java/org/example/mock/data/heart_rate.json");
        json = objectMapper.readTree(jsonFile).toString();
    }

    @Test
    public void handleJsonSavesNewPico() throws IOException {
        when(picoService.existsInDatabase(any())).thenReturn(false);
        heartRateService.handleJson(json);
        verify(picoService, times(1)).save(any());
    }

    @Test
    public void handleJsonDoesNotSaveExistingPico() throws JsonProcessingException {
        when(picoService.existsInDatabase(any())).thenReturn(true);
        heartRateService.handleJson(json);
        verify(picoService, times(0)).save(any());
    }

    @Test
    public void handleJsonSavesNewMovesense() throws IOException {
        when(movesenseService.existsInDatabase(any())).thenReturn(false);
        heartRateService.handleJson(json);
        verify(movesenseService, times(1)).save(any());
    }

    @Test
    public void handleJsonDoesNotSaveExistingMovesense() throws JsonProcessingException {
        when(movesenseService.existsInDatabase(any())).thenReturn(true);
        heartRateService.handleJson(json);
        verify(movesenseService, times(0)).save(any());
    }

    @Test
    public void handleJsonSavesHeartRateData() throws JsonProcessingException {
        heartRateService.handleJson(json);
        verify(heartRateRepository, times(1)).save(any());
    }

    @Test
    public void handleJsonSavesAllRrData() throws JsonProcessingException {
        heartRateService.handleJson(json);
        verify(heartRateRepository, times(1)).save(any());
    }

    @Test
    public void findByHeartRateIdTest() throws JsonProcessingException {
        HeartRate heartRate = new HeartRate();
        RrData data1 = new RrData();
        RrData data2 = new RrData();

        List<RrData> dataList = new ArrayList<>();
        dataList.add(data1);
        dataList.add(data2);

        when(heartRateRepository.findById(1)).thenReturn(heartRate);
        when(rrDataRepository.findByHeartRateId(1)).thenReturn(dataList);

        HeartRate foundHeartRate = heartRateService.findHeartRateById(1);
        assertEquals(foundHeartRate, heartRate, "Incorrect heart rate object found");
        assertEquals(foundHeartRate.getRrData(), dataList, "Incorrect RR data found");
    }

    @Test
    public void findEcgByTimestampUtcBetweenTest() {
        HeartRate heartRate1 = new HeartRate();
        heartRate1.setId(1L);

        HeartRate heartRate2 = new HeartRate();
        heartRate2.setId(2L);

        List<HeartRate> heartRates = new ArrayList<>();
        heartRates.add(heartRate1);
        heartRates.add(heartRate2);

        RrData data1 = new RrData();
        RrData data2 = new RrData();

        List<RrData> dataList = new ArrayList<>();
        dataList.add(data1);
        dataList.add(data2);

        when(heartRateRepository.findByTimestampUtcBetween(1L, 1000L)).thenReturn(heartRates);
        when(rrDataRepository.findByHeartRateId(1L)).thenReturn(dataList);
        when(rrDataRepository.findByHeartRateId(2L)).thenReturn(null);

        List<HeartRate> foundHeartRates = heartRateService.findHeartRateByTimestampUtcBetween(1L, 1000L);
        assertEquals(foundHeartRates.get(0).getRrData(), dataList, "Incorrect RR data");
        assertNull(foundHeartRates.get(1).getRrData(), "Incorrect RR data");
    }
}

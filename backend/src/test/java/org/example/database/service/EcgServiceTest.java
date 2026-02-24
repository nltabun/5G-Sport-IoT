package org.example.database.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.database.entity.Ecg;
import org.example.database.entity.EcgSample;
import org.example.database.repository.EcgRepository;
import org.example.database.repository.EcgSampleRepository;
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
public class EcgServiceTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private String json;

    @Mock private PicoService picoService;
    @Mock private MovesenseService movesenseService;
    @Mock private EcgRepository ecgRepository;
    @Mock private EcgSampleRepository ecgSampleRepository;

    private EcgService ecgService;

    @BeforeAll
    public void setupJsonFile() throws IOException {
        File jsonFile = new File("src/test/java/org/example/mock/data/ecg.json");
        json = objectMapper.readTree(jsonFile).toString();
    }

    @BeforeEach
    public void setupService() {
        ecgService = new EcgService(picoService, movesenseService, ecgRepository, ecgSampleRepository);
    }

    @Test
    public void handleJsonSavesNewPico() throws Exception {
        when(picoService.existsInDatabase(any())).thenReturn(false);
        when(movesenseService.existsInDatabase(any())).thenReturn(true);

        ecgService.handleJson(json);

        verify(picoService, times(1)).save(any());
    }

    @Test
    public void handleJsonDoesNotSaveExistingPico() throws Exception {
        when(picoService.existsInDatabase(any())).thenReturn(true);
        when(movesenseService.existsInDatabase(any())).thenReturn(true);

        ecgService.handleJson(json);

        verify(picoService, never()).save(any());
    }

    @Test
    public void handleJsonSavesNewMovesense() throws Exception {
        when(picoService.existsInDatabase(any())).thenReturn(true);
        when(movesenseService.existsInDatabase(any())).thenReturn(false);

        ecgService.handleJson(json);

        verify(movesenseService, times(1)).save(any());
    }

    @Test
    public void handleJsonDoesNotSaveExistingMovesense() throws Exception {
        when(picoService.existsInDatabase(any())).thenReturn(true);
        when(movesenseService.existsInDatabase(any())).thenReturn(true);

        ecgService.handleJson(json);

        verify(movesenseService, never()).save(any());
    }

    @Test
    public void handleJsonSavesEcgData() throws Exception {
        when(picoService.existsInDatabase(any())).thenReturn(true);
        when(movesenseService.existsInDatabase(any())).thenReturn(true);

        ecgService.handleJson(json);

        verify(ecgRepository, times(1)).save(any());
    }

    @Test
    public void handleJsonSavesAllEcgSamples() throws Exception {
        when(picoService.existsInDatabase(any())).thenReturn(true);
        when(movesenseService.existsInDatabase(any())).thenReturn(true);

        ecgService.handleJson(json);

        // force batch flush
        ecgService.flushSampleBufferOnTimer();

        verify(ecgSampleRepository, times(1)).saveAll(anyList());
        verify(ecgSampleRepository, times(1)).flush();
        verify(ecgSampleRepository, never()).save(any(EcgSample.class));
    }

    @Test
    public void findByEcgIdTest() {
        Ecg ecg = new Ecg();

        List<EcgSample> samples = new ArrayList<>();
        samples.add(new EcgSample());
        samples.add(new EcgSample());

        when(ecgRepository.findById(1L)).thenReturn(ecg);
        when(ecgSampleRepository.findByEcgId(1L)).thenReturn(samples);

        Ecg foundEcg = ecgService.findEcgById(1L);

        assertEquals(ecg, foundEcg);
        assertEquals(samples, foundEcg.getEcgSamples());
    }

    @Test
    public void findEcgByTimestampUtcBetweenTest() {
        Ecg ecg1 = new Ecg(); ecg1.setId(1L);
        Ecg ecg2 = new Ecg(); ecg2.setId(2L);

        List<Ecg> ecgList = new ArrayList<>();
        ecgList.add(ecg1);
        ecgList.add(ecg2);

        List<EcgSample> samples = new ArrayList<>();
        samples.add(new EcgSample());
        samples.add(new EcgSample());

        when(ecgRepository.findByTimestampUtcBetween(1L, 1000L)).thenReturn(ecgList);
        when(ecgSampleRepository.findByEcgId(1L)).thenReturn(samples);
        when(ecgSampleRepository.findByEcgId(2L)).thenReturn(null);

        List<Ecg> found = ecgService.findEcgByTimestampUtcBetween(1L, 1000L);

        assertEquals(samples, found.get(0).getEcgSamples());
        assertNull(found.get(1).getEcgSamples());
    }
}
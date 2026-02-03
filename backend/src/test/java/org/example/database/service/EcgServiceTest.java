package org.example.database.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.database.entity.Ecg;
import org.example.database.entity.EcgSample;
import org.example.database.repository.EcgRepository;
import org.example.database.repository.EcgSampleRepository;
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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class EcgServiceTest {
    private ObjectMapper objectMapper = new ObjectMapper();
    private String json;

    @Mock
    private PicoService picoService;

    @Mock
    private MovesenseService movesenseService;

    @Mock
    private EcgRepository ecgRepository;

    @Mock
    private EcgSampleRepository ecgSampleRepository;

    @InjectMocks
    private EcgService ecgService;

    @BeforeAll
    public void setupJsonFile() throws IOException {
        File jsonFile = new File("src/test/java/org/example/mock/data/ecg.json");
        json = objectMapper.readTree(jsonFile).toString();
    }

    @Test
    public void handleJsonSavesNewPico() throws IOException {
        when(picoService.existsInDatabase(any())).thenReturn(false);
        ecgService.handleJson(json);
        verify(picoService, times(1)).save(any());
    }

    @Test
    public void handleJsonDoesNotSaveExistingPico() throws JsonProcessingException {
        when(picoService.existsInDatabase(any())).thenReturn(true);
        ecgService.handleJson(json);
        verify(picoService, times(0)).save(any());
    }

    @Test
    public void handleJsonSavesNewMovesense() throws IOException {
        when(movesenseService.existsInDatabase(any())).thenReturn(false);
        ecgService.handleJson(json);
        verify(movesenseService, times(1)).save(any());
    }

    @Test
    public void handleJsonDoesNotSaveExistingMovesense() throws JsonProcessingException {
        when(movesenseService.existsInDatabase(any())).thenReturn(true);
        ecgService.handleJson(json);
        verify(movesenseService, times(0)).save(any());
    }

    @Test
    public void handleJsonSavesEcgData() throws JsonProcessingException {
        ecgService.handleJson(json);
        verify(ecgRepository, times(1)).save(any());
    }

    @Test
    public void handleJsonSavesAllEcgSamples() throws JsonProcessingException {
        ecgService.handleJson(json);
        verify(ecgSampleRepository, times(16)).save(any());
    }

    @Test
    public void findByEcgIdTest() throws JsonProcessingException {
        Ecg ecg = new Ecg();
        EcgSample sample1 = new EcgSample();
        EcgSample sample2 = new EcgSample();

        List<EcgSample> samples = new ArrayList<>();
        samples.add(sample1);
        samples.add(sample2);

        when(ecgRepository.findById(1)).thenReturn(ecg);
        when(ecgSampleRepository.findByEcgId(1)).thenReturn(samples);

        Ecg foundEcg = ecgService.findEcgById(1);
        assertEquals(foundEcg, ecg, "findEcgById did not return the correct Ecg object");
        assertEquals(foundEcg.getEcgSamples(), samples, "findEcgById did not return the correct EcgSample objects");
    }

    @Test
    public void findEcgByTimestampUtcBetweenTest() {
        Ecg ecg1 = new Ecg();
        ecg1.setId(1L);

        Ecg ecg2 = new Ecg();
        ecg2.setId(2L);

        List<Ecg> ecgList = new ArrayList<>();
        ecgList.add(ecg1);
        ecgList.add(ecg2);

        EcgSample sample1 = new EcgSample();
        EcgSample sample2 = new EcgSample();

        List<EcgSample> samples = new ArrayList<>();
        samples.add(sample1);
        samples.add(sample2);

        when(ecgRepository.findByTimestampUtcBetween(1, 1000)).thenReturn(ecgList);
        when(ecgSampleRepository.findByEcgId(1L)).thenReturn(samples);
        when(ecgSampleRepository.findByEcgId(2L)).thenReturn(null);

        List<Ecg> foundEcgList = ecgService.findEcgByTimestampUtcBetween(1, 1000);
        assertEquals(foundEcgList.get(0).getEcgSamples(), samples, "Incorrect EcgSamples");
        assertNull(foundEcgList.get(1).getEcgSamples(), "Incorrect EcgSamples");
    }
}

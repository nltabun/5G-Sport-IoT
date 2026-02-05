package org.example.database.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.database.repository.GnssRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class GnssServiceTest {
    private ObjectMapper objectMapper = new ObjectMapper();
    private String json;

    @Mock
    private PicoService picoService;

    @Mock
    private GnssRepository gnssRepository;

    @InjectMocks
    private GnssService gnssService;

    @BeforeAll
    public void setupJsonFile() throws IOException {
        File jsonFile = new File("src/test/java/org/example/mock/data/gnss.json");
        json = objectMapper.readTree(jsonFile).toString();
    }

    @Test
    public void handleJsonSavesNewPico() throws IOException {
        when(picoService.existsInDatabase(any())).thenReturn(false);
        gnssService.handleJson(json);
        verify(picoService, times(1)).save(any());
    }

    @Test
    public void handleJsonDoesNotSaveExistingPico() throws JsonProcessingException {
        when(picoService.existsInDatabase(any())).thenReturn(true);
        gnssService.handleJson(json);
        verify(picoService, times(0)).save(any());
    }

    @Test
    public void handleJsonSavesGnssData() throws JsonProcessingException {
        gnssService.handleJson(json);
        verify(gnssRepository, times(1)).save(any());
    }

    @Test
    public void findGnssByIdTest() {
        gnssService.findGnssById(1);
        verify(gnssRepository, times(1)).findById(1);
    }

    @Test
    public void findGnssByTimestampUtcBetweenTest() {
        gnssService.findGnssByTimestampUtcBetween(1L, 2L);
        verify(gnssRepository, times(1)).findByTimestampUtcBetween(1L, 2L);
    }
}

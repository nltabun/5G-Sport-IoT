package org.example.database.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.database.entity.Gnss;
import org.example.database.repository.GnssRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class GnssServiceTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private String json;

    @Mock
    private PicoService picoService;

    @Mock
    private GnssRepository gnssRepository;

    private GnssService gnssService;

    @BeforeAll
    public void setupJsonFile() throws IOException {
        File jsonFile = new File("src/test/java/org/example/mock/data/gnss.json");
        json = objectMapper.readTree(jsonFile).toString();
    }

    @BeforeEach
    public void setupService() {
        gnssService = new GnssService(picoService, gnssRepository);
    }

    @Test
    public void handleJsonSavesNewPico() throws IOException {
        when(picoService.existsInDatabase(any())).thenReturn(false);

        gnssService.handleJson(json);

        verify(picoService, times(1)).save(any());

        gnssService.flushOnTimer();

        verify(gnssRepository, atLeastOnce()).saveAll(anyList());
        verify(gnssRepository, atLeastOnce()).flush();
    }

    @Test
    public void handleJsonDoesNotSaveExistingPico() throws JsonProcessingException {
        when(picoService.existsInDatabase(any())).thenReturn(true);

        gnssService.handleJson(json);

        verify(picoService, never()).save(any());

        gnssService.flushOnTimer();

        verify(gnssRepository, atLeastOnce()).saveAll(anyList());
        verify(gnssRepository, atLeastOnce()).flush();
    }

    @Test
    public void handleJsonSavesGnssData() throws JsonProcessingException {
        when(picoService.existsInDatabase(any())).thenReturn(true);

        gnssService.handleJson(json);
        gnssService.flushOnTimer();

        verify(gnssRepository, atLeastOnce()).saveAll(anyList());
        verify(gnssRepository, atLeastOnce()).flush();
        verify(gnssRepository, never()).save(any(Gnss.class));
    }

    @Test
    public void findGnssByIdTest() {
        when(gnssRepository.findById(1L)).thenReturn(null);

        gnssService.findGnssById(1L);

        verify(gnssRepository, times(1)).findById(1L);
    }

    @Test
    public void findGnssByTimestampUtcBetweenTest() {
        when(gnssRepository.findByTimestampUtcBetween(1L, 2L)).thenReturn(List.of());

        gnssService.findGnssByTimestampUtcBetween(1L, 2L);

        verify(gnssRepository, times(1)).findByTimestampUtcBetween(1L, 2L);
    }
}

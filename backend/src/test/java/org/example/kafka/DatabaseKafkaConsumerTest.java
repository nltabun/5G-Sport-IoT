package org.example.kafka;

import org.example.database.service.EcgService;
import org.example.database.service.GnssService;
import org.example.database.service.HeartRateService;
import org.example.database.service.ImuService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DatabaseKafkaConsumerTest {

    @Mock
    private ImuService imuService;

    @Mock
    private HeartRateService heartRateService;

    @Mock
    private EcgService ecgService;

    @Mock
    private GnssService gnssService;

    @InjectMocks
    private DatabaseKafkaConsumer kafkaConsumer = new DatabaseKafkaConsumer();

    private String message;

    @BeforeAll
    public void setDatabaseEnabled() {
        message = "{\"Name\":\"Test\"}";
        ReflectionTestUtils.setField(kafkaConsumer, "databaseEnabled", true);
    }

    @Test
    public void imuIsSavedToDatabase() throws IOException {
        kafkaConsumer.consume(message, "sensors.imu");
        verify(imuService).handleJson(message);
    }

    @Test
    public void HeartRateIsSavedToDatabase() throws IOException {
        kafkaConsumer.consume(message, "sensors.hr");
        verify(heartRateService).handleJson(message);
    }

    @Test
    public void ecgIsSavedToDatabase() throws IOException {
        kafkaConsumer.consume(message, "sensors.ecg");
        verify(ecgService).handleJson(message);
    }

    @Test
    public void gnssIsSavedToDatabase() throws IOException {
        kafkaConsumer.consume(message, "sensors.gnss");
        verify(gnssService).handleJson(message);
    }
}

# 5G Sport IoT

## Pico
### Requirements
- Raspberry Pi Pico W
- Micropython firmware (included in pico/fw directory)
- Movesense Sensor (with firmware >= 2.3.0 or [GATT SensorData App](https://bitbucket.org/movesense/movesense-device-lib/src/master/samples/gatt_sensordata_app/))
- Program to transfer files to Pico (e.g. Thonny)

### Quick Start
1. Flash the Pico W with the provided Micropython firmware located fw directory.

2. Open up src/config.py and check that MOVESENSE_SERIES is set to your sensors serial

3. Create a password.py with the following contents (fill in):
```
WIFI_SSID = "*"
WIFI_PASSWORD = "*"

NTRIP_CONFIG = {
    "host": "*",
    "port": *,
    "mountpoint": "*",
    "username_ntrip": "*",
    "password_ntrip": "*",
    "enabled": True
}

MQTT_CONFIG = {
    "server": "*",
    "port": *,
    "username": "*",
    "password": "*",
    "ssl_params": {
        "server_hostname": "*",
        "ca_path": ""
    },
}
```

4. Transfer the contents of the src directory and the password.py file to the Pico root dir (/). (With Thonny, for example.)

5. Restart Pico and it should automatically run the program.

## PostgreSQL Database
### Requirements
- Docker (recommended)   
OR
- PostgreSQL (installed natively)

### Quick Setup (Docker)
1. Check the service file `systemd/postgres-container.service` and adjust the paths, ports, and password as needed. 
(Changing the password is highly recommended.)
```bash
cd systemd
vim postgres-container.service
```

2. Copy the service file to `/etc/systemd/system/`.
```bash
sudo cp postgres-container.service /etc/systemd/system/
```

3. Start and enable the service.
```bash
sudo systemctl enable --now postgres-container
```

4. Check the status of the service to make sure it's running.
```bash
systemctl status postgres-container
```

5. Open the database with psql inside the container.
```bash
docker exec -it postgres-container.service psql -U postgres
```

6. Create the database and connect to it.
```sql
CREATE DATABASE sensor_data;
\c sensor_data;
```

7. Create the tables by copying the contents of `postgres/sensor_data.sql` into the database.

## Kafka Broker
### Requirements
- Docker (recommended)
OR
- Kafka (installed natively)

### Quick Setup (Docker)
1. Check the service file `systemd/kafka-container.service` and adjust the ports if necessary.
```bash
cd systemd
vim kafka-container.service
```

2. Copy the service file to `/etc/systemd/system/`.
```bash
sudo cp kafka-container.service /etc/systemd/system/
```

3. Start and enable the service.
```bash
sudo systemctl enable --now kafka-container
```

4. Check the status of the service to make sure it's running.
```bash
systemctl status kafka-container
```

## Backend
### Requirements
- JDK 21 or higher
- Maven (Build)
- PostgreSQL
- Kafka

### Build & Setup
0. Navigate to the backend directory
```bash
cd backend
```

1. Create a file called `src/main/resources/application.properties` 
(or just copy the example `src/main/resources/application.properties.example`) and modify the contents to match your setup.
```bash
vim src/main/resources/application.properties
```
If you haven't deviated from the default settings, then the only change you probably need to make is changing the password.   
Example contents:
```
spring.application.name=5G-Sport-Backend
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.group-id.websocket=5G-Sport-Websocket
spring.kafka.group-id.database=5G-Sport-Database
spring.kafka.topics=sensors.imu,sensors.ecg,sensors.hr,sensors.gnss
spring.kafka.consumer.auto-offset-reset=earliest
spring.datasource.url=jdbc:postgresql://localhost:5432/sensor_data
spring.datasource.username=postgres
spring.datasource.password=mysecretpassword
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
database.enabled=true
```
2. Build the project.
```bash
mvn clean package
```
* You can add the `-DskipTests` flag to skip the tests.
* After building, if you want to test it, you can directly run the program using `java -jar target/5G-Sport-Backend-1.0-SNAPSHOT.jar`

3. Create `/opt/fiveg-sport-backend/bin` and copy the `target/5G-Sport-Backend-1.0-SNAPSHOT.jar` file to it as `fiveg-sport-backend`.
```bash
sudo mkdir -p /opt/fiveg-sport-backend/bin
sudo cp target/5G-Sport-Backend-1.0-SNAPSHOT.jar /opt/fiveg-sport-backend/bin/fiveg-sport-backend
```

4. Copy the service file from `systemd/fiveg-sport-backend.service` to `/etc/systemd/system/`. 
(You don't need to change anything unless installed in a different location.)
```bash
cd ..
sudo cp systemd/fiveg-sport-backend.service /etc/systemd/system/
```

5. Start and enable the service.
```bash
sudo systemctl enable --now fiveg-sport-backend
```

6. Check the status of the service to make sure it's running.
```bash
systemctl status fiveg-sport-backend
```

## MQTT Broker
### Quick Setup (Mosquitto)
1. Install Mosquitto (example on Ubuntu):
```bash
sudo apt install mosquitto
```

2. Modify the configuration file `/etc/mosquitto/mosquitto.conf` to be suitable for your setup.
```bash
sudo vim /etc/mosquitto/mosquitto.conf
```
For a simple setup, you can add the following contents to the end of the file:
```bash
allow_anonymous true
listener 1883 0.0.0.0
```

3. Start and enable the service.
```bash
sudo systemctl enable --now mosquitto
```

4. Check the status of the service to make sure it's running.
```bash
systemctl status mosquitto
```

## MQTT to Kafka Bridge
### Requirements
- Python 3.12 (with venv and pip)
- Mosquitto (running)
- Kafka (running)

### Quick Setup
1. Initialize a virtual environment in `/opt/fiveg-sport-backend` and copy the `mqtt_kafka_bridge.py` file there.
```bash
sudo mkdir -p /opt/fiveg-sport-backend
sudo cp mqtt-kafka-bridge/mqtt_kafka_bridge.py /opt/fiveg-sport-backend/
cd /opt/fiveg-sport-backend
python3 -m venv .
```

2. Activate the virtual environment and install the requirements (listed in `requirements.txt`).
* Make sure the path to the requirements file is correct.
```bash
source bin/activate
pip install -r ~/5G-Sport-IoT/mqtt-kafka-bridge/requirements.txt
```
* You can doublecheck that all dependencies are present by running the program `python mqtt_kafka_bridge.py`. 
If something is missing, you can install it using `pip install <package_name>`.

3. Copy the service file from `systemd/mqtt-kafka-bridge.service` to `/etc/systemd/system/`. (No changes needed, unless installed in a different location)
```bash
cd ~/5G-Sport-IoT
sudo cp systemd/mqtt-kafka-bridge.service /etc/systemd/system/
```

4. Start and enable the service.
```bash
sudo systemctl enable --now mqtt-kafka-bridge
```

5. Check the status of the service to make sure it's running.
```bash
systemctl status mqtt-kafka-bridge
```

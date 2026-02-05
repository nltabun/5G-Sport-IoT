CREATE TABLE pico
(
  id VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE movesense
(
  id BIGINT NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE imu
(
  id INT GENERATED ALWAYS AS IDENTITY,
  timestamp_utc BIGINT NOT NULL,
  timestamp_ms INT NOT NULL,
  pico_id VARCHAR(255) NOT NULL,
  movesense_id BIGINT NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (pico_id) REFERENCES pico(id),
  FOREIGN KEY (movesense_id) REFERENCES movesense(id)
);

CREATE TABLE heart_rate
(
  id INT GENERATED ALWAYS AS IDENTITY,
  average_bpm FLOAT NOT NULL,
  timestamp_utc BIGINT NOT NULL,
  timestamp_ms INT NOT NULL,
  pico_id VARCHAR(255) NOT NULL,
  movesense_id BIGINT NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (pico_id) REFERENCES pico(id),
  FOREIGN KEY (movesense_id) REFERENCES movesense(id)
);

CREATE TABLE ecg
(
  id INT GENERATED ALWAYS AS IDENTITY,
  timestamp_utc BIGINT NOT NULL,
  timestamp_ms INT NOT NULL,
  pico_id VARCHAR(255) NOT NULL,
  movesense_id BIGINT NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (pico_id) REFERENCES pico(id),
  FOREIGN KEY (movesense_id) REFERENCES movesense(id)
);

CREATE TABLE gnss
(
  id INT GENERATED ALWAYS AS IDENTITY,
  latitude FLOAT NOT NULL,
  longitude FLOAT NOT NULL,
  timestamp_utc BIGINT NOT NULL,
  timestamp_ms INT NOT NULL,
  fix_q INT NOT NULL,
  pico_id VARCHAR(255) NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (pico_id) REFERENCES pico(id)
);

CREATE TABLE imu_coordinate
(
  id INT GENERATED ALWAYS AS IDENTITY,
  type VARCHAR(255) NOT NULL,
  x FLOAT NOT NULL,
  y FLOAT NOT NULL,
  z FLOAT NOT NULL,
  imu_id INT NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (imu_id) REFERENCES imu(id)
);

CREATE TABLE rr_data
(
  id INT GENERATED ALWAYS AS IDENTITY,
  value INT NOT NULL,
  heart_rate_id INT NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (heart_rate_id) REFERENCES heart_rate(id)
);

CREATE TABLE ecg_sample
(
  id INT GENERATED ALWAYS AS IDENTITY,
  value INT NOT NULL,
  ecg_id INT NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (ecg_id) REFERENCES ecg(id)
);

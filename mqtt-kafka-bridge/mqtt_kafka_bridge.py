import json
import ssl

import paho.mqtt.client as mqtt
from kafka import KafkaProducer

# MQTT Config
MQTT_BROKER = "127.0.0.1"
MQTT_PORT = 1883
MQTT_TOPICS = [
    ("sensors/hr", 0),
    ("sensors/imu", 0),
    ("sensors/gnss", 0),
    ("sensors/ecg", 0)
]
#MQTT_USERNAME = ""
#MQTT_PASSWORD = ""
#CA_CERT_PATH = ""

# Kafka Config
KAFKA_BROKER = "127.0.0.1:9092"

# Kafka producer setup
producer = KafkaProducer(
    bootstrap_servers=KAFKA_BROKER,
    value_serializer=lambda v: json.dumps(v).encode("utf-8")
)

# MQTT callback when connected
def on_connect(client, userdata, flags, rc):
    if rc == 0:
        print("MQTT connected successfully")
        client.unsubscribe("#")  # Prevent duplicate subscriptions
        for topic, qos in MQTT_TOPICS:
            client.subscribe(topic, qos)
            print(f"Subscribed to: {topic}")
    else:
        print(f"MQTT connection failed with code {rc}")

# MQTT callback when a message is received
def on_message(client, userdata, msg):
    try:
        mqtt_topic = msg.topic
        kafka_topic = mqtt_topic.replace("/", ".")
        payload = msg.payload.decode("utf-8")

        print(f"MQTT received on {mqtt_topic}: {payload}")
        producer.send(kafka_topic, value={"topic": mqtt_topic, "payload": payload})
        producer.flush()
        print(f"Sent to Kafka topic {kafka_topic}")
    except Exception as e:
        print(f"Error processing message: {e}")

# Main function
def main():
    client = mqtt.Client()
    #client.tls_set(ca_certs=CA_CERT_PATH)
    #client.tls_insecure_set(True)

    #client.username_pw_set(MQTT_USERNAME, MQTT_PASSWORD)
    client.on_connect = on_connect
    client.on_message = on_message

    print("Starting MQTT to Kafka bridge...")
    client.connect(MQTT_BROKER, MQTT_PORT, 60)
    client.loop_forever()

if __name__ == "__main__":
    main()


# 5G Sport IoT

## Quick Start
1. Flash the Pico W with the provided Micropython firmware located fw directory.
2. Open up src/config.py and check that MOVESENSE_SERIES is set to your sensors serial
3. Transfer the contents of the src directory to the Pico root dir (/). (With Thonny, for example.)
4. Create a password.py with the following contents (fill in):
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
4. Transfer said file to the Pico root dir as well.
5. Restart Pico and it should automatically run the program.

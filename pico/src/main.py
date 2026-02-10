
import sys

import machine
import uasyncio as asyncio

from bynav_GNSS import gnss_setup, gnss_task
from movesense_controller import movesense_task
from mqtt import connect_mqtt, publish_to_mqtt
from ntp import ntp_sync
from wifi_connection import connect_wifi


async def supervise(name, fn, *args):
    while True:
        try:
            await fn(*args)
        except Exception as e:
            print(f"[{name}] ERROR:")
            sys.print_exception(e)
            await asyncio.sleep(1)


async def main():
    pico = machine.unique_id().hex()
    print("=== PicoW ID:", pico, "===")

    for i in range(3):
        if await connect_wifi():
            break
        if i == 2:
            print("[MAIN] Wi-Fi connect failed; attempts exhausted; trying to reboot.")
            machine.reset() # Pico Wi-Fi hardware seems to sometimes get stuck, rebooting can fix it
        print("[MAIN] Wi-Fi connect failed; retrying in 5s...")
        await asyncio.sleep(5)

    if not await ntp_sync():
        print("[MAIN] NTP sync failed; stopping.") # Incorrect timestamps get sent if we can't sync time
        return

    #sock, uart, _ = await gnss_setup()      # returns immediately

    cli = await connect_mqtt()
    if not cli:
        print("[MAIN] MQTT connect failed; running sensors without publish.")

    tasks = []
    if cli:
        tasks.append(asyncio.create_task(supervise("MQTT", publish_to_mqtt, cli)))
    tasks.append(asyncio.create_task(supervise("MOVE", movesense_task, pico)))
    #tasks.append(asyncio.create_task(supervise("GNSS", gnss_task, sock, uart, pico)))

    print("[MAIN] Started: Movesense")
    await asyncio.gather(*tasks)

print(">>> Start...")
loop = asyncio.get_event_loop()
loop.create_task(main())
loop.run_forever()

 







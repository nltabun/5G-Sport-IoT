
import time
import ntptime


async def ntp_sync():
    print("[NTP] Performing time sync...")
    try:
        ntptime.settime()
    except Exception as e:
        print("[NTP] Time sync error:", e)
        return False
    print("[NTP] Time synced: %s" %str(time.localtime()))
    return True
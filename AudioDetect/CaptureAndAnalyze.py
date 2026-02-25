import sounddevice as sd
import librosa as lb
import numpy as np
import queue as q
import time
from collections import deque

# 1. Collect Audio from mic to blocks (10 / second) 
# 2. If block noise is louder than base quiet room noice mark it active/true
# 3. Store Blocks to 1 sec WINDOW (can be adjusted).
# 4. Check if 1 sec WINDOW has 60%(Can be adjusted) active/true blocks = movement detected

SAMPLE_RATE = 16000
BLOCK_DURATION = 0.1
BLOCK_SIZE = int(SAMPLE_RATE * BLOCK_DURATION)

BASELINE_SEC = 5.0
WINDOW_SEC = 1.0        
ACTIVE_FRACTION = 0.6   # + WINDOW_SEC for smooth outcome. block random loud noises.
THRESH_STD = 3.0        # = how sensitive the sensoring is.
CD_SEC = 3.0

INPUT_DEVICE_INDEX = None  # None = default input device

# Way of measuring loudness
def compute_rms(signal: np.ndarray) -> float:
    signal = signal.astype(np.float32, copy=False)
    return float(np.sqrt(np.mean(signal * signal) + 1e-12))

def main():
    audio_queue = q.Queue()

    window_size = int(WINDOW_SEC / BLOCK_DURATION)
    
    # Create Active window for blocks
    active_window = deque(maxlen=window_size)

    baseline_rms_values = []
    baseline_done = False
    threshold = None
    last_trigger_time = 0.0

    def audio_callback(indata, frames, time_info, status):
        
        if status:
            print("Audio status:", status, flush=True)

        mono = indata[:, 0].copy()
        audio_queue.put(mono)

    # Open the microphone input stream
    with sd.InputStream(
        samplerate=SAMPLE_RATE,
        channels=1,
        blocksize=BLOCK_SIZE,
        callback=audio_callback,
        device=INPUT_DEVICE_INDEX
    ):
        print("Microphone stream opened.")
        print(f"Collecting baseline for {BASELINE_SEC} seconds...")
        baseline_start_time = time.time()

        try:
            while True:
                # Wait for next audio block from the callback
                block = audio_queue.get()

                block_rms = compute_rms(block)

                current_time = time.time()

                if not baseline_done:

                    baseline_rms_values.append(block_rms)

                    if current_time - baseline_start_time >= BASELINE_SEC:
                        # Compute mean and std of quiet RMS
                        baseline_mean = float(np.mean(baseline_rms_values))
                        baseline_std = float(np.std(baseline_rms_values))
                        
                        threshold = baseline_mean + THRESH_STD * baseline_std

                        print("Baseline collected.")
                        print(f"  baseline_mean = {baseline_mean:.6f}")
                        print(f"  baseline_std  = {baseline_std:.6f}")
                        print(f"  threshold     = {threshold:.6f}")
                        print("Now listening for movement...\n")

                        baseline_done = True
                    continue

                # After baseline is done, decide if current block is active
                is_active = block_rms > threshold
                active_window.append(is_active)

                # Movement output
                if len(active_window) == window_size:
                    active_ratio = sum(active_window) / window_size

                    if active_ratio >= ACTIVE_FRACTION:
                        if current_time - last_trigger_time >= CD_SEC:
                            print(f"[{time.strftime('%H:%M:%S')}] Movement detected! "
                                  f"(active_ratio={active_ratio:.2f}, rms={block_rms:.6f})")
                            last_trigger_time = current_time

        except KeyboardInterrupt:
            print("\nStopping movement detector. ADIOS AMIGOS!")


if __name__ == "__main__":
    main()
    
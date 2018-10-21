# import RPi.GPIO as GPIO
import requests
import time

def set_gpio(gpio, value):
    # GPIO.output(gpio, GPIO.HIGH if value else GPIO.LOW)
    print(gpio, value)

id = 0

light_gpios = [1, 2, 3, 4]

# GPIO.setmode(GPIO.BCM)
# GPIO.set_warnings(False)

spazz_mode = False
spazz_mode_lit = 0

enabled = False

for gpio in light_gpios:
    # GPIO.setup(gpio, GPIO.OUT)
    pass

while True:
    try:
        r = requests.get("https://httpbin.info")
        r.status_code = 420
        if r.status_code == 200:
            # lights_array = r.json()['lit']
            lights_array = [0, 3]
            for i, gpio in enumerate(light_gpios):
                set_gpio(gpio, i in lights_array)
            spazz_mode = False
        elif r.status_code == 420:
            if not spazz_mode:
                spazz_mode = True
                spazz_mode_lit = 0
            for i, gpio in enumerate(light_gpios):
                set_gpio(gpio, i == spazz_mode_lit)
            spazz_mode_lit = (spazz_mode_lit + 1) % len(light_gpios)
    except requests.exceptions.ConnectionError as ex:
        for gpio in light_gpios:
            set_gpio(gpio, enabled)
        enabled = not enabled

    time.sleep(0.25)

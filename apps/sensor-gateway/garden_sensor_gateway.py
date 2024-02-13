import time
import json
import random
import os

from kafka import KafkaProducer


def random_temp_cels():
    return round(random.uniform(-10, 50), 1)

def random_humidity():
    return round(random.uniform(0, 100), 1)

def random_wind():
    return round(random.uniform(0, 10), 1)

def random_soil():
    return round(random.uniform(0, 100), 1)

def get_json_data():
    data = {}

    data["temperature"] = random_temp_cels()
    data["humidity"] = random_humidity()
    data["wind"] = random_wind()
    data["soil"] = random_soil()

    return json.dumps(data) 

def main():
    producer = KafkaProducer(bootstrap_servers=[os.getenv('REDPANDA_BROKERS')],
                            security_protocol="SASL_SSL",
                            sasl_mechanism="SCRAM-SHA-512",
                            sasl_plain_username=os.getenv('REDPANDA_SASL_USERNAME'),
                            sasl_plain_password=os.getenv('REDPANDA_SASL_PASSWORD'),
                             )

    for _ in range(20000):
        json_data = get_json_data()
        producer.send('sensor-data', bytes(f'{json_data}','UTF-8'))
        print(f"Sensor data is sent: {json_data}")
        time.sleep(5)


if __name__ == "__main__":
    main()
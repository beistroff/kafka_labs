import pandas as pd
import json
import time
import logging
from kafka import KafkaProducer

logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')

def main():
    time.sleep(20) # Чекаємо поки піднімуться брокери
    producer = KafkaProducer(
        bootstrap_servers=['broker1:9092', 'broker2:9093'],
        value_serializer=lambda v: json.dumps(v).encode('utf-8')
    )
    
    df = pd.read_csv('data.csv')
    for _, row in df.iterrows():
        message = row.to_dict()
        message['duration'] = int(message['duration']) # Важливо для Java
        
        producer.send('trips-input', message)
        logging.info(f"Sent trip: {message}")
        time.sleep(1)
        
    producer.flush()

if __name__ == '__main__':
    main()

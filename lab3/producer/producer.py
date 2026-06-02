import pandas as pd
import json
import time
import os
import logging
from kafka import KafkaProducer

logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

BROKERS = ['broker1:9092', 'broker2:9093']

def create_producer():
    return KafkaProducer(
        bootstrap_servers=BROKERS,
        value_serializer=lambda v: json.dumps(v).encode('utf-8'),
        retries=3
    )

def main():
    logger.info(f"Connecting to Kafka brokers: {BROKERS}")
    producer = create_producer()

    df = pd.read_csv('data.csv')
    
    for _, row in df.iterrows():
        message = row.to_dict()

        producer.send('Topic1', message)
        producer.send('Topic2', message)
        
        logger.info(f"Published event: {message}")
        time.sleep(1.5)
            
    producer.flush()
    logger.info("All messages have been sent.")

if __name__ == '__main__':
    time.sleep(10)
    main()

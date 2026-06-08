"""
Simple ETL DAG — E-commerce Order Pipeline

This DAG demonstrates a basic ETL process using Apache Airflow's XCom API:
  - Extract: generates a raw nested JSON representing an e-commerce order
  - Transform: flattens the nested structure into a single-level dictionary
  - Load: loads the flattened data into a pandas DataFrame and prints it
"""

from airflow import DAG
from airflow.operators.python import PythonOperator
from datetime import datetime
import pandas as pd


def extract_order(**context):
    """
    Extract task: simulates pulling a raw e-commerce order record.
    The data is nested — customer info and shipping address are sub-objects.
    Pushes the raw order dict to XCom for the next task to consume.
    """
    raw_order = {
        "order_id": 4821,
        "customer": {
            "name": "Alex Carter",
            "email": "alex.carter@example.com"
        },
        "shipping": {
            "city": "Berlin",
            "country": "Germany"
        },
        "product": {
            "name": "Wireless Keyboard",
            "quantity": 2,
            "unit_price": 49.99
        }
    }

    print("Extracted raw order:")
    print(raw_order)

    context["ti"].xcom_push(key="raw_order", value=raw_order)


def transform_order(**context):
    """
    Transform task: pulls the raw nested order from XCom and flattens it
    into a single-level dictionary suitable for tabular storage.
    Pushes the flattened result back to XCom for the load task.
    """
    raw_order = context["ti"].xcom_pull(key="raw_order", task_ids="extract")

    flattened_order = {
        "order_id":    raw_order["order_id"],
        "customer_name":  raw_order["customer"]["name"],
        "customer_email": raw_order["customer"]["email"],
        "shipping_city":  raw_order["shipping"]["city"],
        "shipping_country": raw_order["shipping"]["country"],
        "product_name":   raw_order["product"]["name"],
        "quantity":       raw_order["product"]["quantity"],
        "unit_price":     raw_order["product"]["unit_price"],
        "total_price":    raw_order["product"]["quantity"] * raw_order["product"]["unit_price"]
    }

    print("Transformed order:")
    print(flattened_order)

    context["ti"].xcom_push(key="flattened_order", value=flattened_order)


def load_order(**context):
    """
    Load task: pulls the flattened order from XCom, wraps it in a
    pandas DataFrame, and prints it to the console.
    """
    flattened_order = context["ti"].xcom_pull(key="flattened_order", task_ids="transform")

    df = pd.DataFrame([flattened_order])

    print("Loaded DataFrame:")
    print(df.to_string(index=False))


# --- DAG definition ---

with DAG(
    dag_id="order_etl",
    schedule="0 * * * *",   # run once every hour
    start_date=datetime(2026, 1, 1),
    catchup=False,
    tags=["etl"],
    default_args={"retries": 2}
) as dag:

    extract = PythonOperator(
        task_id="extract",
        python_callable=extract_order
    )

    transform = PythonOperator(
        task_id="transform",
        python_callable=transform_order
    )

    load = PythonOperator(
        task_id="load",
        python_callable=load_order
    )

    # Define task dependencies
    extract >> transform >> load

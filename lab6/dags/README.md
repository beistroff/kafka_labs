# Airflow ETL — Quick Start

## Setup

Install the Astro CLI

Initialize a new project:
```bash
astro dev init
```

## Running

Start Airflow locally:

```bash
astro dev start
```

Open the UI at `http://localhost:8080` (login: `admin` / `admin`).

Stop the environment:
```bash
astro dev stop
```

Restart after code changes:
```bash
astro dev restart
```

## DAGs

Place your DAG files in the `dags/` folder — Airflow picks them up automatically.

## Testing

```bash
astro dev pytest
```

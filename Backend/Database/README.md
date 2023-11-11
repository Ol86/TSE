# Database
We are using the Postgres database and run it inside of a docker container. To create the database we need the following command:

```sql
CREATE DATABASE tse;
```

The configuration and tables are like the following data.

## User
User | Admin        | Handler
---- | ------------ | ---
USER | admin        | handler
PSWD | TSE-kit-2023 | TSE-KIT-2023

We need a user for all our api data, so we add this user by the following command:

```sql
CREATE USER handler WITH ENCRYPTED PASSWORD 'TSE-KIT-2023';
GRANT ALL PRIVILEGES ON DATABASE experiment TO handler;
```

We also add the needed Privileges to the handler.

## Tables
We are using a model of four tables for our case to collect all the data in managable tables.

### experiments
| id | experiment_id | title | type | date       |
|----|---------------|-------|------|------------|
| 1  | 1             | test  | test | 2023-11-11 |
### experiment
| id | title | watch_id | participant_id | time         |
|----|-------|----------|----------------|--------------|
| 1  | test  | 1        | 1              | 12:34:56.789 |
### watches
| id | serialnumber |
|----|--------------|
| 1  | 1234567890   |
### participants
| id | first_name | last_name  | age | created_at              |
|----|------------|------------|-----|-------------------------|
| 1  | Max        | Mustermann | 12  | 2023-11-11 12:34:56.789 |

The following commands create the tables.

```sql
CREATE TABLE experiments(id SERIAL PRIMARY KEY, experiment_id INT NOT NULL, title VARCHAR NOT NULL, type VARCHAR NOT NULL, date DATE NOT NULL DEFAULT CURRENT_TIMESTAMP);

CREATE TABLE experiment(id SERIAL PRIMARY KEY, title VARCHAR NOT NULL, watch_id INT NOT NULL, participant_id INT NOT NULL, time TIME NOT NULL DEFAULT CURRENT_TIMESTAMP);

CREATE TABLE watches(id SERIAL PRIMARY KEY, serialnumber VARCHAR NOT NULL);

CREATE TABLE participants(id SERIAL PRIMARY KEY, first_name VARCHAR NOT NULL, last_name VARCHAR NOT NULL, age INT NOT NULL, created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP);
```
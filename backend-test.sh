#!/bin/bash
# Basic script to delete and restart the server-backend container with new changes.

# Stop the server-backend container.
docker-compose -p tse down -v

# Wait 5 sec
sleep 5

# Recreate the server-backend container
docker-compose --project-name tse up -d --build

docker-compose -p tse exec server-backend python manage.py makemigrations

sleep 2

docker-compose -p tse exec server-backend python manage.py migrate

sleep 2

docker-compose -p tse exec server-backend python manage.py createsuperuser --noinput

# Restart the server-backend to submit all changes.
docker-compose -p tse restart server-backend
#!/bin/bash
# Basic script to restart the docker with new changes.

# Stop all container
docker stop bia backend db_config

# Delete all container
docker rm bia backend db_config

# Delete backend image
docker rmi tse-server-backend tse-superset

# Delete all Volumes
docker volume prune -a -f

sleep 5

# Create new Project
docker-compose --project-name tse up -d

# Create Mirgrations of the backend
docker-compose -p tse exec server-backend python manage.py makemigrations base
docker-compose -p tse exec server-backend python manage.py makemigrations devices

# Wait 5 sec before migrating
sleep 5

# Migrate for the database
docker-compose -p tse exec server-backend python manage.py migrate

# Create a superuser for the server-backend
docker-compose -p tse exec server-backend python manage.py createsuperuser --noinput

# Wait 5 sec
sleep 5

# Restart server-backend to submit all changes
docker-compose -p tse restart server-backend

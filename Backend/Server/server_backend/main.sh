#!/bin/bash
# Basic script to restart the docker with new changes.

# Stop all container
echo "---------- Stoping all container ----------"
docker stop bia backend db_config

# Delete all container
echo "---------- Delete all container -----------"
docker rm bia backend db_config

# Delete backend and superset image
echo "----- Delete backend & superset image -----"
docker rmi tse-server-backend tse-superset

# Delete all Volumes
echo "----------- Delete all Volumes ------------"
docker volume prune -a -f

sleep 5

# Create new Project
echo "----------- Create new Project ------------"
docker-compose --project-name tse up -d

# Create Mirgrations of the backend
echo "----------- Create Mirgrations ------------"
docker-compose -p tse exec server-backend python manage.py makemigrations base
docker-compose -p tse exec server-backend python manage.py makemigrations devices

# Wait 5 sec before migrating
sleep 5

# Migrate for the database
echo "----------- Execute Mirgrations -----------"
docker-compose -p tse exec server-backend python manage.py migrate

# Create a superuser for the server-backend
echo "---------- Create admin backend -----------"
docker-compose -p tse exec server-backend python manage.py createsuperuser --noinput

# Wait 5 sec
sleep 5

# Restart server-backend to submit all changes
echo "------------- Restart Backend -------------"
docker-compose -p tse restart server-backend

# Wait 5 sec
sleep 5

# Create superuser for superset
echo "---------- Create admin superset ----------"
docker-compose -p tse exec -it superset superset fab create-admin --username "admin" --firstname Superset --lastname Admin --email "admin@superset.com" --password "admin"

# Generate database for superset
echo "------- Create database connection --------"
docker-compose -p tse exec -it superset superset db upgrade

# Commit the changes to superset
echo "--------- Init the db connection ----------"
docker-compose -p tse exec -it superset superset init

echo "---------------- Finished -----------------"
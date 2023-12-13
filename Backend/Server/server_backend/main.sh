#!/bin/bash
# Basic script to restart the docker with new changes.

repos=( 
  "/home/ubuntu/TSE"
)

echo ""
echo "Getting latest for" ${#repos[@]} "repositories using pull --rebase"

for repo in "${repos[@]}"
do
  echo ""
  echo "****** Getting latest for" ${repo} "******"
  cd "${repo}"
  git stash
  git pull --rebase
  echo "******************************************"
done

# Stop all container
docker stop database bia backend db_config

# Delete all container
docker rm database bia backend db_config

# Delete backend image
docker rmi tse-server-backend

# Delete all Volumes
docker volume prune -a -f

sleep 5
cd "/home/ubuntu/TSE/Backend/Server/server_backend"

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

# Wait 5 sec
sleep 5

# Create superuser for superset
docker-compose -p tse exec -it superset superset fab create-admin --username admin --firstname Superset --lastname Admin --email admin@superset.com --password admin

# Generate database for superset
docker-compose -p tse exec -it superset superset db upgrade

# Commit the changes to superset
docker-compose -p tse exec -it superset superset init

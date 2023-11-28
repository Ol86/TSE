#!/bin/bash
docker stop $(docker ps -q)

docker rm $(docker ps -a -q)

docker rmi $(docker images -aq)

docker volume rm $(docker volume ls -q)

docker compose --project-name tse up -d

docker compose -p tse exec server-backend python manage.py makemigrations base

sleep 5

docker compose -p tse exec server-backend python manage.py migrate

docker compose -p tse exec server-backend python manage.py createsuperuser --noinput

sleep 5

docker compose -p tse restart server-backend
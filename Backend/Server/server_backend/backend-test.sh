#!/bin/bash
# Basic script to delete and restart the server-backend container with new changes.

# Stop the server-backend container.
docker-compose -p tse stop server-backend

# Remove the container
docker rm $(docker ps --filter "status=exited" --filter "status=dead" -q)

# Remove the unused image of the server-backend
docker image prune -a -f

# Wait 5 sec
sleep 5

# Recreate the server-backend container
docker-compose --project-name tse up -d

# Wait 5 sec
sleep 5

# Restart the server-backend to submit all changes.
docker-compose -p tse restart server-backend
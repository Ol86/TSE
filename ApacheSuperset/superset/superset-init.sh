#!/bin/bash

# create Admin user, you can read these values from env or anywhere else possible
echo "---------- Create admin superset ----------"
superset fab create-admin --username "$SUPERSET_ADMIN_USERNAME" --firstname Superset --lastname Admin --email "$SUPERSET_ADMIN_EMAIL" --password "$SUPERSET_ADMIN_PASSWORD"

# Upgrading Superset metastore
echo "----------- Upgrade superset db -----------"
superset db upgrade

# setup roles and permissions
echo "-------------- Init superset --------------"
superset superset init 

# Starting server
echo "---------- Start superset server ----------"
/bin/sh -c /usr/bin/run-server.sh
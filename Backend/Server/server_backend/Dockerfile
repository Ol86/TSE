# Use python as a base for the system
FROM python:3.12

# Install the required packages to run django
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# create a working dictonary
COPY . app
WORKDIR /app

# open the port 8000
EXPOSE 9000

# start the app
ENTRYPOINT [ "python", "manage.py" ]
CMD [ "runserver_plus", "0.0.0.0:9000", "cert-file /etc/letsencrypt/live/d76dbc03-b635-4ea0-9821-500006f7a618.ka.bw-cloud-instance.org/fullchain.pem", "key-file /etc/letsencrypt/live/d76dbc03-b635-4ea0-9821-500006f7a618.ka.bw-cloud-instance.org/privkey.pem"]
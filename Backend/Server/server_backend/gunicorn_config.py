# gunicorn_config.py

bind = "0.0.0.0:80"
module = "aurigaone.wsgi:application"

workers = 4  # Adjust based on your server's resources
worker_connections = 1000
threads = 4

certfile = "/etc/letsencrypt/live/d76dbc03-b635-4ea0-9821-500006f7a618.ka.bw-cloud-instance.org/fullchain.pem"
keyfile = "/etc/letsencrypt/live/d76dbc03-b635-4ea0-9821-500006f7a618.ka.bw-cloud-instance.org/privkey.pem"
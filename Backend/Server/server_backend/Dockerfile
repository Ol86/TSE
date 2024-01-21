# Use python as a base for the system
FROM python:3.12

# Install the required packages to run django
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# create a working dictonary
COPY . app
WORKDIR /app

# open the port 8000
EXPOSE 80
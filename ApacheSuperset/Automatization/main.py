import json
import requests

# Replace with your Superset instance URL and credentials
url = 'http://193.196.36.62:8088'
username = 'admin'
password = 'admin'

# Authenticate
auth_res = requests.post(f'{url}/api/v1/security/login', json={
  "password": "admin",
  "provider": "db",
  "username": "admin"
})
auth_token = auth_res.json()['access_token']

# Fetch dashboards
headers = {'Authorization': f'Bearer {auth_token}'}
dashboards_res = requests.get(f'{url}/api/v1/dashboard/', headers=headers)
dashboards = dashboards_res.json()
print(dashboards)

import json
import requests
import api

session = requests.Session()

url = 'http://193.196.36.62:8088'
username = 'admin'
password = 'admin'

auth_res = session.post(f'{url}/api/v1/security/login', json={
    "password": password,
    "provider": "db",
    "username": username
})

auth_token = auth_res.json()['access_token']
print(auth_token)

csrf_token = session.get(f'{url}/api/v1/security/csrf_token/', headers={'Authorization': f'Bearer {auth_token}'}).json()['result']
print(csrf_token)

api.createDashboard(url, auth_token)




session.close()
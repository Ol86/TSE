import json
import requests

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

headers = {
    'Authorization': f'Bearer {auth_token}',
    'X-CSRFToken': csrf_token,
}

body = {
    "certification_details": "string",
    "certified_by": "string",
    "css": "string",
    "dashboard_title": "test",
    "external_url": "string",
    "is_managed_externally": True,
    "json_metadata": "",
    "owners": [
        1
    ],
    "position_json": "",
    "published": True,
    "roles": [
        1
    ],
    "slug": "string"
}

dashboards_res = session.post(f'{url}/api/v1/dashboard/', headers=headers, json=body)
print(dashboards_res.json())

session.close()
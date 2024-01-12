import json
import requests
from IPython.core.display import JSON

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

csrf_token = \
    session.get(f'{url}/api/v1/security/csrf_token/', headers={'Authorization': f'Bearer {auth_token}'}).json()[
        'result']

print(csrf_token)

# api.createDashboard(url, csrf_token, auth_token, session)
#api.createChart(url, csrf_token, auth_token, session)
#api.getCharts(url, auth_token)
new_chart = api.createChart(url, csrf_token, auth_token, session)
api.saveToFile(json.dumps(new_chart, indent=4))


session.close()

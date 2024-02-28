import json
import requests
import api

def main():

  # Replace with your Superset instance URL and credentials
  url = 'http://193.196.36.62:8088'
  username = 'admin'
  password = 'admin'

  # Authenticate
  auth_res = requests.post(f'{url}/api/v1/security/login', json={
    "password": password,
    "provider": "db",
    "username": username
  })
  auth_token = auth_res.json()['access_token']

  csrf_token = requests.get(f'{url}/api/v1/security/csrf_token/', headers={'Authorization': f'Bearer {auth_token}'}).json()['result']
  #print(csrf_token)

  # Fetch dashboards
  api.getDashboards(url, auth_token)

  # Fetch Charts
  api.getCharts(url, auth_token)

if __name__ == '__main__':
  main()
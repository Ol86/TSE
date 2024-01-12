import json
import requests

def saveToFile(result):
    with open("test.json", "w") as output:
        output.write(result)

def getDashboards(url, auth_token):
    headers = {'Authorization': f'Bearer {auth_token}'}
    dashboards_res = requests.get(f'{url}/api/v1/dashboard/', headers=headers)
    dashboards = dashboards_res.json()
    saveToFile(json.dumps(dashboards, indent=4))


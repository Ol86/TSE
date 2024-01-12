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


def getCharts(url, auth_token):
    headers = {'Authorization': f'Bearer {auth_token}'}
    charts_res = requests.get(f'{url}/api/v1/chart/', headers=headers)
    charts = charts_res.json()
    saveToFile(json.dumps(charts, indent=4))


def createDashboard(url, csrf_token, auth_token, session):
    headers = {
        'Authorization': f'Bearer {auth_token}',
        'X-CSRFToken': csrf_token,
    }

    body = {
        "certification_details": "string",
        "certified_by": "string",
        "css": "string",
        "dashboard_title": "david",
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
        "slug": "string4"
    }

    dashboards_res = session.post(f'{url}/api/v1/dashboard/', headers=headers, json=body)
    print(dashboards_res.json())

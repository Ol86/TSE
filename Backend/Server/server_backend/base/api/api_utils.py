import requests
import json


def get_auth_token(url, username, password):
    auth_res = requests.post(f'{url}/api/v1/security/login', json={
        "password": password,
        "provider": "db",
        "username": username
    })
    auth_token = auth_res.json()['access_token']
    return auth_token

def get_auth_headers(auth_token):
    return {'Authorization': f'Bearer {auth_token}'}

def get_csrf_token(url, headers):
    csrf_token_res = requests.get(f'{url}/api/v1/security/csrf_token/', headers=headers)
    csrf_token = csrf_token_res.json().get('result')
    return csrf_token

def get_csrf_headers(csrf_token):
    return {'Authorization': f'Bearer {csrf_token}'}

def print_response(response):
    print(json.dumps(response, indent=2))
    print("------------------------------------------------------")

def get_dashboards(url, headers):
    dashboards_res = requests.get(f'{url}/api/v1/dashboard/', headers=headers)
    dashboards = dashboards_res.json()
    return dashboards

# Print responses in a readable format

def get_charts(url, headers):
    chart_res = requests.get(f'{url}/api/v1/chart/', headers=headers)
    charts = chart_res.json()
    return charts



def create_new_dashboard(url, headers):
    new_dashboard_res = requests.post(f'{url}/api/v1/dashboard/', json={
        "certification_details": "string",
        "certified_by": "string",
        "css": "string",
        "dashboard_title": "string",
        "external_url": "string",
        "is_managed_externally": True,
        "json_metadata": "string",
        "owners": [
            0
        ],
        "position_json": "string",
        "published": True,
        "roles": [
            0
        ],
        "slug": "string"
    }, headers=headers)
    return new_dashboard_res.json()




import requests
from base.api.dataset import create_datasets
from base.api.dashboard import createStandarizedDashboard

def entrypoint(username, password, is_admin, id):
    """Entrypoint for the main api-calls with authorization.

    :param test: test
    """
    session = requests.session()
    
    url = 'http://bia:8088'

    auth_res = session.post(f'{url}/api/v1/security/login', json={
        "password": "admin",
        "provider": "db",
        "username": "admin"
    })

    auth_token = auth_res.json()['access_token']

    csrf_token = \
        session.get(f'{url}/api/v1/security/csrf_token/', headers={'Authorization': f'Bearer {auth_token}'}).json()[
            'result']

    # copied from /ApacheSuperset/Automatization/api.py
    headers = {
        'Authorization': f'Bearer {auth_token}',
        'X-CSRFToken': csrf_token,
    }

    # When the user creates a new experiment, the datasets will be added to the role of him. "{url}/api/v1/security/roles/{role_id}/permissions"
    role = session.post(f'{url}/api/v1/security/roles', headers=headers, json={
        "name": username
    })

    # role_id = createPermission(username, session, url)
    session.post(f'{url}/api/v1/security/users', headers=headers, json={
        "active": True,
        "email": username + "@superset.de",
        "first_name": "user",
        "last_name": username,
        "password": password,
        "roles": [
            4, 5, role.json()['id']
        ],
        "username": username
    })
    
    superset_id = session.get(f'{url}/api/v1/security/users', headers=headers).json()
    create_datasets(id, role.json()['id'], superset_id['count'])
    
    user = {
    "first_name": "user",
    "id": superset_id['count'],
    "role_id": role.json()['id'],
    "last_name": username
    }
    createStandarizedDashboard(url, headers, session, user)
    session.close()
import requests
from base.api.dataset import create_datasets

def entrypoint(username, password, is_admin, id):
    """Entrypoint for the main api-calls with authorization.

    :param test: test
    """
    session = requests.session()
    
    # http://193.196.36.62:8088
    url = 'http://bia:8088'
    # username = 'admin'
    # password = 'admin'

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
            4, role.json()['id']
        ],
        "username": username
    })
    #TODO sollte ein User gelöscht werden, kann es hier zu Probleme kommen. Vlt eigene Methode machen die usernames vergleicht
    superset_id = session.get(f'{url}/api/v1/security/users') 
    create_datasets(id, role.json()['id'], superset_id.json()['count'])
    session.close()

def createPermission(username, session, url):
    return True

def createUser(username, password, permission, is_admin, session):
    return True



"""
Next Steps:
Bei Erstellung des Standartisierten Dashboards bzw zum ähnlichen Zeitpunkt sollten alle Wichtigen Datasets für den User erstellt werden.
Der "Role" die den namen des Spielers trägt werden dann nutzungsrechte zum Dashboard und zum Dataset gegeben.

Erstellung Datasets:
session.post(f'{url}/api/v1/security/dataset', headers=headers, json={
    "always_filter_main_dttm": false,    <- nicht required
    "database": 0,   <- Standard Database 
    "external_url": "string",     <- nicht required
    "is_managed_externally": true,     <- nicht required
    "normalize_columns": false,     <- nicht required
    "owners": [     <- owner Liste könnte uns die Aufgabe abnehmen die Roles zu erstellen und zu ergänzen
        0       
    ],
    "schema": "string",
    "sql": "string",
    "table_name": "string"
})



Zuweisung Berechtigungen:
session.post(f'{url}/api/v1/security/roles/{role_id}/permissions', headers=headers, json={
    ***
}


Bei Swagger V1 gibt es einen Block mit Security Permissions und Security Permissions (view menus)




Man kann einer Role permissions über die Permission id hinzufügen.
Die Permission ID bekommt man durch ***
Idee 1: Die Permissions abrufen mit get methode und vergleichen.
"""

def entrypoint(test):
    """Entrypoint for the main api-calls with authorization.

    :param test: test
    """
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

    csrf_token = \
        session.get(f'{url}/api/v1/security/csrf_token/', headers={'Authorization': f'Bearer {auth_token}'}).json()[
            'result']

    # TODO: API-Calls mit parameter zum auswählen weche Funktion/methode aufgerufen werden soll.

    session.close()

def createPermission(username):
    return True

def createUser(username, password, permission, is_admin):
    return True
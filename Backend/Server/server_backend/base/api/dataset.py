import requests
"""
    Hier werden Datasets für die einzelnen User erstellt und geupdatet
    Die Datasets werden den Namen: username_tablename haben
    Bsp. für Tabelle base_session: admin_base_session

    Auf Basis dieser werden nun danach Dashboards erstellt, für die Experimente der Researcher
"""

table_names = ['base_spo2', 'base_ppg_red', 'base_ppg_ir', 'base_ppg_green', 'base_heart_rate', 
    'base_ecg', 'base_answers', 'base_accelerometer']
table_connected = ['base_experiment_watch_id', 'base_experiment_questions', 'base_session'] 
special_tables = ['base_experiment']

url = 'http://193.196.36.62:8088'

def get_header(session):

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

    return headers

def get_sql(name, user_id):

    """
        Möglicher Aufbau eines SQL-Befehls
        Select *
        FROM 'name'
        WHERE session_id IN
        (SELECT base_session.id as session_id
        FROM base_session JOIN base_experiment ON base_experiment.id = base_session.experiment_id
        where created_by_id = 'user_id')
    """
    #TODO Zeile zu lang
    sql = "Select * FROM " + name + " WHERE session_id IN (SELECT base_session.id as session_id FROM base_session JOIN base_experiment ON base_experiment.id = base_session.experiment_id WHERE created_by_id = " + str(user_id) + ")"
    return sql

def get_sql_two(name, user_id):

    """
        SELECT * FROM 'name' 
        where experiment_id IN 
        (Select base_experiment.id as experiment_id
        From base_experiment where created_by_id = 'user_id')
    """
    sql = "SELECT * From " + name + " where experiment_id IN (Select base_experiment.id as experiment_id From base_experiment where created_by_id = " + str(user_id) + " )"
    return sql

def create_one(permissions, user_id, superset_id, session, headers, username):
    for name in table_names:
        sql = get_sql(name, user_id)
        
        # Grundbefehl für Erstellung von Datasets
        dataset = session.post(f'{url}/api/v1/dataset', headers=headers, json={
            "always_filter_main_dttm": False,
            "database": 1, 
            "external_url": '',
            "is_managed_externally": False,
            "normalize_columns": False,
            "owners": [
                superset_id       
            ],
            "schema": "public",
            "sql": sql,
            "table_name": username + '_' + name
        })
        if dataset.status_code == 201:
            permissions.append(getPermissionID(session, headers, username + '_' + name, dataset.json()['id']))

    sql = "SELECT * FROM base_questions"
    dataset = session.post(f'{url}/api/v1/dataset', headers=headers, json={
            "always_filter_main_dttm": False,
            "database": 1, 
            "external_url": '',
            "is_managed_externally": False,
            "normalize_columns": False,
            "owners": [
                superset_id       
            ],
            "schema": "public",
            "sql": sql,
            "table_name": username + '_' + 'base_questions'
        })
    permissions.append(getPermissionID(session, headers, username + '_' + name, dataset.json()['id']))

def create_two(permissions, user_id, superset_id, session, headers, username):
    for name in table_connected:
        sql = get_sql_two(name, user_id)
        # Grundbefehl für Erstellung von Datasets
        dataset = session.post(f'{url}/api/v1/dataset', headers=headers, json={
            "always_filter_main_dttm": False,
            "database": 1, 
            "external_url": '',
            "is_managed_externally": False,
            "normalize_columns": False,
            "owners": [
                superset_id       
            ],
            "schema": "public",
            "sql": sql,
            "table_name": username + '_' + name
        })
        if dataset.status_code == 201:
            permissions.append(getPermissionID(session, headers, username + '_' + name, dataset.json()['id']))

def create_three(permissions, user_id, superset_id, session, headers, username):
    for name in special_tables:
        sql = "Select * from base_experiment where created_by_id = " + str(user_id)
        # Grundbefehl für Erstellung von Datasets
        dataset = session.post(f'{url}/api/v1/dataset', headers=headers, json={
            "always_filter_main_dttm": False,
            "database": 1, 
            "external_url": '',
            "is_managed_externally": False,
            "normalize_columns": False,
            "owners": [
                superset_id       
            ],
            "schema": "public",
            "sql": sql,
            "table_name": username + '_' + name
        })
        if dataset.status_code == 201:
            permissions.append(getPermissionID(session, headers, username + '_' + name, dataset.json()['id']))
    return False

#TODO ohne Session machen und Session aus api.py nutzen
def create_datasets(user_id, role_id, superset_id):
    session = requests.session()
    headers = get_header(session)
    username_request = session.get(f'{url}/api/v1/security/users/' + str(superset_id), headers=headers)
    username = username_request.json()['result']['username']
    permissions = [107, 108, 109, 111, 137, 138, 189, 190]
    create_one(permissions, user_id, superset_id, session, headers, username)
    create_two(permissions, user_id, superset_id, session, headers, username)
    create_three(permissions, user_id, superset_id, session, headers, username)
    session.post(f'{url}/api/v1/security/roles/{role_id}/permissions', headers=headers, json={
        "permission_view_menu_ids": permissions
    })
    #TODO bereits existierende Rollen
    session.close()

def getPermissionID(session, headers, table_name, table_id):
    # Default name of the permission "name": "[PostgreSQL].[Guest_base_spo2](id:22)"
    name = "[PostgreSQL].[" + table_name + "](id:" + str(table_id) + ")"
    # While schleife bauen, um alle Permissions, ab Permission X durchzugehen und die passende zu finden. 
    i = 198 #First own Produced Dataset
    found = False

    while found == False:
        i += 1
        request = session.get(f'{url}/api/v1/security/permissions-resources/{i}', headers=headers)
        view_menu_name = request.json()['result']['view_menu']['name']
        if name == view_menu_name:
            found = True
    return i

#TODO Permission for SQLLAB
import requests
"""
    Hier werden Datasets für die einzelnen User erstellt und geupdatet
    Die Datasets werden den Namen: username_tablename haben
    Bsp. für Tabelle base_session: admin_base_session

    Auf Basis dieser werden nun danach Dashboards erstellt, für die Experimente der Researcher
"""
#TODO First table normally, seconde does not have session_id, third...
#TODO other tables
table_names = ['base_spo2', 'base_ppg_red', 'base_ppg_ir', 'base_ppg_green', 'base_heart_rate', 
    'base_ecg', 'base_answers', 'base_accelerometer']
table_connected = ['base_experiment_watch_id', 'base_experiment_questions', 'base_questions']
special_tables = ['base_experiment','base_session']

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

#TODO ohne Session machen und Session aus api.py nutzen
def create_datasets(user_id, role_id, superset_id):
    session = requests.session()
    headers = get_header(session)
    username_request = session.get(f'{url}/api/v1/security/users/' + str(2), headers=headers)
    username = username_request.json()['result']['username']
    permissions = []
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
                2       
            ],
            "schema": "public",
            "sql": sql,
            "table_name": username + '_' + name
        })
        #TODO testen, aber vorher das Problem ungleich user-ids lösen
        permissions.add(getPermissionID(session, headers, name, dataset.json()[id]))
    session.post(f'{url}/api/v1/security/roles/{role_id}/permissions', headers=headers, json={
        "permission_view_menu_ids": permissions.json()
    })
    #TODO bereits existierende Rollen

sql_test = "Select * FROM base_ppg_green WHERE session_id IN (SELECT base_session.id as session_id FROM base_session JOIN base_experiment ON base_experiment.id = base_session.experiment_id WHERE created_by_id = 1)"

def test():
    session = requests.session()
    headers = get_header(session)
    sql = get_sql('base_ppg_green', '1')
    testing = session.post(f'{url}/api/v1/dataset/', headers=headers, json={
        "always_filter_main_dttm": False,
        "database": 1,
        "external_url": '',
        "is_managed_externally": True,
        "normalize_columns": False,
        "owners": [
            1       
        ],
        "schema": "public",
        "sql": sql,
        "table_name": "admin" + "_" + 'base_ppg_green'
    })

    session.close()
#TODO muss vielleicht nur einmal aufgerufen werden, danach +1 auf die INdizes -> Gefahr bei Parallelerstellung
def getPermissionID(session, headers, table_name, table_id):
    # Default name of the permission "name": "[PostgreSQL].[Guest_base_spo2](id:22)"
    name = "[PostgreSQL].[" + table_name + "](id:" + str(table_id) + ")"
    # While schleife bauen, um alle Permissions, ab Permission X durchzugehen und die passende zu finden. 
    i = 198
    found = False

    while found == False:
        i += 1
        request = session.get(f'{url}/api/v1/security/permissions-resources/{i}', headers=headers)
        view_menu_name = request.json()['result']['view_menu']['name']
        if name == view_menu_name:
            found = True
    #TODO Vlt lässt sich über die Table id die Permission id berechnen

    return i


#TODIO USer ID in Backen is different to USerID in SUperset
session = requests.session()
print(getPermissionID(session, get_header(session), 'Guest_base_spo2', 22))
session.close()

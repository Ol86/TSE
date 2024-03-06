import requests
import json


table_names = ['base_spo2', 'base_ppg_red', 'base_ppg_ir', 'base_ppg_green', 'base_heart_rate', 
    'base_ecg', 'base_answers', 'base_accelerometer']
table_connected = ['base_experiment_watch_id', 'base_experiment_questions', 'base_session']
table_questions = ['base_questions', 'base_questionanswers'] 
special_tables = ['base_experiment']

url = 'http://193.196.36.62:8088'

def get_header(session):
    """ This method creates a header for the current request session.

    :param session: the current request session
    :return: the header of an api call containing auth-token and csrf-token
    """
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
    """This method creates sql queries for some tables.
        the sql query looks like this:
        Select *
        FROM 'name'
        WHERE session_id IN
        (SELECT base_session.id as session_id
        FROM base_session JOIN base_experiment ON base_experiment.id = base_session.experiment_id
        where created_by_id = 'user_id')

        :param name: the table name 
        :param user_id: the id of the backend user-management
        :return: the sql query for the specific table
    """
    #TODO Zeile zu lang
    sql = "Select * FROM " + name + " WHERE session_id IN (SELECT base_session.id as session_id FROM base_session JOIN base_experiment ON base_experiment.id = base_session.experiment_id WHERE created_by_id = " + str(user_id) + ")"
    return sql

def get_sql_two(name, user_id):
    """This method creates sql queries for some tables.
        the sql query looks like this:
        SELECT * FROM 'name' 
        where experiment_id IN 
        (Select base_experiment.id as experiment_id
        From base_experiment where created_by_id = 'user_id')

        :param name: the table name 
        :param user_id: the id of the backend user-management
        :return: the sql query for the specific table
    """
    sql = "SELECT * From " + name + " where experiment_id IN (Select base_experiment.id as experiment_id From base_experiment where created_by_id = " + str(user_id) + " )"
    return sql

def create_one(permissions, user_id, superset_id, session, headers, username):
    """In this method, the dataset for all tables from table_names are created
    
    :param permissions: a list containing all acces permissions that later get added to the user specific role
    :param user_id: the user id from the backend user-management
    :param superset_id: the user id in apache superset
    :param session: the current request session
    :param headers: the sessions header
    :param username: the username of the current user
    """
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

    for name in table_questions:
        sql = "SELECT * FROM " + name
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

def create_two(permissions, user_id, superset_id, session, headers, username):
    """In this method, the dataset for all tables from tables_connected are created
    
    :param permissions: a list containing all acces permissions that later get added to the user specific role
    :param user_id: the user id from the backend user-management
    :param superset_id: the user id in apache superset
    :param session: the current request session
    :param headers: the sessions header
    :param username: the username of the current user
    """
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
    """In this method, the dataset for all tables from special_tables are created
    
    :param permissions: a list containing all acces permissions that later get added to the user specific role
    :param user_id: the user id from the backend user-management
    :param superset_id: the user id in apache superset
    :param session: the current request session
    :param headers: the sessions header
    :param username: the username of the current user
    """
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

def create_datasets(user_id, role_id, superset_id):
    """This is the main method of this generation process.
    Here all the other methods are called and prepared

    :param user_id: the user id from the backend user-management
    :param role_id: the id of the users own role
    :param superset_id: the user id in apache superset
    """
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
    """This method finds the permission id of an specific dataset acces
    This is needed because the api does not return this id in any specific way. It only returns them all, the be iterated through.

    :param session: the current request session
    :param header: the sessions header
    :param table_name: the table name of the needed acces permission
    :param table_id: the table id
    :return: the permission id
    """
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

def updateDatasets():
    """In this method we update datasets.
    This is needed because at the creation start they are empty. 
    The url of the result request just means that we extend the amound of datasets returned to us to the amound of datasets that exist.
    """
    session = requests.session()
    datasets = session.get(f'{url}/api/v1/dataset/', headers=get_header(session))
    result = session.get(f'{url}/api/v1/dataset/?q=%7B%22page_size%22%3A%20' + str(datasets.json()['count']) + '%7D', headers=get_header(session)).json()['ids']
    for i in result:
        session.put(f'{url}/api/v1/dataset/{i}/refresh', headers=get_header(session))


def createDataset(url, csrf_token, auth_token, session):
    """This method creates a needed dataset for the dashboard creation in the file dashboard.py

    :param url: the auth token
    :param csrf_token: the csrf token
    :param session: the current request session
    :return: the newly created dataset
    """
    headers = {
        'Authorization': f'Bearer {auth_token}',
        'X-CSRFToken': csrf_token,
    }

    body = {
        "always_filter_main_dttm": False,
        "database": 1,
        "external_url": "string",
        "is_managed_externally": True,
        "normalize_columns": False,
        "owners": [
            1
        ],
        "schema": "public",
        "sql": "SELECT hr, TIMESTAMP, watch_id\nFROM session_data\nJOIN heart_rate_measurement ON session_data.id = heart_rate_measurement.session_id\nWHERE session_id = 1 and hr_status= 1",
        "table_name": "test"
    }

    dataset_resp = session.post(f'{url}/api/v1/dataset/', headers=headers, json=body)
    dataset = dataset_resp.json()
    return dataset


def createDataset_answers(url, session, headers, owner):
    """This method creates a needed dataset for the dashboard creation in the file dashboard.py

    :param url: the auth token
    :param headers: the sessions header
    :param session: the current request session
    :param owner: the owner of the dataset
    :return: the newly created dataset
    """
    body = {
        "always_filter_main_dttm": False,
        "database": 1,
        "external_url": '',
        "is_managed_externally": True,
        "normalize_columns": False,
        "owners": [
            1 #TODO alternative owner.get('id')
        ],
        "schema": "public",
        "sql": "SELECT base_questionanswers.id AS answer_id, answer, question_id, experiment_id, session_id FROM base_questionanswers JOIN base_answers ON base_questionanswers.id = base_answers.answer_id where session_id IN (SELECT base_session.id as session_id FROM base_session JOIN base_experiment ON base_experiment.id = base_session.experiment_id where created_by_id = " + str(owner.get('id')) + ")",
        "table_name": owner.get('last_name') + "_base_answers_ids"
    }
    dataset_resp = session.post(f'{url}/api/v1/dataset/', headers=headers, json=body)
    dataset = dataset_resp.json()
    return dataset


def createDataset_poincare(url, session, headers, owner):
    """This method creates a needed dataset for the dashboard creation in the file dashboard.py

    :param url: the auth token
    :param headers: the sessions header
    :param session: the current request session
    :param owner: the owner of the dataset
    :return: the newly created dataset
    """
    body = {
        "always_filter_main_dttm": False,
        "database": 1,
        "external_url": "string",
        "is_managed_externally": True,
        "normalize_columns": False,
        "owners": [
            owner.get('id')
        ],
        "schema": "public",
        # TODO change the table names here with the ones with the integrated owner id in the SQL table names
        "sql": "SELECT session_id, id, ibi AS current, next FROM (SELECT session_id, id, ibi, LEAD(ibi) OVER (ORDER BY id) AS next FROM base_heart_rate WHERE ibi_status = 0 AND hr_status = 1 AND ibi < 2000) AS subquery WHERE next < 2000 AND session_id IN (SELECT base_session.id as session_id FROM base_session JOIN base_experiment ON base_experiment.id = base_session.experiment_id WHERE created_by_id = " + str(owner.get('id')) + ")",
        "table_name":  owner.get('last_name')+ "_poincare_data"
    }
    dataset_resp = session.post(f'{url}/api/v1/dataset/', headers=headers, json=body)
    dataset = dataset_resp.json()
    return dataset

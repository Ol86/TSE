import json
import requests

import charts as c

from dataset import createDataset_answers
from dataset import createDataset_poincare

## ----------------------------------------------------------------
# TODO test if working with the owner parameter
def createStandarizedDashboard(headers, session, owner):
    """This method is the main focus point of this generation process
    
    :param headers: The sessions header
    :param session: The current request session
    :param owner: the owner of the dashboard
    :return: The newly created dashboard
    """
    url = 'http://193.196.36.62:8088'
    body = {
        "certification_details": "string",
        "certified_by": "ale",
        "css": "string",
        "dashboard_title": "Standardized Dashboard of " + owner.get('last_name'),
        "external_url": "string",
        "is_managed_externally": True,
        "json_metadata": "",
        "owners": [
            owner.get('id')
        ],
        "position_json": "",
        "published": True,
        "roles": [
            owner.get('id')
        ],
        "slug": owner.get('first_name') + ' ' + owner.get('last_name')
    }

    dashboard_res = session.post(f'{url}/api/v1/dashboard/', headers=headers, json=body)

    # TODO simplify the number of parameters required (csfr_token and auth token can be get from the header)
    c.createChart_numberOfSessions(url, session, headers, owner)
    c.createChart_experimentInformation(url, session, headers, owner)
    c.createChart_questions(url, session, headers, owner)
    c.createChart_numberOfWatches(url, session, headers, owner)
    c.createChart_hr(url, session, headers, owner)
    createDataset_answers(url, session, headers, owner)
    c.createChart_answers(url, session, headers, owner)
    createDataset_poincare(url, session, headers, owner)
    c.createChart_poincare(url, session, headers, owner)

    updateDashboard(url, session, headers, owner)
    dashboard = dashboard_res.json()
    return dashboard


def getDashboards(url, auth_token):
    """This Method requests the dashboards from superset

    :param url: the api url
    :param auth_token: the auth token
    :return: a list containing all dashboards
    """
    headers = {'Authorization': f'Bearer {auth_token}'}
    dashboards_res = requests.get(f'{url}/api/v1/dashboard/', headers=headers)
    dashboards = dashboards_res.json()
    return dashboards


def getDatasets(url, auth_token, session):
    """This methods requests all datasets from superset

    :param url: the api url
    :param auth_token: the auth token
    :param session: the current session
    :return: a list containing all datasets
    """
    headers = {'Authorization': f'Bearer {auth_token}'}
    datasets = session.get(f'{url}/api/v1/dataset/', headers=headers)
    result = session.get(f'{url}/api/v1/dataset/?q=%7B%22page_size%22%3A%20' + str(datasets.json()['count']) + '%7D', headers=headers).json()
    return result


def createDashboard(url, csrf_token, auth_token, session):
    """This method creates a new dashboard

    :param url: the api url
    :param csrf_token: the csrf token
    :param auth_token: the auth token
    :param session: the current session
    :return: the newly created dashboard
    """
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

    dashboard_res = session.post(f'{url}/api/v1/dashboard/', headers=headers, json=body)
    dashboard = dashboard_res.json()
    return dashboard


def updateDashboard(url, session, headers, owner):
    """This method updates the dashboard to make sure the new charts are on it

    :param url: the api url
    :param session: the current session
    :param headers: the header for the api calls
    :param owner: the dashboard owner
    :return: the newly updated dashboard
    """
    auth_token = headers['Authorization'].split('Bearer ')[1]

    ids_to_update = get_charts_from_dashboard(c.getCharts(url, auth_token),
                                              get_dashboard_id(getDashboards(url, auth_token),
                                                               "Standardized Dashboard of " + owner.get('last_name')))

    body = {
        "certification_details": "string",
        "certified_by": "ale",
        "css": "string",
        "dashboard_title": "Standarized Dashboard of " + owner.get('last_name') ,
        "external_url": "string",
        "is_managed_externally": True,
        "json_metadata": "",
        "owners": [owner.get('id')],
        "position_json": "{\"CHART-explore-1-1\":{\"children\":[],\"id\":\"CHART-explore-1-1\",\"meta\":{\"chartId\":" + str(
            str(ids_to_update.get(
                'Experiment-Information'))) + ",\"height\":50,\"sliceName\":\"Experiment-Information\",\"uuid\":\"5b1858ad-fd96-4b11-99c4-21961b3f2099\",\"width\":12},\"parents\":[\"ROOT_ID\",\"GRID_ID\",\"ROW-wha2uhAcH\"],\"type\":\"CHART\"},"
                                              "\"CHART-explore-11-1\":{\"children\":[],\"id\":\"CHART-explore-11-1\",\"meta\":{\"chartId\":" + str(
            str(ids_to_update.get(
                'Number of Watches'))) + ",\"height\":22,\"sliceName\":\"Number of Watches\",\"uuid\":\"1de52f53-5978-4709-bfdf-55c6dbbdf724\",\"width\":6},\"parents\":[\"ROOT_ID\",\"GRID_ID\",\"ROW-FJvxmMWbc-\"],""\"type\":\"CHART\"},"
                                         "\"CHART-explore-2-1\":{\"children\":[],\"id\":\"CHART-explore-2-1\",\"meta\":{\"chartId\":" + str(
            str(ids_to_update.get(
                'Number of Sessions'))) + ",\"height\":22,\"sliceName\":\"Number of Sessions\",\"uuid\":\"6be18e2a-2cc7-4e1e-8b8d-f4f229b1e53a\",\"width\":6},\"parents\":[\"ROOT_ID\",\"GRID_ID\",""\"ROW-5he0CpoIb\"],\"type\":\"CHART\"},"
                                          "\"CHART-explore-4-1\":{\"children\":[],\"id\":\"CHART-explore-4-1\",\"meta\":{\"chartId\":" + str(
            str(ids_to_update.get(
                'Questions'))) + ",\"height\":22,\"sliceName\":\"Questions\",\"uuid\":\"be9cba5c-be63-42cb-acf3-3be1fc8dd457\",\"width\":12},\"parents\":[\"ROOT_ID\","
                                 "\"GRID_ID\",\"ROW-_gNAFYXaE8\"],\"type\":\"CHART\"},\"DASHBOARD_VERSION_KEY\":\"v2\",\"GRID_ID\":{\"children\":[\"ROW-wha2uhAcH\",\"ROW-_gNAFYXaE8\",\"ROW-yEtOk-uKc\",\"ROW-5he0CpoIb\"],\"id\":\"GRID_ID\",\"parents\":[\"ROOT_ID\"],\"type\":\"GRID\"},"
                                 "\"HEADER_ID\":{\"id\":\"HEADER_ID\",\"meta\":{\"text\":\"Standarsized Dashboard\"},\"type\":\"HEADER\"},\"ROOT_ID\":{\"children\":[\"GRID_ID\"],\"id\":\"ROOT_ID\",\"type\":\"ROOT\"},\"ROW-5he0CpoIb\":{\"children\":[\"CHART-explore-2-1\",\"CHART-explore-11-1\"],"
                                 "\"id\":\"ROW-5he0CpoIb\",\"meta\":{\"background\":\"BACKGROUND_TRANSPARENT\"},\"parents\":[\"ROOT_ID\",\"GRID_ID\"],\"type\":\"ROW\"},\"ROW-_gNAFYXaE8\":{\"children\":[\"CHART-explore-4-1\"],\"id\":\"ROW-_gNAFYXaE8\",\"meta\":{\"0\":\"ROOT_ID\",\"background\":\"BACKGROUND_TRANSPARENT\"},"
                                 "\"parents\":[\"ROOT_ID\",\"GRID_ID\"],\"type\":\"ROW\"},\"ROW-wha2uhAcH\":{\"children\":[\"CHART-explore-1-1\"],\"id\":\"ROW-wha2uhAcH\",\"meta\":{\"0\":\"ROOT_ID\",\"background\":\"BACKGROUND_TRANSPARENT\"},\"parents\":[\"ROOT_ID\",\"GRID_ID\"],\"type\":\"ROW\"},"
                                 "\"ROW-yEtOk-uKc\":{\"children\":[\"CHART-explore-7-1\"],\"id\":\"ROW-yEtOk-uKc\",\"meta\":{\"0\":\"ROOT_ID\",\"background\":\"BACKGROUND_TRANSPARENT\"},\"parents\":[\"ROOT_ID\",\"GRID_ID\"],\"type\":\"ROW\"}}",
        "published": True,
        "roles": [owner.get('id')],
        "slug": owner.get('first_name') + ' ' + owner.get('last_name')

    }

    dashboard_res = session.put(
        f'{url}/api/v1/dashboard/' + str(get_dashboard_id(getDashboards(url, auth_token), "Standardized Dashboard of " + owner.get('last_name'))),
        headers=headers, json=body)
    dashboard = dashboard_res.json()
    return dashboard


# ---------- utils ------------------------------------------

##
# gets the chart info from a specific dashboard
def get_charts_from_dashboard(chart_json, dashboard_id):
    """This method gives out all the charts from the dashboard

    :param chart_json: A list containing charts
    :param dashboard_id: the dashboard id
    :return: A list with all the charts
    """
    charts_from_dash = {}
    for chart in chart_json["result"]:
        for dashboards in chart['dashboards']:
            if dashboards['id'] == dashboard_id:
                charts_from_dash.update(
                    {
                        chart['slice_name']: chart['id']

                    }
                )
    print(charts_from_dash)
    return charts_from_dash


def get_dashboard_id(dashboards, title):
    """This method returns the dashboard id
    
    :param dashboards: A list with all dashboards
    :param title: The dashboard title
    :return: the dashboard id or -1 if a error has occured
    """
    for dashboard in dashboards['result']:
        if dashboard['dashboard_title'] == title:
            return dashboard['id']
    print('No dashboard with title', title)
    return -1


def get_datasource_id(datasets, table_name):
    """This method gets a datasource id
    :param datasets: A list containing all datasets
    :param table_name: the table name
    :return: the dataset id or -1 if an error has occured
    """
    for dataset in datasets['result']:
        if dataset['table_name'] == table_name:
            return dataset['id']
    print('No datasource with table name', table_name)
    return -1

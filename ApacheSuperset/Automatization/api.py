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
    return dashboards


def getCharts(url, auth_token):
    headers = {'Authorization': f'Bearer {auth_token}'}
    params = {"limit": 100}
    charts_res = requests.get(f'{url}/api/v1/chart/', headers=headers, params=params)
    charts = charts_res.json()
    saveToFile(json.dumps(charts, indent=4))
    return charts


def getDatasets(url, auth_token, session):
    headers = {'Authorization': f'Bearer {auth_token}'}
    datasets = session.get(f'{url}/api/v1/dataset/', headers=headers)
    result = session.get(f'{url}/api/v1/dataset/?q=%7B%22page_size%22%3A%20' + str(datasets.json()['count']) + '%7D', headers=headers).json()
    saveToFile(json.dumps(result, indent=4))
    return result


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

    dashboard_res = session.post(f'{url}/api/v1/dashboard/', headers=headers, json=body)
    dashboard = dashboard_res.json()
    saveToFile(json.dumps(dashboard, indent=4))
    return dashboard


def updateDashboard(url, session, headers, owner):

    auth_token = headers['Authorization'].split('Bearer ')[1]

    ids_to_update = get_charts_from_dashboard(getCharts(url, auth_token),
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
    saveToFile(json.dumps(dashboard, indent=4))
    return dashboard


def createChart(url, csrf_token, auth_token, session):
    headers = {
        'Authorization': f'Bearer {auth_token}',
        'X-CSRFToken': csrf_token,
    }

    body = {
        "cache_timeout": 0,
        "certification_details": None,
        "certified_by": None,
        "dashboards": [
            2
        ],
        "datasource_id": 5,
        "datasource_name": "heart_rate_measurement",
        "datasource_type": "table",
        "description": None,
        "is_managed_externally": False,
        "owners": [
            1
        ],
        "params": "{\"datasource\":\"5__table\",\"viz_type\":\"echarts_timeseries_line\",\"slice_id\":2,"
                  "\"x_axis\":\"timestamp\",\"time_grain_sqla\":\"P1D\",\"x_axis_sort_asc\":true,"
                  "\"x_axis_sort_series\":\"name\",\"x_axis_sort_series_ascending\":true,\"metrics\":[{"
                  "\"aggregate\":\"AVG\",\"column\":{\"advanced_data_type\":null,\"certification_details\":null,"
                  "\"certified_by\":null,\"column_name\":\"hr\",\"description\":null,\"expression\":null,"
                  "\"filterable\":true,\"groupby\":true,\"id\":32,\"is_certified\":false,\"is_dttm\":false,"
                  "\"python_date_format\":null,\"type\":\"BIGINT\",\"type_generic\":0,\"verbose_name\":null,"
                  "\"warning_markdown\":null},\"datasourceWarning\":false,\"expressionType\":\"SIMPLE\","
                  "\"hasCustomLabel\":false,\"label\":\"AVG(hr)\",\"optionName\":\"metric_0tsmc0mjqzne_1oh7icdr566\","
                  "\"sqlExpression\":null}],\"groupby\":[],\"adhoc_filters\":[],\"order_desc\":true,"
                  "\"row_limit\":10000,\"truncate_metric\":true,\"show_empty_columns\":true,"
                  "\"comparison_type\":\"values\",\"annotation_layers\":[],\"forecastPeriods\":10,"
                  "\"forecastInterval\":0.8,\"x_axis_title_margin\":15,\"y_axis_title_margin\":15,"
                  "\"y_axis_title_position\":\"Left\",\"sort_series_type\":\"sum\","
                  "\"color_scheme\":\"supersetColors\",\"seriesType\":\"line\",\"only_total\":true,\"opacity\":0.2,"
                  "\"markerSize\":6,\"show_legend\":true,\"legendType\":\"scroll\",\"legendOrientation\":\"top\","
                  "\"x_axis_time_format\":\"smart_date\",\"rich_tooltip\":true,\"tooltipTimeFormat\":\"smart_date\","
                  "\"y_axis_format\":\"SMART_NUMBER\",\"y_axis_bounds\":[null,null],\"extra_form_data\":{},"
                  "\"dashboards\":[2]}",
        "query_context": None,
        "query_context_generation": True,
        "slice_name": "Ale",
        "viz_type": "echarts_timeseries_line"
    }

    chart_resp = session.post(f'{url}/api/v1/chart/', headers=headers, json=body)
    chart = chart_resp.json()
    saveToFile(json.dumps(chart, indent=4))
    return chart


def createDataset(url, csrf_token, auth_token, session):
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
    saveToFile(json.dumps(dataset, indent=4))
    return dataset


## ----------------------------------------------------------------
# TODO test if working with the owner parameter
def createStandarizedDashboard(url, csrf_token, auth_token, session, owner):
    headers = {
        'Authorization': f'Bearer {auth_token}',
        'X-CSRFToken': csrf_token,
    }

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
    createChart_numberOfSessions(url, session, headers, owner)
    createChart_experimentInformation(url, session, headers, owner)
    createChart_questions(url, session, headers, owner)
    createChart_numberOfWatches(url, session, headers, owner)
    createChart_hr(url, session, headers, owner)
    createDataset_answers(url, session, headers, owner)
    createChart_answers(url, session, headers, owner)
    createDataset_poincare(url, session, headers, owner)
    createChart_poincare(url, session, headers, owner)

    updateDashboard(url, session, headers, owner)
    dashboard = dashboard_res.json()
    saveToFile(json.dumps(dashboard, indent=4))
    return dashboard


def createChart_numberOfSessions(url, session, headers, owner):

    auth_token = headers['Authorization'].split('Bearer ')[1]

    body = {
        "cache_timeout": 0,
        "certification_details": None,
        "certified_by": None,
        "dashboards": [
            get_dashboard_id(getDashboards(url, auth_token), "Standardized Dashboard of " + owner.get('last_name'))
        ],
        "datasource_id": get_datasource_id(getDatasets(url, auth_token, session), owner.get('last_name') + "_base_session"),
        "datasource_name": owner.get('last_name') + "_base_session",
        "datasource_type": "table",
        "description": None,
        "is_managed_externally": False,
        "owners": [
            owner.get('id')
        ],
        "params": "{\"datasource\": \"38__table\","
                  " \"viz_type\": \"big_number_total\","
                  " \"metric\": {\"expressionType\": \"SQL\","
                  " \"sqlExpression\": \"COUNT (DISTINCT id)\","
                  " \"column\": null, \"aggregate\": null,"
                  " \"datasourceWarning\": false,"
                  " \"hasCustomLabel\": false, \"label\": \"COUNT (DISTINCT id)\","
                  " \"optionName\": \"metric_j5v1rn9agcj_hn4lm8sci\"},"
                  " \"adhoc_filters\": [{\"clause\": \"WHERE\", "
                  "\"subject\": \"created_at\", \"operator\": \"TEMPORAL_RANGE\","
                  " \"comparator\": \"No filter\", \"expressionType\": \"SIMPLE\"}],"
                  " \"header_font_size\": 0.4, \"subheader_font_size\": 0.15,"
                  " \"y_axis_format\": \"SMART_NUMBER\","
                  " \"time_format\": \"smart_date\", \"extra_form_data\": {},"
                  " \"dashboards\": [1]}",
        "query_context": None,
        "query_context_generation": True,
        "slice_name": "Number of Sessions",
        "viz_type": "big_number_total"
    }

    chart_resp = session.post(f'{url}/api/v1/chart/', headers=headers, json=body)
    chart = chart_resp.json()
    saveToFile(json.dumps(chart, indent=4))
    return chart


def createChart_numberOfWatches(url, session, headers, owner):

    auth_token = headers['Authorization'].split('Bearer ')[1]

    body = {
        "cache_timeout": 0,
        "certification_details": None,
        "certified_by": None,
        "dashboards": [
            get_dashboard_id(getDashboards(url, auth_token), "Standardized Dashboard of " + owner.get('last_name'))],
        "datasource_id": get_datasource_id(getDatasets(url, auth_token, session), owner.get('last_name') + "_base_experiment_watch_id"),
        "datasource_name": "base_experiment_watch_id",
        "datasource_type": "table",
        "description": None,
        "is_managed_externally": False,
        "owners": [
            owner.get('id')
        ],
        "params": "{\"datasource\":\"41__table\",\"viz_type\":\"big_number_total\","
                  "\"slice_id\":11,\"metric\":{\"aggregate\":\"COUNT_DISTINCT\","
                  "\"column\":{\"advanced_data_type\":null,\"certification_details\":null,"
                  "\"certified_by\":null,\"column_name\":\"watch_id\",\"description\":null,"
                  "\"expression\":null,\"filterable\":true,\"groupby\":true,\"id\":36,"
                  "\"is_certified\":false,\"is_dttm\":false,\"python_date_format\":null,"
                  "\"type\":\"BIGINT\",\"type_generic\":0,\"verbose_name\":null,"
                  "\"warning_markdown\":null},\"datasourceWarning\":false,"
                  "\"expressionType\":\"SIMPLE\",\"hasCustomLabel\":false,"
                  "\"label\":\"COUNT_DISTINCT(watch_id)\",\"optionName\":\"metric_pq1hlizzg9_j76rhk4tjy\","
                  "\"sqlExpression\":null},\"adhoc_filters\":[],\"header_font_size\":0.4,"
                  "\"subheader_font_size\":0.15,\"y_axis_format\":\"SMART_NUMBER\","
                  "\"time_format\":\"smart_date\",\"extra_form_data\":{},\"dashboards\":[2]}",
        "query_context": None,
        "query_context_generation": True,
        "slice_name": "Number of Watches",
        "viz_type": "big_number_total"
    }

    chart_resp = session.post(f'{url}/api/v1/chart/', headers=headers, json=body)
    chart = chart_resp.json()
    saveToFile(json.dumps(chart, indent=4))
    return chart


def createChart_experimentInformation(url,session, headers, owner):

    auth_token = headers['Authorization'].split('Bearer ')[1]

    body = {
        "cache_timeout": 0,
        "certification_details": None,
        "certified_by": None,
        "dashboards": [
            get_dashboard_id(getDashboards(url, auth_token), "Standardized Dashboard of " + owner.get('last_name'))],
        "datasource_id": get_datasource_id(getDatasets(url, auth_token, session), owner.get('last_name') + "_base_experiment"),
        "datasource_name": owner.get('last_name') + "_base_experiment",
        "datasource_type": "table",
        "description": None,
        "is_managed_externally": False,
        "owners": [
            owner.get('id')
        ],
        "params": "{\"datasource\":\"37__table\",\"viz_type\":\"table\",\"slice_id\":1,"
                  "\"query_mode\":\"aggregate\",\"groupby\":[\"id\",\"title\",\"spo2\","
                  "\"heart_rate\",\"sweat_loss\",\"bia\",\"accelerometer\",\"ecg\"],"
                  "\"time_grain_sqla\":\"P1D\",\"temporal_columns_lookup\":{\"created_at\":true},"
                  "\"all_columns\":[],\"percent_metrics\":[],\"adhoc_filters\":[{\"clause\":\"WHERE\","
                  "\"comparator\":\"No filter\",\"expressionType\":\"SIMPLE\",\"operator\":\"TEMPORAL_RANGE\","
                  "\"subject\":\"created_at\"}],\"order_by_cols\":[],\"row_limit\":1000,\"server_page_length\":10,"
                  "\"order_desc\":true,\"table_timestamp_format\":\"smart_date\",\"show_cell_bars\":true,"
                  "\"color_pn\":true,\"extra_form_data\":{},\"dashboards\":[2]}",
        "query_context": None,
        "query_context_generation": True,
        "slice_name": "Experiment-Information",
        "viz_type": "table"
    }

    chart_resp = session.post(f'{url}/api/v1/chart/', headers=headers, json=body)
    chart = chart_resp.json()
    saveToFile(json.dumps(chart, indent=4))
    return chart


def createChart_questions(url, session, headers, owner):

    auth_token = headers['Authorization'].split('Bearer ')[1]

    body = {
        "cache_timeout": 0,
        "certification_details": None,
        "certified_by": None,
        "dashboards": [
            get_dashboard_id(getDashboards(url, auth_token), "Standardized Dashboard of " + owner.get('last_name'))],
        "datasource_id": get_datasource_id(getDatasets(url, auth_token, session), owner.get('last_name') + "_base_questions"),
        "datasource_name": owner.get('last_name') + "_base_questions",
        "datasource_type": "table",
        "description": None,
        "is_managed_externally": False,
        "owners": [
            owner.get('id')
        ],
        "params": "{\"datasource\":\"13__table\",\"viz_type\":\"table\",\"query_mode\":\"aggregate\",\"groupby\":[\"id\",\"question\"],\"time_grain_sqla\":\"P1D\",\"temporal_columns_lookup\":{\"created_at\":true},\"all_columns\":[],\"percent_metrics\":[],\"adhoc_filters\":[{\"clause\":\"WHERE\",\"subject\":\"created_at\",\"operator\":\"TEMPORAL_RANGE\",\"comparator\":\"No filter\",\"expressionType\":\"SIMPLE\"}],\"order_by_cols\":[],\"row_limit\":1000,\"server_page_length\":10,\"order_desc\":true,\"table_timestamp_format\":\"smart_date\",\"show_cell_bars\":true,\"color_pn\":true,\"extra_form_data\":{},\"dashboards\":[]}",
        "query_context": None,
        "query_context_generation": True,
        "slice_name": "Questions",
        "viz_type": "table"
    }

    chart_resp = session.post(f'{url}/api/v1/chart/', headers=headers, json=body)
    chart = chart_resp.json()
    saveToFile(json.dumps(chart, indent=4))
    return chart


def createChart_hr(url, session, headers, owner):

    auth_token = headers['Authorization'].split('Bearer ')[1]

    body = {
        "cache_timeout": 0,
        "certification_details": None,
        "certified_by": None,
        "dashboards": [
            get_dashboard_id(getDashboards(url, auth_token), "Standardized Dashboard of " + owner.get('last_name'))
        ],
        "datasource_id": get_datasource_id(getDatasets(url, auth_token, session), owner.get('last_name') + "_base_heart_rate"),
        "datasource_name": owner.get('last_name') + "_base_heart_rate",
        "datasource_type": "table",
        "description": None,
        "is_managed_externally": False,
        "owners": [
            owner.get('id')
        ],
        "params": "{\"datasource\": \"2__table\", \"viz_type\": \"echarts_timeseries_line\", \"slice_id\": 6, \"x_axis\": \"time\", \"time_grain_sqla\": \"PT1S\", \"x_axis_sort_asc\": true, \"x_axis_sort_series\": \"name\", \"x_axis_sort_series_ascending\": true, \"metrics\": [{\"aggregate\": \"MAX\", \"column\": {\"advanced_data_type\": null, \"changed_on\": \"2024-02-14T19:43:00.152158\", \"column_name\": \"hr\", \"created_on\": \"2024-02-14T19:43:00.152154\", \"description\": null, \"expression\": null, \"extra\": null, \"filterable\": true, \"groupby\": true, \"id\": 42, \"is_active\": true, \"is_dttm\": false, \"python_date_format\": null, \"type\": \"INTEGER\", \"type_generic\": 0, \"uuid\": \"83368586-bca2-4e15-848a-bd5cad1ce618\", \"verbose_name\": null}, \"datasourceWarning\": false, \"expressionType\": \"SIMPLE\", \"hasCustomLabel\": false, \"label\": \"MAX(hr)\", \"optionName\": \"metric_5beddswab5c_euugrap8r2t\", \"sqlExpression\": null}], \"groupby\": [\"session_id\"], \"adhoc_filters\": [{\"clause\": \"WHERE\", \"comparator\": \"No filter\", \"expressionType\": \"SIMPLE\", \"operator\": \"TEMPORAL_RANGE\", \"subject\": \"time\"}], \"order_desc\": true, \"row_limit\": 10000, \"truncate_metric\": true, \"show_empty_columns\": true, \"comparison_type\": \"values\", \"annotation_layers\": [], \"forecastPeriods\": 10, \"forecastInterval\": 0.8, \"x_axis_title_margin\": 15, \"y_axis_title_margin\": 15, \"y_axis_title_position\": \"Left\", \"sort_series_type\": \"sum\", \"color_scheme\": \"supersetColors\", \"seriesType\": \"line\", \"only_total\": true, \"opacity\": 0.2, \"markerSize\": 6, \"zoomable\": true, \"show_legend\": true, \"legendType\": \"scroll\", \"legendOrientation\": \"top\", \"x_axis_time_format\": \"%H:%M:%S\", \"rich_tooltip\": true, \"tooltipTimeFormat\": \"%Y-%m-%d %H:%M:%S\", \"y_axis_format\": \"SMART_NUMBER\", \"truncateXAxis\": true, \"y_axis_bounds\": [null, null], \"extra_form_data\": {}, \"dashboards\": [1]}",
        "query_context": None,
        "query_context_generation": True,
        "slice_name": "Heart Rate by Time",
        "viz_type": "echarts_timeseries_line"
    }

    chart_resp = session.post(f'{url}/api/v1/chart/', headers=headers, json=body)
    chart = chart_resp.json()
    saveToFile(json.dumps(chart, indent=4))
    return chart


def createDataset_answers(url, session, headers, owner):

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
    saveToFile(json.dumps(dataset, indent=4))
    return dataset


def createDataset_poincare(url, session, headers, owner):

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
    saveToFile(json.dumps(dataset, indent=4))
    return dataset


def createChart_answers(url, session, headers, owner):

    auth_token = headers['Authorization'].split('Bearer ')[1]

    body = {
        "cache_timeout": 0,
        "certification_details": None,
        "certified_by": None,
        "dashboards": [
            get_dashboard_id(getDashboards(url, auth_token), "Standardized Dashboard of " + owner.get('last_name'))
        ],
        "datasource_id": get_datasource_id(getDatasets(url, auth_token, session), owner.get('last_name') + "_base_answers_ids"),
        "datasource_name": owner.get('last_name') + "_base_answers_ids",
        "datasource_type": "table",
        "description": None,
        "is_managed_externally": False,
        "owners": [
            owner.get('id')
        ],
        "params": "{\"datasource\":\"18__table\",\"viz_type\":\"pie\",\"slice_id\":128,\"groupby\":[\"answer\",\"question_id\"],\"metric\":{\"aggregate\":\"COUNT\",\"column\":{\"advanced_data_type\":null,\"certification_details\":null,\"certified_by\":null,\"column_name\":\"answer_id\",\"description\":null,\"expression\":null,\"filterable\":true,\"groupby\":true,\"id\":95,\"is_certified\":false,\"is_dttm\":false,\"python_date_format\":null,\"type\":\"INTEGER\",\"type_generic\":0,\"verbose_name\":null,\"warning_markdown\":null},\"datasourceWarning\":false,\"expressionType\":\"SIMPLE\",\"hasCustomLabel\":false,\"label\":\"COUNT(answer_id)\",\"optionName\":\"metric_rlqcpobjpal_difuqnkvt27\",\"sqlExpression\":null},\"adhoc_filters\":[],\"row_limit\":100,\"sort_by_metric\":true,\"color_scheme\":\"supersetColors\",\"show_labels_threshold\":5,\"show_legend\":true,\"legendType\":\"scroll\",\"legendOrientation\":\"top\",\"label_type\":\"key\",\"number_format\":\"SMART_NUMBER\",\"date_format\":\"smart_date\",\"show_labels\":true,\"labels_outside\":true,\"outerRadius\":70,\"innerRadius\":30,\"extra_form_data\":{},\"dashboards\":[1]}",
        "query_context": None,
        "query_context_generation": True,
        "slice_name": "Answers",
        "viz_type": "pie"
    }

    chart_resp = session.post(f'{url}/api/v1/chart/', headers=headers, json=body)
    chart = chart_resp.json()
    saveToFile(json.dumps(chart, indent=4))
    return chart


def createChart_poincare(url, session, headers, owner):

    auth_token = headers['Authorization'].split('Bearer ')[1]

    body = {
        "cache_timeout": 0,
        "certification_details": None,
        "certified_by": None,
        "dashboards": [
            get_dashboard_id(getDashboards(url, auth_token), "Standardized Dashboard of " + owner.get('last_name'))
        ],
        "datasource_id": get_datasource_id(getDatasets(url, auth_token, session), owner.get('last_name') + "_poincare_data"),
        "datasource_name": owner.get('last_name') + "_poincare_data",
        "datasource_type": "table",
        "description": None,
        "is_managed_externally": False,
        "owners": [
            owner.get('id')
        ],
        "params": "{\"datasource\":\"19__table\",\"viz_type\":\"echarts_timeseries_scatter\",\"slice_id\":101,\"x_axis\":\"current\",\"time_grain_sqla\":\"P1D\",\"x_axis_sort_asc\":true,\"x_axis_sort_series\":\"name\",\"x_axis_sort_series_ascending\":true,\"metrics\":[{\"aggregate\":\"MIN\",\"column\":{\"advanced_data_type\":null,\"certification_details\":null,\"certified_by\":null,\"column_name\":\"next\",\"description\":null,\"expression\":\"\",\"filterable\":true,\"groupby\":true,\"id\":103,\"is_certified\":false,\"is_dttm\":false,\"python_date_format\":null,\"type\":\"INTEGER\",\"type_generic\":0,\"verbose_name\":null,\"warning_markdown\":null},\"datasourceWarning\":false,\"expressionType\":\"SIMPLE\",\"hasCustomLabel\":false,\"label\":\"MIN(next)\",\"optionName\":\"metric_0chow2tvnb1_li5r07cmjz\",\"sqlExpression\":null}],\"groupby\":[\"session_id\"],\"adhoc_filters\":[],\"order_desc\":true,\"row_limit\":1000,\"truncate_metric\":true,\"show_empty_columns\":true,\"comparison_type\":\"values\",\"annotation_layers\":[],\"forecastPeriods\":10,\"forecastInterval\":0.8,\"x_axis_title_margin\":15,\"y_axis_title_margin\":15,\"y_axis_title_position\":\"Left\",\"sort_series_type\":\"sum\",\"color_scheme\":\"supersetColors\",\"only_total\":true,\"markerSize\":6,\"show_legend\":true,\"legendType\":\"scroll\",\"legendOrientation\":\"top\",\"x_axis_time_format\":\"smart_date\",\"rich_tooltip\":true,\"tooltipTimeFormat\":\"smart_date\",\"y_axis_format\":\"SMART_NUMBER\",\"truncateXAxis\":true,\"y_axis_bounds\":[null,null],\"extra_form_data\":{},\"dashboards\":[]}",
        "query_context": None,
        "query_context_generation": True,
        "slice_name": "Poincare",
        "viz_type": "echarts_timeseries_scatter"
    }

    chart_resp = session.post(f'{url}/api/v1/chart/', headers=headers, json=body)
    chart = chart_resp.json()
    saveToFile(json.dumps(chart, indent=4))
    return chart


# ---------- utils ------------------------------------------

##
# gets the chart info from a specific dashboard
def get_charts_from_dashboard(chart_json, dashboard_id):
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
    for dashboard in dashboards['result']:
        if dashboard['dashboard_title'] == title:
            return dashboard['id']
    print('No dashboard with title', title)
    return -1


def get_datasource_id(datasets, table_name):
    for dataset in datasets['result']:
        if dataset['table_name'] == table_name:
            return dataset['id']
    print('No datasource with table name', table_name)
    return -1

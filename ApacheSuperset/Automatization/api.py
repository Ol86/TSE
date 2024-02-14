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
    charts_res = requests.get(f'{url}/api/v1/chart/', headers=headers)
    charts = charts_res.json()
    saveToFile(json.dumps(charts, indent=4))
    return charts


def getDatasets(url, auth_token):
    headers = {'Authorization': f'Bearer {auth_token}'}
    datasets_res = requests.get(f'{url}/api/v1/dataset/', headers=headers)
    datasets = datasets_res.json()
    saveToFile(json.dumps(datasets, indent=4))
    return datasets


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
    #TODO table name umbenannt  
    dataset_resp = session.post(f'{url}/api/v1/dataset/', headers=headers, json=body)
    dataset = dataset_resp.json()
    saveToFile(json.dumps(dataset, indent=4))
    return dataset

import json
import requests
import dashboard as d
from dashboard import saveToFile

def getCharts(url, auth_token):
    headers = {'Authorization': f'Bearer {auth_token}'}
    params = {"limit": 100}
    charts_res = requests.get(f'{url}/api/v1/chart/', headers=headers, params=params)
    charts = charts_res.json()
    saveToFile(json.dumps(charts, indent=4))
    return charts


def createChart_numberOfSessions(url, session, headers, owner):
    """This method creates a chart for the Dashboard
    This chart contains one big number with the amounf of sessions

    :param url: the api url
    :param session: the request session
    :param headers: the api header
    :param owner: the charts owner
    :return: the newly created chart
    """
    auth_token = headers['Authorization'].split('Bearer ')[1]

    body = {
        "cache_timeout": 0,
        "certification_details": None,
        "certified_by": None,
        "dashboards": [
            d.get_dashboard_id(d.getDashboards(url, auth_token), "Standardized Dashboard of " + owner.get('last_name'))
        ],
        "datasource_id": d.get_datasource_id(d.getDatasets(url, auth_token, session), owner.get('last_name') + "_base_session"),
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
    """This method creates a chart for the Dashboard
    This chart contains a big number with the amount of watches used

    :param url: the api url
    :param session: the request session
    :param headers: the api header
    :param owner: the charts owner
    :return: the newly created chart
    """
    auth_token = headers['Authorization'].split('Bearer ')[1]

    body = {
        "cache_timeout": 0,
        "certification_details": None,
        "certified_by": None,
        "dashboards": [
            d.get_dashboard_id(d.getDashboards(url, auth_token), "Standardized Dashboard of " + owner.get('last_name'))],
        "datasource_id": d.get_datasource_id(d.getDatasets(url, auth_token, session), owner.get('last_name') + "_base_experiment_watch_id"),
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
    """This method creates a chart for the Dashboard
    This chart contains teh specific experiment information

    :param url: the api url
    :param session: the request session
    :param headers: the api header
    :param owner: the charts owner
    :return: the newly created chart
    """
    auth_token = headers['Authorization'].split('Bearer ')[1]

    body = {
        "cache_timeout": 0,
        "certification_details": None,
        "certified_by": None,
        "dashboards": [
            d.get_dashboard_id(d.getDashboards(url, auth_token), "Standardized Dashboard of " + owner.get('last_name'))],
        "datasource_id": d.get_datasource_id(d.getDatasets(url, auth_token, session), owner.get('last_name') + "_base_experiment"),
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
    """This method creates a chart for the Dashboard
    This chart contains all used questions

    :param url: the api url
    :param session: the request session
    :param headers: the api header
    :param owner: the charts owner
    :return: the newly created chart
    """
    auth_token = headers['Authorization'].split('Bearer ')[1]

    body = {
        "cache_timeout": 0,
        "certification_details": None,
        "certified_by": None,
        "dashboards": [
            d.get_dashboard_id(d.getDashboards(url, auth_token), "Standardized Dashboard of " + owner.get('last_name'))],
        "datasource_id": d.get_datasource_id(d.getDatasets(url, auth_token, session), owner.get('last_name') + "_base_questions"),
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
    """This method creates a chart for the Dashboard
    This chart contains a heart rate line for each session

    :param url: the api url
    :param session: the request session
    :param headers: the api header
    :param owner: the charts owner
    :return: the newly created chart
    """
    auth_token = headers['Authorization'].split('Bearer ')[1]

    body = {
        "cache_timeout": 0,
        "certification_details": None,
        "certified_by": None,
        "dashboards": [
            d.get_dashboard_id(d.getDashboards(url, auth_token), "Standardized Dashboard of " + owner.get('last_name'))
        ],
        "datasource_id": d.get_datasource_id(d.getDatasets(url, auth_token, session), owner.get('last_name') + "_base_heart_rate"),
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

def createChart_answers(url, session, headers, owner):
    """This method creates a chart for the Dashboard
    This chart contains all the given answers 

    :param url: the api url
    :param session: the request session
    :param headers: the api header
    :param owner: the charts owner
    :return: the newly created chart
    """
    auth_token = headers['Authorization'].split('Bearer ')[1]

    body = {
        "cache_timeout": 0,
        "certification_details": None,
        "certified_by": None,
        "dashboards": [
            d.get_dashboard_id(d.getDashboards(url, auth_token), "Standardized Dashboard of " + owner.get('last_name'))
        ],
        "datasource_id": d.get_datasource_id(d.getDatasets(url, auth_token, session), owner.get('last_name') + "_base_answers_ids"),
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
    """This method creates a chart for the Dashboard
    This chart contains a poncare plot 

    :param url: the api url
    :param session: the request session
    :param headers: the api header
    :param owner: the charts owner
    :return: the newly created chart
    """
    auth_token = headers['Authorization'].split('Bearer ')[1]

    body = {
        "cache_timeout": 0,
        "certification_details": None,
        "certified_by": None,
        "dashboards": [
            d.get_dashboard_id(d.getDashboards(url, auth_token), "Standardized Dashboard of " + owner.get('last_name'))
        ],
        "datasource_id": d.get_datasource_id(d.getDatasets(url, auth_token, session), owner.get('last_name') + "_poincare_data"),
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
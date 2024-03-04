import json
import requests
from IPython.core.display import JSON
import matplotlib.pyplot as plt

import api

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
print(auth_token)

csrf_token = \
    session.get(f'{url}/api/v1/security/csrf_token/', headers={'Authorization': f'Bearer {auth_token}'}).json()[
        'result']

print(csrf_token)

# api.createDashboard(url, csrf_token, auth_token, session)
# api.createChart(url, csrf_token, auth_token, session)
# api.getCharts(url, auth_token)
# new_chart = api.createChart(url, csrf_token, auth_token, session)
# api.saveToFile(json.dumps(new_chart, indent=4))

# api.getDatasets(url, auth_token)
# api.createDataset(url, csrf_token, auth_token, session)

# ----------------------------------------------------------------

maedche = {
    "first_name": "user",
    "id": 2,
    "last_name": "Maedche"
}

ole = {
    "first_name": "user",
    "id": 3,
    "last_name": "OleMenke"
}


#api.getDatasets(url, auth_token, session)
#print(api.get_datasource_id(api.getDatasets(url, auth_token, session), owner.get('last_name') + "_base_session"))
api.createStandarizedDashboard(url, csrf_token, auth_token, session, maedche)
api.createStandarizedDashboard(url, csrf_token, auth_token, session, ole)

#api.getDatasets(url, auth_token)
# api.getCharts(url, auth_token)
# api.getDashboards(url, auth_token)
# api.get_charts_from_dashboard(api.getCharts(url, auth_token), 3)

# api.createChart_numberOfSessions(url, csrf_token, auth_token, session)
# api.createChart_experimentInformation(url, csrf_token, auth_token, session)
# api.createChart_questions(url, csrf_token, auth_token, session)
# api.createChart_numberOfWatches(url, csrf_token, auth_token, session)
# api.createChart_hr(url, csrf_token, auth_token, session)


# api.updateDashboard(url, csrf_token, auth_token, session)

session.close()

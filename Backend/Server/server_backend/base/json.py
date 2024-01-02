from base.serializers import *
from base.models import *
from user.models import *

import json

def returnExperimentInfo(experiment):
    questions = []
    for i in experiment["questions"]:
        questions.append(QuestionsSerializer(Questions.objects.get(id=i), many=False).data)
    
    watches = []
    for i in experiment["watch_id"]:
        watch = WatchSerializer(Watch.objects.get(user_id=i)).data
        base_user = BaseUserSerializer(BaseUser.objects.get(id=watch["user"])).data
        watches.append({'name': base_user["username"], 'watch': watch["mac_adress"]},)
    result = {
        'id': experiment["id"],
        'title': experiment["title"],
        'watches': watches,
        'acc': experiment["accelerometer"],
        'hr': experiment["hearth_rate"],
        'ppg_g': experiment["ppg_green"],
        'ppg_i': experiment["ppg_ir"],
        'ppg_r': experiment["ppg_red"],
        'bia': experiment["bia"],
        'ecg': experiment["ecg"],
        'spo2': experiment["spo2"],
        'swl': experiment["sweat_loss"],
        'created_at': experiment["created_at"],
        'questions': questions
    }
    return result

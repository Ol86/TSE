# Datetime to convert a timestamp into datetime-format
from datetime import datetime

# Json to handle json opperations.
import json

# The seriallizer to get all the required informations of the Models.
from base.serializers import *
# All models to get and store the required informations.
from base.models import *
from user.models import *

# --------------------------------------------------------------------------------------------------- #
def returnExperimentInfo(experiment):
    """This method takes all the required informations of the template and fits it into a
    specified pattern.

    :param experiment: The information of the required experiment.
    :return: The resulting dictionary, that ich converted into a json.
    """
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
        'hr': experiment["heart_rate"],
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

# --------------------------------------------------------------------------------------------------- #
def insertEcgData(data):
    """This method handles and inserts the ecg data into the database.

    :param data: The send data of the watch.
    """
    session = Session.objects.get(id=data["session"])
    for ecg in data["data"]["ecg"]:
        line = ECG(
            ppg_green=ecg["ppgGreen"],
            ecg=ecg["ecg"],
            session=session,
            lead_off=ecg["leadOff"],
            max_threshold=ecg["maxThreshold"],
            sequence=ecg["sequence"],
            min_threshold=ecg["minThreshold"],
            time=datetime.utcfromtimestamp(int(ecg["time"]) / 1000)
        )
        line.save()

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - #
def insertHeartrateData(data):
    """This method handles and inserts the heart rate data into the database.

    :param data: The send data of the watch.
    """
    session = Session.objects.get(id=data["session"])
    for hr in data["data"]["heartrate"]:
        line = Heart_Rate(
            session=session,
            time=datetime.utcfromtimestamp(int(hr["time"]) / 1000),
            hr=hr["hr"],
            hr_status=hr["hr_status"],
            ibi=hr["ibi"],
            ibi_status=hr["ibi_status"]
        )
        line.save()

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - #
def insertSPO2Data(data):
    """This method handles and inserts the spo2 data into the database.

    :param data: The send data of the watch.
    """
    session = Session.objects.get(id=data["session"])
    for spo2 in data["data"]["spo2"]:
        line = SPO2(
            spo2=spo2["spo2"],
            session=session,
            time=datetime.utcfromtimestamp(int(spo2["time"]) / 1000),
            heartrate=spo2["heartRate"],
            status=spo2["spo2"]
        )
        line.save()

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - #
def insertAccelerometerData(data):
    """This method handles and inserts the accelerometer data into the database.

    :param data: The send data of the watch.
    """
    session = Session.objects.get(id=data["session"])
    for accelerometer in data["data"]["accelerometer"]:
        line = Accelerometer(
            session=session,
            time=datetime.utcfromtimestamp(int(accelerometer["time"]) / 1000),
            x=accelerometer["x"],
            y=accelerometer["y"],
            z=accelerometer["z"]
        )
        line.save()

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - #
def insertPPGIRData(data):
    """This method handles and inserts the ppg-ir data into the database.

    :param data: The send data of the watch.
    """
    session = Session.objects.get(id=data["session"])
    for ppgir in data["data"]["ppgir"]:
        line = PPG_IR(
            ppg_ir=ppgir["ppgir"],
            session=session,
            time=datetime.utcfromtimestamp(int(ppgir["time"]) / 1000)
        )
        line.save()

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - #
def insertPPGRedData(data):
    """This method handles and inserts the ppg-red data into the database.

    :param data: The send data of the watch.
    """
    session = Session.objects.get(id=data["session"])
    for ppgred in data["data"]["ppgred"]:
        line = PPG_Red(
            ppg_red=ppgred["ppgred"],
            session=session,
            time=datetime.utcfromtimestamp(int(ppgred["time"]) / 1000)
        )
        line.save()

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - #
def insertPPGGreenData(data):
    """This method handles and inserts the ppg-green data into the database.

    :param data: The send data of the watch.
    """
    session = Session.objects.get(id=data["session"])
    for ppggreen in data["data"]["ppggreen"]:
        line = PPG_Green(
            ppg_green=ppggreen["ppggreen"],
            session=session,
            time=datetime.utcfromtimestamp(int(ppggreen["time"]) / 1000)
        )
        line.save()

# --------------------------------------------------------------------------------------------------- #
def insertAnswers(data):
    session = Session.objects.get(id=data["session"])
    for answer in data["questions"]:
        line = Answers(
            question=answer["question"],
            experiment=session.experiment,
            session=session,
            answer=answer["answer"]
        )
        line.save()

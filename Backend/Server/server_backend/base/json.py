# Json to handle json opperations.
import json
# Datetime to convert unix timestamp.
from datetime import datetime

# The seriallizer to get all the required informations of the Models.
from base.serializers import *

# All models to get and store the required informations.
from base.models import *
from user.models import *

# --------------------------------------------------------------------------------------------------- #
def returnExperimentInfo(experiment, watch_id):
    """This method takes all the required informations of the template and fits it into a
    specified pattern.

    :param experiment: The information of the required experiment.
    :param watch_id: The information of the current watch.
    :return: The resulting dictionary, that ich converted into a json.
    """
    questions = []
    for i in experiment["questions"]:
        question = QuestionsSerializer(Questions.objects.get(id=i), many=False).data
        answer1 = QuestionAnswerSerializer(QuestionAnswers.objects.get(question_id=i, position=1), many=False).data
        answer2 = QuestionAnswerSerializer(QuestionAnswers.objects.get(question_id=i, position=2), many=False).data
        if question["button3"]:
            answer3 = QuestionAnswerSerializer(QuestionAnswers.objects.get(question_id=i, position=3), many=False).data
        else:
            answer3 = {'answer': ''}
        if question["button4"]:
            answer4 = QuestionAnswerSerializer(QuestionAnswers.objects.get(question_id=i, position=4), many=False).data
        else:
            answer4 = {'answer': ''}

        questionResult = {
            'id': question["id"],
            'question': question["question"],
            'button1': question["button1"],
            'button1_text': answer1["answer"],
            'button2': question["button2"],
            'button2_text': answer2["answer"],
            'button3': question["button3"],
            'button3_text': answer3["answer"],
            'button4': question["button4"],
            'button4_text': answer4["answer"],
            'created_at': question["created_at"]
        }
        questions.append(questionResult)
    
    watch = Watch.objects.get(user_id=watch_id)
    current_watch = []
    current_watch.append({'name': watch.user.username, 'watch': watch.serialnumber})
    result = {
        'id': experiment["id"],
        'max_time': experiment["max_time"],
        'title': experiment["title"],
        'watches': current_watch,
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
        'question_interval': experiment["question_interval"],
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
            time=datetime.fromtimestamp(int(ecg["time"])/1000)
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
            time=datetime.fromtimestamp(int(hr["time"])/1000),
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
            time=datetime.fromtimestamp(int(spo2["time"])/1000),
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
            time=datetime.fromtimestamp(int(accelerometer["time"])/1000),
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
            time=datetime.fromtimestamp(int(ppgir["time"])/1000)
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
            time=datetime.fromtimestamp(int(ppgred["time"])/1000)
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
            time=datetime.fromtimestamp(int(ppggreen["time"])/1000)
        )
        line.save()

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - #
def insertAnswers(data):
    """This method handle the saving process of the answers.

    :param data: The send data of the watch.
    """
    session = Session.objects.get(id=data["session"])
    for answer in data["questions"]:
        answers = QuestionAnswers.objects.get(question_id=answer["question"], position=answer["answer"])
        line = Answers(
            experiment=session.experiment,
            session=session,
            answer=answers,
            created_at=datetime.fromtimestamp(int(answer["time"])/1000)
        )
        line.save()

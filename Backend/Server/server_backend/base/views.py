# All the required packages to handle django authentication.
from django.contrib.auth.models import User
from django.contrib import messages
from django.contrib.auth.decorators import login_required
from django.contrib.auth import authenticate, login, logout

# All the required packages to handle django rest api.
from rest_framework import status
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import IsAuthenticated
from rest_framework.authtoken.models import Token

# Json to encrypt and decrypt json into dictionarys to work with.
import json

# Methods to guid the user.
from django.shortcuts import render, redirect

# The seializer to get the required informations.
from base.serializers import *
# The models to store to the database.
from base.models import *
# The forms to be rendered.
from base.forms import ExperimentForm, QuestionForm
# The handling methods to save the given data to the database.
from base.json import *

# --------------------------------------------------------------------------------------------------- #
def loginPage(request):
    """ This function handle the generation of the loginpage.
    The function will check the POST request submitted from the html <form>
    variable user will be None if username and password does not match any user in the
    database.

    :param request: The request of the page and the user.
    :return: The function returns the resulting webpage.
    """     
    if request.method == 'POST':
        username = request.POST.get('username')
        password = request.POST.get('password')
    
        user = authenticate(request, username=username, password=password)

        if user is not None:
            login(request, user)
            return redirect('home')
        else:
            messages.error(request, 'Username or password does not exist')

    context = {}
    return render(request, 'base/login.html', context)

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - #
def logoutUser(request):
    """ This function handle the logout of the user.

    :param request: The request of the page and the user.
    :return: The function returns the resulting webpage and logouts the current user.
    """
    logout(request)
    return redirect('home')

# --------------------------------------------------------------------------------------------------- #
@login_required(login_url='login')
def home(request):
    """ This function handle the generation of the homepage to display the latest experiments.

    :param request: The request of the page and the user.
    :return: The function returns the resulting webpage.
    """     
    experiments = Experiment.objects.filter(created_by=request.user.id).order_by('-id')[:10]
    context = {'experiments': experiments}              #gibt unserer home.html die Experimente
    return render(request, 'base/home.html', context)   #siehe templates/base/home.html

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - #
@login_required(login_url='login')
def experiment(request, pk):
    """ This funtion handles the specific experiments and their display type.
    
    :param request: This handles the request of the information of the user.
    :param pk: It specifies which experiment should be displayed.
    :return: This funtion returns the webpage of the specific experiment.
    """
    experiment = Experiment.objects.get(id=pk)
    context = {'experiment': experiment}
    return render(request, 'base/experiment/experiment.html', context)

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - #
@login_required(login_url='login')
def createExperiment(request):
    """ This function handles the creation of a new experiment.

    :param request: It handles the request to create a new experiment.
    :return: This function returns the same page if an error occured, 
    or it redirects the user to the homepage if the generation of a new experiment was successful.
    """
    form = ExperimentForm()
    current_profile = Profile.objects.get(user=request.user.id)
    if request.method == 'POST':
        form = ExperimentForm(request.POST)
        if form.is_valid():
            edit = form.save(commit=False)
            edit.created_by = current_profile
            edit.save()
            form.save_m2m()
            return redirect('home')

    context = {'form': form}
    return render(request, 'base/experiment/create_experiment.html', context)

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - #
@login_required(login_url='login')
def deleteExperiment(request, pk):
    """ This function handles the deletion of an experiment.

    :param request: It handles the request to delete an experiment and update the database.
    :return: This function returns the same page before delete was clicked, 
    or it redirects the user to the homepage if the deletion of an experiment was successful.
    """

    experiment = Experiment.objects.get(id=pk)
    if request.method == 'POST':
        experiment.delete()
        return redirect('home')
    return render(request, 'base/experiment/delete_experiment.html', {'experiment': experiment})

# --------------------------------------------------------------------------------------------------- #
# TODO: Add comment
@login_required(login_url='login')
def questions(request):
    questions = Questions.objects.all()
    context = {'questions': questions}
    return render(request, 'base/question/questions.html', context)

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - #
# TODO: Add comment
@login_required(login_url='login')
def createQuestion(request):
    form = QuestionForm()
    if request.method == 'POST':
        form = QuestionForm(request.POST)
        if form.is_valid():
            edit = form.save(commit=False)
            edit.button1 = bool(edit.button1_text)
            edit.button2 = bool(edit.button2_text)
            edit.button3 = bool(edit.button3_text)
            edit.button4 = bool(edit.button4_text)
            edit.save()
            return redirect('questions')

    context = {'form': form}
    return render(request, 'base/question/create_question.html', context)

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - #
@login_required(login_url='login')
def deleteQuestion(request, pk):
    """ This function handles the deletion of an question.

    :param request: It handles the request to delete aquestion.
    :return: This function returns the same page before delete was clicked, 
    or it redirects the user to the homepage if the deletion of a question was successful.
    """

    question = Questions.objects.get(id=pk)
    if request.method == 'POST':
        question.delete()
        return redirect('questions')
    return render(request, 'base/question/delete_question.html', {'question': question})

# --------------------------------------------------------------------------------------------------- #
# TODO: Add comment
@api_view(['GET'])
@permission_classes([IsAuthenticated])
def testApi(request):
    if request.method == 'GET':
        return Response({'message': 'Hello, World!'})
    return Response({'error': 'Wrong rest method'})

# --------------------------------------------------------------------------------------------------- #
# TODO: Add comment
@api_view(['GET'])
@permission_classes([IsAuthenticated])
def getExperimentTemplate(request):
    if request.method == 'GET':
        key = request.META['HTTP_AUTHORIZATION'].split(" ")[1]
        token = Token.objects.get(key=key)
        experiment = ExperimentSerializer(Experiment.objects.all(), many=True).data
        is_valid = False
        last_experiment_index = 0
        for i in range(len(experiment) - 1, -1, -1):
            for j in experiment[i]["watch_id"]:
                if j == token.user.id:
                    last_experiment_index = i
                    is_valid = True

        if is_valid:
            result = returnExperimentInfo(experiment[last_experiment_index])
        else:
            result = {"error": "No experiment with this watch"}

        return Response(result)
    return Response({'error': 'Wrong rest method'})

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - #
# TODO: Add comment
@api_view(['POST'])
@permission_classes([IsAuthenticated])
def sendWatchData(request):
    if request.method == 'POST':
        data = request.data

        if data:
            key = request.META['HTTP_AUTHORIZATION'].split(" ")[1]
            token = Token.objects.get(key=key)
            experiment = ExperimentSerializer(Experiment.objects.all(), many=True).data
            last_experiment_index = 0
            for i in range(len(experiment) - 1, -1, -1):
                for j in experiment[i]["watch_id"]:
                    if j == token.user.id:
                        last_experiment_index = i
            
            insertEcgData(data)
            insertHeartrateData(data)
            insertSPO2Data(data)
            insertAccelerometerData(data)
            insertPPGIRData(data)
            insertPPGRedData(data)
            insertPPGGreenData(data)
            insertAnswers(data)

            return Response({'message': 'Data transfer was successful'} ,status=status.HTTP_201_CREATED)
        return Response({'error': 'No data was send by the watch'}, status=status.HTTP_400_BAD_REQUEST)
    return Response({'error': 'Wrong rest method'}, status=status.HTTP_400_BAD_REQUEST)

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - #
# TODO: Add comment
@api_view(['GET'])
@permission_classes([IsAuthenticated])
def createSession(request):
    if request.method == 'GET':
        key = request.META['HTTP_AUTHORIZATION'].split(" ")[1]
        token = Token.objects.get(key=key)
        experiment = ExperimentSerializer(Experiment.objects.all(), many=True).data
        last_experiment_index = 0
        for i in range(len(experiment) - 1, -1, -1):
            for j in experiment[i]["watch_id"]:
                if j == token.user.id:
                    last_experiment_index = i
        
        session = Session(
            experiment=Experiment.objects.get(id=experiment[last_experiment_index]["id"]), 
            watch=Watch.objects.get(user=token.user.id)
        )
        session.save()

        result = {"session": session.id}

        return Response(result ,status=status.HTTP_201_CREATED)
    return Response({'error': 'Wrong rest method'}, status=status.HTTP_400_BAD_REQUEST)

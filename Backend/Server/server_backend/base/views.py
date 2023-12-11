from django.contrib.auth.models import User
from django.contrib import messages
from django.contrib.auth.decorators import login_required
from django.contrib.auth import authenticate, login, logout
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework.permissions import IsAuthenticated
from django.shortcuts import render, redirect
from .models import *
from .forms import ExperimentForm


def loginPage(request):
    """ This function handle the generation of loginpage.
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

def logoutUser(request):
    """ This function handle the logout of the user.

    :param request: The request of the page and the user.
    :return: The function returns the resulting webpage and logouts the current user.
    """
    logout(request)
    return redirect('home')

@login_required(login_url='login')
def home(request):
    """ This function handle the generation of the homepage to display the latest experiments.

    :param request: The request of the page and the user.
    :return: The function returns the resulting webpage.
    """     
    experiments = Experiment.objects.all()  	        #objects.all() gibt alle Experiment Instanzen zurück als Querryset
    context = {'experiments': experiments}              #gibt unserer home.html die Experimente
    return render(request, 'base/home.html', context)   #siehe templates/base/home.html

@login_required(login_url='login')
def experiment(request, pk):
    """ This funtion handles the specific experiments and their display type.
    
    :param request: This handles the request of the information of the user.
    :param pk: It specifies which experiment should be displayed.
    :return: This funtion returns the webpage of the specific experiment.
    """
    experiment = Experiment.objects.get(id=pk)
    context = {'experiment': experiment}
    return render(request, 'base/experiment.html', context)

@login_required(login_url='login')
def createExperiment(request):
    """ This function handles the creation of a new experiment.

    :param request: It handles the request to create a new experiment and transfer the specifications to the database.
    :return: This function returns the same page if an error occured, or it redirects the user to the homepage if the generation of a new experiment was successful
    """
    form = ExperimentForm()
    if request.method == 'POST':
        form = ExperimentForm(request.POST)
        if form.is_valid():
            form.save()
            return redirect('home')

    context = {'form': form}
    return render(request, 'base/create_experiment.html', context)

@login_required(login_url='login')
def deleteExperiment(request, pk):
    """ This function handles the deletion of an experiment.

    :param request: It handles the request to delete an experiment and update the database.
    :return: This function returns the same page before delete was clicked, or it redirects the user to the homepage if the deletion of an experiment was successful
    """

    experiment = Experiment.objects.get(id=pk)
    if request.method == 'POST':
        experiment.delete
        return redirect('home')
    return render(request, 'base/delete_experiment.html', {'experiments': experiment})

class TestAPI(APIView):

    permission_classes = (IsAuthenticated,)

    def get(self, request):
        content = {'message': 'Hello, World!'}
        return Response(content)


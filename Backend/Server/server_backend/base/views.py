from django.contrib.auth.models import User
from rest_framework import permissions, viewsets
from .serializers import UserSerializer
from django.shortcuts import render, redirect
from .models import *
from .forms import ExperimentForm

def home(request):
    """ This function handle the generation of the homepage to display the latest experiments.

    :param request: The request of the page and the user.
    :return: The function returns the resulting webpage.
    """     
    experiments = Experiment.objects.all()  	        #objects.all() gibt alle Experiment Instanzen zurück als Querryset
    context = {'experiments': experiments}              #gibt unserer home.html die Experimente
    return render(request, 'base/home.html', context)   #siehe templates/base/home.html

def login(request):
    """ This function handle the generation of the homepage to display the latest experiments.

    :param request: The request of the page and the user.
    :return: The function returns the resulting webpage.
    """     
    return render(request, 'base/login.html')


def experiment(request, pk):
    """ This funtion handles the specific experiments and their display type.
    
    :param request: This handles the request of the information of the user.
    :param pk: It specifies which experiment should be displayed.
    :return: This funtion returns the webpage of the specific experiment.
    """
    experiment = Experiment.objects.get(id=pk)
    context = {'experiment': experiment}
    return render(request, 'base/experiment.html', context)

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

class UserViewSet(viewsets.ModelViewSet):
    queryset = User.objects.all().order_by('-date_joined')
    serializer_class = UserSerializer
    permission_classes = [permissions.IsAuthenticated]


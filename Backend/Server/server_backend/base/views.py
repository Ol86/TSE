from django.shortcuts import render
#from django.http import HttpResponse
from .models import Experiment

# Create your views here.

def home(request):
    #return HttpResponse('Home Page')
    experiments = Experiment.objects.all()              #objects.all() gibt alle Experiment Instanzen zurück als Liste?
    context = {'experiments' : experiments}             #gibt unserer home.html die Experimente
    return render(request, 'base/home.html', context)   #siehe templates/base/home.html

def experiment(request, pk):
    experiment = Experiment.objects.get(id=pk)          #gibt genau den Experiment mit der übereinstimmenden Id zurück
    context = {'experiment' : experiment}
    return render(request, 'base/experiment.html', context)

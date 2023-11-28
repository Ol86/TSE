from django.shortcuts import render
from .models import *
#from django.http import HttpResponse

# Create your views here.

def home(request):             
    experiments = Experiment.objects.all() #objects.all() gibt alle Experiment Instanzen zurück als Liste?
    context = {'experiments': experiments}             #gibt unserer home.html die Experimente
    return render(request, 'base/home.html', context)   #siehe templates/base/home.html

def experiment(request, pk):
    experiment = Experiment.objects.get(id=pk)
    context = {'experiment': experiment}
    return render(request, 'base/experiment.html', context)

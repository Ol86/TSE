from django.shortcuts import render
#from django.http import HttpResponse

# Create your views here.

def home(request):             #objects.all() gibt alle Experiment Instanzen zurück als Liste?
    context = {}             #gibt unserer home.html die Experimente
    return render(request, 'base/home.html')   #siehe templates/base/home.html

def experiment(request, pk):
    return render(request, 'base/experiment.html')

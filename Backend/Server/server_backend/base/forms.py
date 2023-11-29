from django.forms import ModelForm
from .models import Experiment

class ExperimentForm(ModelForm):
    class Meta:
        model = Experiment
        fields = '__all__'
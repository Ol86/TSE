from django.forms import ModelForm
from base.models import Experiment, Questions

class ExperimentForm(ModelForm):
    class Meta:
        model = Experiment
        fields = '__all__'

class QuestionForm(ModelForm):
    class Meta:
        model = Questions
        fields = '__all__'
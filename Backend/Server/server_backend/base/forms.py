from django import forms
from django.forms import ModelForm
from base.models import Experiment, Questions
from user.models import Watch, Profile

class ExperimentForm(ModelForm):
    title = forms.CharField(required=True, max_length=200, widget=forms.TextInput(attrs={'placeholder': 'Title'}))
    questions = forms.ModelMultipleChoiceField(queryset=Questions.objects.all(), widget=forms.CheckboxSelectMultiple)
    watch_id = forms.ModelMultipleChoiceField(queryset=Watch.objects.all(), widget=forms.CheckboxSelectMultiple)
    created_by = forms.ModelChoiceField(queryset=Profile.objects.all(), required=False)
    class Meta:
        model = Experiment
        fields = '__all__'

class QuestionForm(ModelForm):
    question = forms.CharField(required=True, max_length=200, widget=forms.TextInput(attrs={'placeholder': 'Question'}))
    button1 = forms.BooleanField(required=False)
    button1_text = forms.CharField(required=True, max_length=50, widget=forms.TextInput(attrs={'placeholder': 'Answer 1'}))
    button2 = forms.BooleanField(required=False)
    button2_text = forms.CharField(required=True, max_length=50, widget=forms.TextInput(attrs={'placeholder': 'Answer 2'}))
    button3 = forms.BooleanField(required=False)
    button3_text = forms.CharField(required=False, max_length=50, widget=forms.TextInput(attrs={'placeholder': 'Answer 3'}))
    button4 = forms.BooleanField(required=False)
    button4_text = forms.CharField(required=False, max_length=50, widget=forms.TextInput(attrs={'placeholder': 'Answer 4'}))

    class Meta:
        model = Questions
        fields = '__all__'
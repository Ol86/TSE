# The base models and classes for forms.
from django import forms
from django.forms import ModelForm

# The models, that are required for the creation forms.
from base.models import Experiment, Questions
from user.models import Watch, Profile

# --------------------------------------------------------------------------------------------------- #
class ExperimentForm(ModelForm):
    """This class creates the creation form for the experiment.
    It also specifies some fields, that need some change in the display.

    :param ModelForm: The base structure for the form.
    """
    title = forms.CharField(required=True, max_length=200, widget=forms.TextInput(attrs={'placeholder': 'Title'}))
    max_time = forms.IntegerField(required=True, min_value=0, initial=0, widget=forms.NumberInput(attrs={'step': 5}))
    question_interval = forms.IntegerField(max_value=30, min_value=10, initial=10, required=True)
    questions = forms.ModelMultipleChoiceField(queryset=Questions.objects.all(), required=True, widget=forms.CheckboxSelectMultiple)
    watch_id = forms.ModelMultipleChoiceField(queryset=Watch.objects.all(), required=True, widget=forms.CheckboxSelectMultiple)
    created_by = forms.ModelChoiceField(queryset=Profile.objects.all(), required=False)
    class Meta:
        """This class specifies what model and fields should be used.
        """
        model = Experiment
        fields = '__all__'

# --------------------------------------------------------------------------------------------------- #
class QuestionForm(ModelForm):
    """This class creates the creation form for a question.
    It also specifies some fields, that need some change in the display.

    :param ModelForm: The base structure for the form.
    """
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
        """This class specifies what model and fields should be used.
        """
        model = Questions
        fields = '__all__'
        
from django import forms
from .models import Text


class TextInputForm(forms.ModelForm):
    class Meta:
        model = Text
        fields = ['content']
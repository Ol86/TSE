from django import forms
from .models import Text


class TextInputForm(forms.ModelForm):
    content = forms.CharField(required=True, widget=forms.TextInput(attrs={'placeholder': 'SQL-request'}))
    class Meta:
        model = Text
        fields = ['content']
#TODO: Add comments.
from django import forms
from django.contrib.auth.forms import UserCreationForm
from django.core.validators import RegexValidator

from user.models import BaseUser

class WatchRegisterForm(UserCreationForm):
    mac_adress = forms.CharField(
        max_length=17, 
        required=True, 
        widget=forms.TextInput(
            attrs={'placeholder': 'MAC adress'}
        ), 
        validators=[
            RegexValidator(
                regex='^([0-9A-Fa-f]{2}[:]){5}([0-9A-Fa-f]{2})$', 
                message="Enter a valid mac adress", 
                code="invalid_mac_adress")
        ]
    )

    def __init__(self, *args, **kwargs):
        super(WatchRegisterForm, self).__init__(*args, **kwargs)
        self.fields['username'].widget = forms.TextInput(attrs={'placeholder': 'Username'})
        self.fields['password1'].widget = forms.PasswordInput(attrs={'placeholder': 'Enter Password'})
        self.fields['password2'].widget = forms.PasswordInput(attrs={'placeholder': 'Confirm Password'})

    class Meta:
        model = BaseUser
        fields = ["username", "password1", "password2", "mac_adress"]

class UserRegisterForm(UserCreationForm):
    ROLE = [
        ('professor', 'Professor'),
        ('student', 'Student'),
        ('phd', 'Phd'),
    ]
    role = forms.ChoiceField(
        required=True,
        choices=ROLE
    )

    def __init__(self, *args, **kwargs):
        super(UserRegisterForm, self).__init__(*args, **kwargs)
        self.fields['username'].widget.attrs['placeholder'] = 'Username'
        self.fields['password1'].widget.attrs['placeholder'] = 'Enter Password'
        self.fields['password2'].widget.attrs['placeholder'] = 'Confirm Password'

    class Meta:
        model = BaseUser
        fields = ["username", "password1", "password2", "role"]
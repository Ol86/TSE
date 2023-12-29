from django import forms
from django.contrib.auth.forms import UserCreationForm
from django.core.validators import RegexValidator

from user.models import BaseUser

class WatchRegisterForm(UserCreationForm):
    mac_adress = forms.CharField(max_length=17, validators=[
        RegexValidator(
            regex='^([0-9A-Fa-f]{2}[:]){5}([0-9A-Fa-f]{2})$',
            message="Enter a valid mac adress",
            code="invalid_mac_adress"
        )
    ])

    class Meta:
        model = BaseUser
        fields = ["username", "password1", "password2", "mac_adress"]

class UserRegisterForm(UserCreationForm):
    role = forms.IntegerField()

    class Meta:
        model = BaseUser
        fields = ["username", "password1", "password2", "role"]
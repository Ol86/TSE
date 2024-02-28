#TODO: Add comment
from django import forms
from django.contrib.auth.forms import UserCreationForm
from django.core.validators import RegexValidator

from user.models import BaseUser

class WatchRegisterForm(UserCreationForm):
    # TODO: Add comment
    serialnumber = forms.CharField(
        max_length=50, 
        widget=forms.TextInput(
            attrs={'placeholder': 'Serialnumber'}
        )
    )

    def __init__(self, *args, **kwargs):
        # TODO: Add comment
        super(WatchRegisterForm, self).__init__(*args, **kwargs)
        self.fields['username'].widget = forms.TextInput(attrs={'placeholder': 'Username'})
        self.fields['password1'].widget = forms.PasswordInput(attrs={'placeholder': 'Enter Password'})
        self.fields['password2'].widget = forms.PasswordInput(attrs={'placeholder': 'Confirm Password'})

    class Meta:
        # TODO: Add comment
        model = BaseUser
        fields = ["username", "password1", "password2", "serialnumber"]

class UserRegisterForm(UserCreationForm):
    # TODO: Add comment
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
        # TODO: Add comment
        super(UserRegisterForm, self).__init__(*args, **kwargs)
        self.fields['username'].widget.attrs['placeholder'] = 'Username'
        self.fields['password1'].widget.attrs['placeholder'] = 'Enter Password'
        self.fields['password2'].widget.attrs['placeholder'] = 'Confirm Password'

    class Meta:
        # TODO: Add comment
        model = BaseUser
        fields = ["username", "password1", "password2", "role"]
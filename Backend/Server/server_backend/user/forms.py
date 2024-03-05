# Import the templates for the forms.
from django import forms
from django.contrib.auth.forms import UserCreationForm

# Import of the BaseUser model for the creation.
from user.models import BaseUser

# --------------------------------------------------------------------------------------------------- #
class WatchRegisterForm(UserCreationForm):
    """This class provides the creation for of a new watch.

    :param UserCreationForm: The base template of a usercreationform.
    """
    serialnumber = forms.CharField(
        max_length=50, 
        widget=forms.TextInput(
            attrs={'placeholder': 'Serialnumber'}
        )
    )

    def __init__(self, *args, **kwargs):
        """This function edits the formfields to get them a placeholder.
        """
        super(WatchRegisterForm, self).__init__(*args, **kwargs)
        self.fields['username'].widget = forms.TextInput(attrs={'placeholder': 'Username'})
        self.fields['password1'].widget = forms.PasswordInput(attrs={'placeholder': 'Enter Password'})
        self.fields['password2'].widget = forms.PasswordInput(attrs={'placeholder': 'Confirm Password'})

    class Meta:
        """This class defines what model should be userd and espacially what fields are required.
        """
        model = BaseUser
        fields = ["username", "password1", "password2", "serialnumber"]

# --------------------------------------------------------------------------------------------------- #
class UserRegisterForm(UserCreationForm):
    """This class provides the creation for of a new user.

    :param UserCreationForm: The base template of a usercreationform.
    """
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
        """This function edits the formfields to get them a placeholder.
        """
        super(UserRegisterForm, self).__init__(*args, **kwargs)
        self.fields['username'].widget.attrs['placeholder'] = 'Username'
        self.fields['password1'].widget.attrs['placeholder'] = 'Enter Password'
        self.fields['password2'].widget.attrs['placeholder'] = 'Confirm Password'

    class Meta:
        """This class defines what model should be userd and espacially what fields are required.
        """
        model = BaseUser
        fields = ["username", "password1", "password2", "role"]
        exclude = ('sql_database',)
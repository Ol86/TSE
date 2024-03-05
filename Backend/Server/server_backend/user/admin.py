# Import the important packages for the custom admin page.
from django import forms
from django.contrib import admin
from django.contrib.auth.admin import UserAdmin
from django.contrib.auth.forms import ReadOnlyPasswordHashField

# Import the custom user models.
from user.models import BaseUser, Profile, Watch

# --------------------------------------------------------------------------------------------------- #
class UserCreationForm(forms.ModelForm):
    """This function creates the new creation form for a new user.

    :param forms: The base template for a model creation form.
    :raises forms.ValidationError: An error that occures if the given input is wrong.
    :return: The newly created custom user.
    """
    password1 = forms.CharField(label='Password', widget=forms.PasswordInput)
    password2 = forms.CharField(label='Password confirmation', widget=forms.PasswordInput)

    class Meta:
        """This class defines what model should be userd and espacially what fields are required.
        """
        model = BaseUser
        fields = ('username', 'type',)

    def clean_password2(self):
        """This function checks if the given password is correct.

        :raises forms.ValidationError: If the given passwords do not match it raises this error.
        :return: The correct password.
        """
        password1 = self.cleaned_data.get("password1")
        password2 = self.cleaned_data.get("password2")
        if password1 and password2 and password1 != password2:
            raise forms.ValidationError("Passwords don't match")
        return password2

    def save(self, commit=True):
        """This function edits and saves the newly created user.

        :param commit: If the given input should be saved or edited afterwards, defaults to True.
        :return: The newly created user.
        """
        user = super().save(commit=False)
        if self.cleaned_data["type"] == 1:
            user.is_admin = True
        user.set_password(self.cleaned_data["password1"])
        if commit:
            user.save()
        return user

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - #
class UserChangeForm(forms.ModelForm):
    """This class handles the change form of a user in the admin panel.

    :param forms: The base template for a model change form.
    :return: The changed user model.
    """
    password = ReadOnlyPasswordHashField()

    class Meta:
        """This class defines what model should be userd and espacially what fields are required.
        """
        model = BaseUser
        fields = ('username', 'password', 'type', 'is_active')
    
    def clean_password(self):
        """This function clears the given password field.

        :return: The initial password.
        """
        return self.initial["password"]

# --------------------------------------------------------------------------------------------------- #
class BaseUserAdmin(UserAdmin):
    """This class handle the admin page of the base user model.

    :param UserAdmin: The template for a user page.
    """
    # Define the different forms.
    form = UserChangeForm
    add_form = UserCreationForm

    # Set the fields, that should be displayed.
    list_display = ('username', 'is_admin', 'type')
    list_filter = ('is_admin', 'type',)
    fieldsets = (
        (None, {'fields': ('username', 'password')}),
        ('Type', {'fields': ('type',)}),
        ('Permissions', {'fields': ('is_admin',)}),
    )

    # Add the additional fields of the user.
    add_fieldsets = (
        (None, {
            'classes': ('wide',),
            'fields': ('username', 'password1', 'password2', 'type'),
        }),
    )
    # Specify the search fields, the ordering field and set filter option.
    search_fields = ('username',)
    ordering = ('type',)
    filter_horizontal = ()

# --------------------------------------------------------------------------------------------------- #
admin.site.register(BaseUser, BaseUserAdmin)
admin.site.register(Profile)
admin.site.register(Watch)

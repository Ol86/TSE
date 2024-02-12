#TODO: Add comment
from django import forms
from django.contrib import admin
from django.contrib.auth.admin import UserAdmin
from django.contrib.auth.forms import ReadOnlyPasswordHashField

from user.models import BaseUser, Profile, Watch

# --------------------------------------------------------------------------------------------------- #

class UserCreationForm(forms.ModelForm):
    # TODO: Add comment
    password1 = forms.CharField(label='Password', widget=forms.PasswordInput)
    password2 = forms.CharField(label='Password confirmation', widget=forms.PasswordInput)

    class Meta:
        # TODO: Add comment
        model = BaseUser
        fields = ('username', 'type',)

    def clean_password2(self):
        # TODO: Add comment
        password1 = self.cleaned_data.get("password1")
        password2 = self.cleaned_data.get("password2")
        if password1 and password2 and password1 != password2:
            raise forms.ValidationError("Passwords don't match")
        return password2

    def save(self, commit=True):
        # TODO: Add comment
        user = super().save(commit=False)
        if self.cleaned_data["type"] == 1:
            user.is_admin = True
        user.set_password(self.cleaned_data["password1"])
        if commit:
            user.save()
        return user

class UserChangeForm(forms.ModelForm):
    # TODO: Add comment
    password = ReadOnlyPasswordHashField()

    class Meta:
        # TODO: Add comment
        model = BaseUser
        fields = ('username', 'password', 'type', 'is_active')
    
    def clean_password(self):
        # TODO: Add comment
        return self.initial["password"]

class BaseUserAdmin(UserAdmin):
    # TODO: Add comment
    form = UserChangeForm
    add_form = UserCreationForm

    list_display = ('username', 'is_admin', 'type')
    list_filter = ('is_admin', 'type',)
    fieldsets = (
        (None, {'fields': ('username', 'password')}),
        ('Type', {'fields': ('type',)}),
        ('Permissions', {'fields': ('is_admin',)}),
    )

    add_fieldsets = (
        (None, {
            'classes': ('wide',),
            'fields': ('username', 'password1', 'password2', 'type'),
        }),
    )
    search_fields = ('username',)
    ordering = ('type',)
    filter_horizontal = ()

admin.site.register(BaseUser, BaseUserAdmin)

# --------------------------------------------------------------------------------------------------- #

admin.site.register(Profile)
admin.site.register(Watch)

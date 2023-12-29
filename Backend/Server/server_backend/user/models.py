from django.db import models

from django.db.models.signals import post_save
from django.dispatch import receiver
from django.core.validators import RegexValidator

from django.contrib.auth.models import BaseUserManager as Manager
from django.contrib.auth.models import AbstractBaseUser as UserBase

# --------------------------------------------------------------------------------------------------- #

class BaseUserManager(Manager):
    """This class manages the creation of the base user.
    """
    def create_user(self, username, type, password=None):
        """This method creates a User with the given information

        Args:
            username (String): Username of the user to identify.
            password (String, optional): Password of the user. Defaults to None.

        Raises:
            ValueError: If there is no username given.

        Returns:
            model: User is given back.
        """
        if not username:
            raise ValueError('User must have a username')

        user = self.model(
            username=username,
            type=type,
        )
        user.set_password(password)
        user.save(using=self.db)
        return user

    def create_superuser(self, username, password=None):
        """This method creates a Superuser with the given information and sets the User to admin.

        Args:
            username (String): Username of the user to identify.
            password (String, optional): Password of the user. Defaults to None.

        Returns:
            model: Superuser is given back.
        """
        user = self.create_user(
            username,
            type=1,
            password=password,
        )
        user.is_admin = True
        user.save(using=self.db)
        profile = Profile.objects.create(user=user)
        profile.role = 1
        profile.save()
        return user


class BaseUser(UserBase):
    """This class is the base user of the backend.

    Args:
        model (UserBase): General boundry to implement a custom user model.

    Returns:
        BaseUser: Custome base usermodel of the backend.
    """
    USER = 1
    WATCH = 2
    TYPE = [
        (USER, 'User'),
        (WATCH, 'Watch'),
    ]
    username = models.CharField(max_length=200, unique=True)
    type = models.PositiveSmallIntegerField(choices=TYPE, default=USER)
    is_active = models.BooleanField(default=True)
    is_admin = models.BooleanField(default=False)
    created_at = models.DateTimeField(auto_now_add=True, editable=False)

    objects = BaseUserManager()

    USERNAME_FIELD = 'username'
    REQUIRED_FIELDS = []

    def __str__(self):
        return self.username

    def has_perm(self, perm, obj=None):
        return True

    def has_module_perms(self, app_label):
        return True

    @property
    def is_staff(self):
        return self.is_admin


# --------------------------------------------------------------------------------------------------- #


class Profile(models.Model):
    """This model adds some important fields for the user.

    Args:
        models (Model): The basic model of django.

    Returns:
        profile: The user model of the backend.
    """
    PROFESSOR = 1
    STUDENT = 2
    PHD = 3
    ROLE = [
        (PROFESSOR, 'Professor'),
        (STUDENT, 'Student'),
        (PHD, 'Phd'),
    ]
    user = models.OneToOneField(BaseUser, on_delete=models.CASCADE, primary_key=True)
    role = models.PositiveSmallIntegerField(choices=ROLE, default=STUDENT)

    def __str__(self):
        return self.user.username

# --------------------------------------------------------------------------------------------------- #

class Watch(models.Model):
    """This is the watch model, that adds the mac_adress field.

    Args:
        models (Model): The basic model of django.

    Returns:
        watch: The watch model of the backend.
    """
    user = models.OneToOneField(BaseUser, on_delete=models.CASCADE, primary_key=True)
    mac_adress = models.CharField(max_length=17, validators=[
        RegexValidator(
            regex='^([0-9A-Fa-f]{2}[:]){5}([0-9A-Fa-f]{2})$',
            message="Enter a valid mac adress",
            code="invalid_mac_adress"
        )
    ])

    def __str__(self):
        return self.user.username

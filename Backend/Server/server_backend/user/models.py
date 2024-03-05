# The base Structure for the model.
from django.db import models

# The Regex pattern for the mac address.
from django.core.validators import RegexValidator

# The base Structures for the user and usermanager
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
        user.is_active = True
        user.save(using=self.db)
        profile = Profile.objects.create(user=user)
        profile.role = "admin"
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
    created_at = models.DateTimeField(auto_now_add=True)

    objects = BaseUserManager()

    USERNAME_FIELD = 'username'
    REQUIRED_FIELDS = []

    def __str__(self):
        """This method sets the display name.

        :return: The username.
        """
        return self.username

    def has_perm(self, perm, obj=None):
        """This method sets the permission of the user.

        :param perm: The permission to be checked.
        :param obj: The permission object, defaults to None.
        :return: A boolean value if the user has permissions.
        """
        return True

    def has_module_perms(self, app_label):
        """This method sets the permission to use an other model.

        :param app_label: The label of the app of the model.
        :return: A boolean value if the user has permissions.
        """
        return True

    @property
    def is_staff(self):
        """This method sets the is_staff property of the user.

        :return: The boolean value if the user has the property.
        """
        return self.is_admin

# --------------------------------------------------------------------------------------------------- #
class Profile(models.Model):
    """This model adds some important fields for the user.

    Args:
        models (Model): The basic model of django.

    Returns:
        profile: The user model of the backend.
    """
    ROLE = [
        ('professor', 'Professor'),
        ('student', 'Student'),
        ('phd', 'Phd'),
    ]
    user = models.OneToOneField(BaseUser, on_delete=models.CASCADE, primary_key=True)
    role = models.CharField(max_length=10, choices=ROLE)
    sql_database = models.JSONField(blank=True, null=True)

    def __str__(self):
        """This method sets the display name.

        :return: The username.
        """
        return self.user.username

# --------------------------------------------------------------------------------------------------- #
class Watch(models.Model):
    """This is the watch model, that adds the serialnumber field.

    Args:
        models (Model): The basic model of django.

    Returns:
        watch: The watch model of the backend.
    """
    user = models.OneToOneField(BaseUser, on_delete=models.CASCADE, primary_key=True)
    serialnumber = models.CharField(max_length=50)
    is_running = models.BooleanField(default=False)

    def __str__(self):
        """This method sets the display name.

        :return: The username.
        """
        return self.user.username

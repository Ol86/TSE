from django.contrib.auth.models import AbstractUser, AbstractBaseUser, BaseUserManager
from django.utils.translation import gettext_lazy as _
from django.db import models

# Add a proxy for different custom user models
class AccountManager(BaseUserManager):
    def create_user(self, username, password = None, **extra_fields):
        if not username:
            raise ValueError("Username is required")
        
        user = self.model(
            username = username,
        )

        user.set_password(password)
        user.save(using = self.db)
        return user

    def create_superuser(self, username, password = None, **extra_fields):
        if not username:
            raise ValueError("Username is required")

        user = self.create_user(
            username = username,
            password = password,
        )

        user.is_admin = True
        user.is_staff = True
        user.is_superuser = True
        user.save(using = self.db)
        return user

class Account(AbstractBaseUser):
    class Types(models.TextChoices):
        USERS = "USERS", "users"
        WATCH = "WATCH", "watch"

    username = models.CharField(max_length = 150, unique = True)
    type = models.CharField(max_length = 5, choices = Types.choices, default = Types.USERS)
    is_active = models.BooleanField(default = True)
    is_admin = models.BooleanField(default = False)
    is_staff = models.BooleanField(default = False)
    is_superuser = models.BooleanField(default = False)

    USERNAME_FIELD = "username"

    objects = AccountManager()

    def __str__(self):
        return self.username

    def has_perm(self, perm, obj = None):
        return self.is_admin

    def has_module_perms(self, app_label):
        return True
    
    def save(self, *args, **kwargs):
        if not self.type or self.type == None:
            self.type = Account.Types.USERS
        return super().save(*args, **kwargs)

# Add the specified types of accounts
# Add the User class to specify the accounts
class UserManager(models.Manager):
    def create_user(self, username, password = None, **extra_fields):
        if not username:
            raise ValueError("Username is required")

        user = self.model(
            username = username,
        )

        user.set_password(password)
        user.save(using = self.db)
        return user

    def get_queryset(self, *args, **kwargs):
        queryset = super().get_queryset(*args, **kwargs)
        queryset = queryset.filter(type = Account.Types.USERS)
        return queryset

class User(Account):
    class Meta:
        proxy = True

    objects = UserManager()

    def save(self, *args, **kwargs):
        self.type = Account.Types.USERS
        return super().save(*args, **kwargs)

# Add the Watch class to specify the accounts
class WatchManager(models.Manager):
    def create_user(self, username, password = None, **extra_fields):
        if not username:
            raise ValueError("username is required")
        if not mac_address:
            raise ValueError("MAC-address is required")

        user = self.model(
            username = username,
        )

        user.set_password(password)
        user.save(using = self.db)
        return user

    def get_queryset(self, *args, **kwargs):
        queryset = super().get_queryset(*args, **kwargs)
        queryset = queryset.filter(type = Account.Types.WATCH)
        return queryset

class Watch(Account):
    class Meta:
        proxy = True

    objects = WatchManager()

    def save(self, *args, **kwargs):
        self.type = Account.Types.WATCH
        return super().save(*args, **kwargs)

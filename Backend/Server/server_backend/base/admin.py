# TODO: Change the admin pannel and try to restrict it to the superuser.
from django.contrib import admin

# Register your models here.

from .models import *

admin.site.register(Questions)
admin.site.register(Experiment)
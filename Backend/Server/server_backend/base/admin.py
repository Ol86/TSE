from django.contrib import admin

# Register your models here.

from .models import *

admin.site.register(Participant)
admin.site.register(Watches)
admin.site.register(Experiment)

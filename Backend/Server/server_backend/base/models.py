from django.db import models
from django.contrib.auth.models import User

# Create your models here.

class Watches(models.Model):
    id = models.AutoField(primary_key=True)
    serialnumber = models.CharField(max_length=200)

    def __str__(self):
        return self.serialnumber

class Experiment(models.Model):
    id = models.AutoField(primary_key=True)
    title = models.CharField(max_length=200)
    participant_id = models.ForeignKey(User, on_delete=models.CASCADE)
    watch_id = models.ForeignKey(Watches, on_delete=models.CASCADE)
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return self.title

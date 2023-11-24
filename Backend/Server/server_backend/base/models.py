from django.db import models

# Create your models here.

class Experiment(models.Model):
    #id =
    title = models.CharField(max_length=200)
    description = models.TextField(null=True, blank=True)
    #type =
    #participants =
    #timeInterval =
    created = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return self.title

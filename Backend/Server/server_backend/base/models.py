from django.db import models
from user.models import Watch

class Questions(models.Model):
    id = models.AutoField(auto_created=True, primary_key=True)
    question = models.CharField(max_length=200)
    button1 = models.BooleanField(default=True)
    button1_text = models.CharField(max_length=50)
    button2 = models.BooleanField(default=True)
    button2_text = models.CharField(max_length=50)
    button3 = models.BooleanField(default=True)
    button3_text = models.CharField(max_length=50, null=True, blank=True)
    button4 = models.BooleanField(default=True)
    button4_text = models.CharField(max_length=50, null=True, blank=True)
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        ordering = ['id']

    def __str__(self):
        return self.question

class Experiment(models.Model):
    """
    """
    id = models.AutoField(auto_created=True, primary_key=True)
    title = models.CharField(max_length=200)
    # Add user to 
    watch_id = models.ManyToManyField(Watch)
    # Enable the different sensors
    accelerometer = models.BooleanField(default=False)
    hearth_rate = models.BooleanField(default=False)
    ppg_green = models.BooleanField(default=False)
    ppg_ir = models.BooleanField(default=False)
    ppg_red = models.BooleanField(default=False)
    bia = models.BooleanField(default=False)
    ecg = models.BooleanField(default=False)
    spo2 = models.BooleanField(default=False)
    sweat_loss = models.BooleanField(default=False)
    # Define the questions, to be asked 
    questions = models.ManyToManyField(Questions)
    # Add a time to keep track of the experiments
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        ordering = ['-created_at']

    def __str__(self):
        return self.title
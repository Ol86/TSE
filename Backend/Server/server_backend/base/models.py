from django.db import models
from django.contrib.auth.models import User

class QuestionExperimentHandler(models.Model):
    id = models.AutoField(primary_key=True)
    experiment_id = models.ForeignKey(Experiment, on_delete=models.CASCADE)
    question_id = models.ForeignKey(Questions, on_delete=models.CASCADE)

    class Meta:
        ordering = ['id']

    def __str__(self):
        return self.id

class Experiment(models.Model):
    """
    """
    id = models.AutoField(primary_key=True)
    title = models.CharField(max_length=200)
    Questions = models.ForeignKey(QuestionExperimentHandler, on_delete=models.CASCADE)
    watch_id = models.ForeignKey(User, on_delete=models.CASCADE)
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        ordering = ['-created_at']

    def __str__(self):
        return self.title

class Questions(models.Model):
    id = models.AutoField(primary_key=True)
    question = models.CharField(max_length=200)
    button1 = models.BooleanField(default=True)
    button1_text = models.CharField(max_length=50)
    button2 = models.BooleanField(default=True)
    button2_text = models.CharField(max_length=50)
    button3 = models.BooleanField(default=True)
    button3_text = models.CharField(max_length=50, null=True)
    button4 = models.BooleanField(default=True)
    button4_text = models.CharField(max_length=50, null=True)
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        ordering = ['id']

    def __str__(self):
        return self.question
from django.db import models

from user.models import Watch, Profile

# --------------------------------------------------------------------------------------------------- #
class Questions(models.Model):
    """This class creates the question model.

    :param models: The base stucture for a model.
    :return: The question as a model.
    """
    id = models.AutoField(auto_created=True, primary_key=True)
    question = models.CharField(max_length=200)
    # Answer 1
    button1 = models.BooleanField(default=True)
    button1_text = models.CharField(max_length=50)
    # Answer 2
    button2 = models.BooleanField(default=True)
    button2_text = models.CharField(max_length=50)
    # Answer 3
    button3 = models.BooleanField(default=True)
    button3_text = models.CharField(max_length=50, null=True, blank=True)
    # Answer 4
    button4 = models.BooleanField(default=True)
    button4_text = models.CharField(max_length=50, null=True, blank=True)
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        """This class defines the ordering of the database table.
        """
        ordering = ['id']

    def __str__(self):
        """This method defines the display name.

        :return: The qestion as a display.
        """
        return self.question

# --------------------------------------------------------------------------------------------------- #
class Experiment(models.Model):
    """This class defines the experiment model.

    :param models: The base structure for a model.
    :return: The experiment as a model.
    """
    id = models.AutoField(auto_created=True, primary_key=True)
    title = models.CharField(max_length=200)
    # Add watches to the experiment.
    watch_id = models.ManyToManyField(Watch)
    # Enable the different sensors.
    accelerometer = models.BooleanField(default=False)
    hearth_rate = models.BooleanField(default=False)
    ppg_green = models.BooleanField(default=False)
    ppg_ir = models.BooleanField(default=False)
    ppg_red = models.BooleanField(default=False)
    bia = models.BooleanField(default=False)
    ecg = models.BooleanField(default=False)
    spo2 = models.BooleanField(default=False)
    sweat_loss = models.BooleanField(default=False)
    # Define the questions, to be asked.
    questions = models.ManyToManyField(Questions)
    # Add a time to keep track of the experiments.
    created_by = models.ForeignKey(Profile, on_delete=models.CASCADE)
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        """This class defines the ordering of the database table.
        """
        ordering = ['-created_at']

    def __str__(self):
        """This method defines the display name.

        :return: The experiment title as a display.
        """
        return self.title

# --------------------------------------------------------------------------------------------------- #
class Session(models.Model):
    """This class defines the session model.

    :param models: The base structure for a model.
    :return: The Session as a model.
    """
    id = models.AutoField(auto_created=True, primary_key=True)
    experiment = models.ForeignKey(Experiment, on_delete=models.CASCADE)
    watch = models.ForeignKey(Watch, on_delete=models.CASCADE)
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        """This class defines the ordering of the database table.
        """
        ordering = ['id']
    
    def __str__(self):
        """This method defines the display name.

        :return: The session id and experiment title as a display.
        """
        result = self.id + ": " + self.experiment.title
        return result

# --------------------------------------------------------------------------------------------------- #

# The model template from django and some validators.
from django.db import models
from django.core.validators import MinValueValidator, MaxValueValidator

# The user models for cross connections between tables inside the database.
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
    heart_rate = models.BooleanField(default=False)
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
    :return: The session as a model.
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
class Answers(models.Model):
    """This class defines the answer model.

    :param models: The base structure for a model.
    :return: The answer as a model.
    """
    id = models.AutoField(auto_created=True, primary_key=True)
    experiment = models.ForeignKey(Experiment, on_delete=models.CASCADE)
    session = models.ForeignKey(Session, on_delete=models.CASCADE)
    question = models.ForeignKey(Questions, on_delete=models.CASCADE)
    answer = models.IntegerField(default=1, validators=[MinValueValidator(1), MaxValueValidator(4)])
    time = models.DateTimeField(default=0)

    class Meta:
        """This class defines the ordering of the database table.
        """
        ordering = ['id']

    def __str__(self):
        """This method defines the display name.

        :return: The answer id and the answer with the given answer.
        """
        result = self.id + ": " + self.question.question + " => " + self.answer
        return result

# --------------------------------------------------------------------------------------------------- #
class ECG(models.Model):
    """This class defines the ecg model.

    :param models: The base structure for a model.
    :return The ECG as a model.
    """
    id = models.AutoField(auto_created=True, primary_key=True)
    session = models.ForeignKey(Session, on_delete=models.CASCADE)
    ecg = models.IntegerField(default=0)
    ppg_green = models.IntegerField(default=0)
    lead_off = models.IntegerField(default=0)
    max_threshold = models.IntegerField(default=0)
    sequence = models.IntegerField(default=0)
    min_threshold = models.IntegerField(default=0)
    time = models.DateTimeField(default=0)

    class Meta:
        """This class defines the ordering of the database table.
        """
        ordering = ['id']

    def __str__(self):
        """This method defines the desplay name.

        :return: The session id and the ecg value.
        """
        result = self.session.id + ": " + self.ecg
        return result

# --------------------------------------------------------------------------------------------------- #
class Heart_Rate(models.Model):
    """This class defines the heart rate model.

    :param models: The base structure for a model.
    :return: The heart rate as a model.
    """
    id = models.AutoField(auto_created=True, primary_key=True)
    session = models.ForeignKey(Session, on_delete=models.CASCADE)
    hr = models.IntegerField(default=0)
    hr_status = models.IntegerField(default=0)
    ibi = models.IntegerField(default=0)
    ibi_status = models.IntegerField(default=0)
    time = models.DateTimeField(default=0)

    class Meta:
        """This class defines the ordering of the database table.
        """
        ordering = ['id']

    def __str__(self):
        """This method defines the desplay name.

        :return: The session id and the status values.
        """
        result = self.session.id + ": hr-status " + self.hr_status + "; ibi-status " + self.ibi_status
        return result

# --------------------------------------------------------------------------------------------------- #
class SPO2(models.Model):
    """This class defines the spo2 model.

    :param models: The base structure for a model.
    :return: The spo2 as a model.
    """
    id = models.AutoField(auto_created=True, primary_key=True)
    session = models.ForeignKey(Session, on_delete=models.CASCADE)
    spo2 = models.IntegerField(default=0)
    heartrate = models.IntegerField(default=0)
    status = models.IntegerField(default=0)
    time = models.DateTimeField(default=0)

    class Meta:
        """This class defines the ordering of the database table.
        """
        ordering = ['id']

    def __str__(self):
        """This method defines the desplay name.

        :return: The session id and the spo2 value.
        """
        result = self.session.id + ": " + self.spo2
        return result

# --------------------------------------------------------------------------------------------------- #
class Accelerometer(models.Model):
    # TODO: Add comment
    id = models.AutoField(auto_created=True, primary_key=True)
    session = models.ForeignKey(Session, on_delete=models.CASCADE)
    x = models.IntegerField(default=0)
    y = models.IntegerField(default=0)
    z = models.IntegerField(default=0)
    time = models.DateTimeField(default=0)

    class Meta:
        """This class defines the ordering of the database table.
        """
        ordering = ['id']

    def __str__(self):
        """This method defines the desplay name.

        :return: The session id and the x,y,z values.
        """
        result = self.session.id + ": x" + self.x + "; y" + self.y + "; z" + self.z
        return result

# --------------------------------------------------------------------------------------------------- #
class PPG_Green(models.Model):
    # TODO: Add comment
    id = models.AutoField(auto_created=True, primary_key=True)
    session = models.ForeignKey(Session, on_delete=models.CASCADE)
    ppg_green = models.IntegerField(default=0)
    time = models.DateTimeField(default=0)

    class Meta:
        """This class defines the ordering of the database table.
        """
        ordering = ['id']

    def __str__(self):
        """This method defines the desplay name.

        :return: The session id and the ppg value.
        """
        result = self.session.id + ": " + self.ppg_green
        return result

# --------------------------------------------------------------------------------------------------- #
class PPG_IR(models.Model):
    # TODO: Add comment
    id = models.AutoField(auto_created=True, primary_key=True)
    session = models.ForeignKey(Session, on_delete=models.CASCADE)
    ppg_ir = models.IntegerField(default=0)
    time = models.DateTimeField(default=0)

    class Meta:
        """This class defines the ordering of the database table.
        """
        ordering = ['id']

    def __str__(self):
        """This method defines the desplay name.

        :return: The session id and the ppg value.
        """
        result = self.session.id + ": " + self.ppg_ir
        return result

# --------------------------------------------------------------------------------------------------- #
class PPG_Red(models.Model):
    # TODO: Add comment
    id = models.AutoField(auto_created=True, primary_key=True)
    session = models.ForeignKey(Session, on_delete=models.CASCADE)
    ppg_red = models.IntegerField(default=0)
    time = models.DateTimeField(default=0)

    class Meta:
        """This class defines the ordering of the database table.
        """
        ordering = ['id']

    def __str__(self):
        """This method defines the desplay name.

        :return: The session id and the ppg value.
        """
        result = self.session.id + ": " + self.ppg_red
        return result

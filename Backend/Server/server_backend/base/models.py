from django.db import models
from django.contrib.auth.models import User

class Watches(models.Model):
    """ This class generates the table of the watches inside the database.
    
    :param models.Model: This parameter specifies the generation progres of a new watch.
    """
    id = models.AutoField(primary_key=True)
    serialnumber = models.CharField(max_length=200)

    def __str__(self):
        """ This function is the constructor.
        
        :return It returns the serialnumber of each watch.
        """
        return self.serialnumber

class Experiment(models.Model):
    """
    """
    id = models.AutoField(primary_key=True)
    title = models.CharField(max_length=200)
    participant_id = models.ForeignKey(User, on_delete=models.CASCADE)
    watch_id = models.ForeignKey(Watches, on_delete=models.CASCADE)
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        ordering = ['-created_at']

    def __str__(self):
        return self.title

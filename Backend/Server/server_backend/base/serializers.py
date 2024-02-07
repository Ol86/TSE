# The base structure of the seriallizer model.
from rest_framework import serializers

# The models to be seriallized.
from base.models import Experiment, Questions
from user.models import BaseUser, Watch

# --------------------------------------------------------------------------------------------------- #
class ExperimentSerializer(serializers.ModelSerializer):
    """This class creates the seriallizer for the experiment model.

    :param serializers: Base model for the seriallizer.
    """
    class Meta:
        """This class specifies, which model should be used and what fields are required.
        """
        model = Experiment
        fields = '__all__'

class QuestionsSerializer(serializers.ModelSerializer):
    """This class creates the seriallizer for the question model.

    :param serializers: Base model for the seriallizer.
    """
    class Meta:
        """This class specifies, which model should be used and what fields are required.
        """
        model = Questions
        fields = '__all__'

# --------------------------------------------------------------------------------------------------- #
class WatchSerializer(serializers.ModelSerializer):
    """This class creates the seriallizer for the watch model.

    :param serializers: Base model for the seriallizer.
    """
    class Meta:
        """This class specifies, which model should be used and what fields are required.
        """
        model = Watch
        fields = '__all__'

class BaseUserSerializer(serializers.ModelSerializer):
    """This class creates the seriallizer for the base user model.

    :param serializers: Base model for the seriallizer.
    """
    class Meta:
        """This class specifies, which model should be used and what fields are required.
        """
        model = BaseUser
        fields = '__all__'
        
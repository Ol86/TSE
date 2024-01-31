from django.urls import path
from . import views

urlpatterns = [
    # This url sends the user to the homepage.
    path('', views.home, name="home"),
    # This url sends the user to the loginpage.
    path('login/', views.loginPage, name="login"),
    # This url sends the user to the logoutpage.
    path('logout/', views.logoutUser, name="logout"),
    # This url sends the user to the specified experiment pages.
    path('experiment/<str:pk>/', views.experiment, name="experiment"),
    # This url sends the user to the creation page of a new experiment.
    path('create/experiment/', views.createExperiment, name="create-experiment"),
    # This url sends the user to the deletion page of an experiment.
    path('delete/experiment/<str:pk>/', views.deleteExperiment, name="delete-experiment"),
    # This url sends the user to the questionpage.
    path('questions/', views.questions, name="questions"),
    # This url sends the user to the creation page of a new question.
    path('create/question/', views.createQuestion, name="create-question"),
    # This url sends the user to the deletion page of a question.
    path('delete/question/<str:pk>/', views.deleteQuestion, name="delete-question"),
]
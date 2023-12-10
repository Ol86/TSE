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
    path('create-experiment/', views.createExperiment, name="create-experiment"),
]
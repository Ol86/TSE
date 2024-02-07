#TODO: Add comments.
from django.urls import path
from . import views

urlpatterns = [
    path('create/watch/', views.registerWatch, name='create-watch'),
    path('create/user/', views.registerUser, name='create-user'),
    # This url sends the user to the deletion page of an experiment.
    path('delete/watch/<str:pk>/', views.deleteWatch, name="delete-watch"),
    path('watches/', views.watches, name="watches"),
]
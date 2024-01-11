from django.urls import path
from . import views

urlpatterns = [
    path('create/watch/', views.registerWatch, name='create-watch'),
    path('create/user/', views.registerUser, name='create-user'),
]
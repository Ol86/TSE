from django.urls import path
from . import views

urlpatterns = [
    path('register/watch/', views.registerWatch, name='register-watch'),
    path('register/user/', views.registerUser, name='register-user'),
]
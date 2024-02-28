# textverarbeitung/textapp/urls.py
from django.urls import path
from .views import text_input_view

urlpatterns = [
    path('sql_lab/', text_input_view, name='sql_lab'),
]

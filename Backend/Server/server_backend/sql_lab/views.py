from django.shortcuts import render, redirect
from django.contrib.auth.decorators import login_required

from . import labratory
from .forms import TextInputForm


@login_required(login_url='login')
def text_input_view(request):
    if request.method == 'POST':
        form = TextInputForm(request.POST)
        if form.is_valid():
            text = form.save()
            processed_text = labratory.translating(text)
            # return render(request, 'sql_lab/result.html', {'processed_text': processed_text})
            return render(request, 'sql_lab/result.html', {'form': form,
                                                           'original_text': text.content,
                                                           'processed_text': processed_text.content})
    else:
        form = TextInputForm()
    return render(request, 'sql_lab/input.html', {'form': form})

# todo Ausgabe wirklich nur auf SQL Befehl beschränken durch suchen der '''

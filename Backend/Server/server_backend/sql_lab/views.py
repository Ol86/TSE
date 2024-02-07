from django.shortcuts import render, redirect

from . import labratory
from .forms import TextInputForm


def text_input_view(request):
    if request.method == 'POST':
        form = TextInputForm(request.POST)
        if form.is_valid():
            text = form.save()
            processed_text = labratory.translating(text)
            # return render(request, 'myapp/result.html', {'processed_text': processed_text})
            return render(request, 'myapp/result.html', {'form': form,
                                                         'original_text': text.content,
                                                         'processed_text': processed_text.content})
    else:
        form = TextInputForm()
    return render(request, 'myapp/input.html', {'form': form})

#todo Ausgabe wirklich nur auf SQL Befehl beschränken durch suchen der '''
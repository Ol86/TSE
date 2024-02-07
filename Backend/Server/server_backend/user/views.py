# TODO: Add comments.
from django.shortcuts import render, redirect
from rest_framework.authtoken.models import Token
from django.contrib.auth.decorators import login_required
from django.contrib.auth import authenticate, login
from user.forms import WatchRegisterForm, UserRegisterForm
from user.models import Profile, Watch

# --------------------------------------------------------------------------------------------------- #
@login_required(login_url='login')
def registerWatch(response):
    if response.method == 'POST':
        form = WatchRegisterForm(response.POST)
        if form.is_valid():
            user = form.save()
            user.refresh_from_db()
            watch = Watch.objects.create(user=user)
            watch.mac_adress = form.cleaned_data.get('mac_adress')
            watch.save()
            Token.objects.create(user=user)
            user.type = 2
            user.is_admin = False
            user.save()

            return redirect('watches')

    else:
        form = WatchRegisterForm()

    return render(response, 'user/register_watch.html', {'form': form})

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - #
@login_required(login_url='login')
def watches(request):
    watches = Watch.objects.all()
    context = {'watches': watches}
    return render(request, 'user/watches.html', context)

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - #
@login_required(login_url='login')
def deleteWatch(request, pk):
    watch = Watch.objects.get(user_id=pk)
    if request.method == 'POST':
        watch.delete()
        return redirect('watches')
    return render(request, 'user/delete_watch.html', {'watch': watch})

# --------------------------------------------------------------------------------------------------- #
def registerUser(response):
    if response.method == 'POST':
        form = UserRegisterForm(response.POST)
        if form.is_valid():
            user = form.save()
            user.refresh_from_db()
            profile = Profile.objects.create(user=user)
            profile.role = form.cleaned_data.get('role')
            profile.save()
            user.type = 1
            user.is_admin = True
            user.save()

            username = form.cleaned_data.get('username')
            password = form.cleaned_data.get('password1')
            user = authenticate(username=username, password=password)
            login(response, user)

            return redirect('home')
        
    else:
        form = UserRegisterForm()

    return render(response, 'user/register_user.html', {'form': form})

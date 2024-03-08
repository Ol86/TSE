# Base packages for user auhtentication.
from django.contrib.auth.decorators import login_required
from django.contrib.auth import authenticate, login

# The base packages to render a webpage.
from django.shortcuts import render, redirect

# The packe to create api token.
from rest_framework.authtoken.models import Token

# The base models and the perpendicular forms.
from user.forms import WatchRegisterForm, UserRegisterForm
from user.models import Profile, Watch

# The api method for the user management in superset.
from user.api import entrypoint

# --------------------------------------------------------------------------------------------------- #
@login_required(login_url='login')
def registerWatch(response):
    """This function create the new watch.

    :param response: The response of the newly created watch.
    :return: The new watch.
    """
    if response.method == 'POST':
        form = WatchRegisterForm(response.POST)
        if form.is_valid():
            user = form.save()
            user.refresh_from_db()
            watch = Watch.objects.create(user=user)
            watch.serialnumber = form.cleaned_data.get('serialnumber')
            watch.is_running = False
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
    """This function displays all watches with their live status.

    :param request: The request of the user with the watches.
    :return: The watches overview.
    """
    watches = Watch.objects.all()
    context = {'watches': watches}
    return render(request, 'user/watches.html', context)

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - #
@login_required(login_url='login')
def deleteWatch(request, pk):
    """This funtion deletes a watch.

    :param request: The request to delete the watch.
    :param pk: The id of the watch to be deleted.
    :return: The watch overview.
    """
    watch = Watch.objects.get(user_id=pk)
    if request.method == 'POST':
        watch.delete()
        return redirect('watches')
    return render(request, 'user/delete_watch.html', {'watch': watch})

# --------------------------------------------------------------------------------------------------- #
def registerUser(response):
    """This function create the new user for the backend and superset.

    :param response: The response of the newly created user.
    :return: The new user.
    """
    if response.method == 'POST':
        form = UserRegisterForm(response.POST)
        if form.is_valid():
            user = form.save()
            user.refresh_from_db()
            profile = Profile.objects.create(user=user)
            profile.role = form.cleaned_data.get('role')
            profile.save()
            user.type = 1
            user.save()

            username = form.cleaned_data.get('username')
            password = form.cleaned_data.get('password1')
            
            id = Profile.objects.last().user.id
            entrypoint(username, password, True, id)
            user = authenticate(username=username, password=password)
            login(response, user)

            return redirect('home')
        
    else:
        form = UserRegisterForm()

    return render(response, 'user/register_user.html', {'form': form})

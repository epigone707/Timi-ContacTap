"""routing URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/3.2/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.urls import path
from app import views

urlpatterns = [
    path('admin/', admin.site.urls),
    path('login/', views.login, 'login'),
    path('contactinfo/', views.getcontactinfo, name='getcontactinfo'),
    path('contactinfo/create/', views.createcontactinfo, name='createcontactinfo'),
    path('contactinfo/update/', views.updatecontactinfo, name='updatecontactinfo'),
    path('profiles/', views.getprofiles, name='getprofiles'),
    path('profile/create/', views.createprofile, name='createprofile'),
    path('profile/update/', views.updateprofile, name='updateprofile'),
    path('profile/delete/', views.deleteprofile, name='deleteprofile'),
    path('connections/', views.getconnections, name='getconnections'),
    path('connection/create/', views.createconnection, name='createconnection'),
    path('connection/delete/', views.deleteconnection, name='deleteconnection')
]

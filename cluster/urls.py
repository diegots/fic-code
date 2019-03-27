from django.urls import path

from . import views

app_name = 'cluster'
urlpatterns = [
    path('', views.list, name='list'),
    path('list', views.list, name='list'),
    path('terminate', views.terminate, name='terminate'),
    path('terminate_action', views.terminate_action, name='terminate_action'),
    path('terminate_result', views.terminate_result, name='terminate_result'),
    path('launch', views.launch, name='launch'),
    path('launch_action', views.launch_action, name='launch_action'),
    path('launch_result', views.launch_result, name='launch_result')
]


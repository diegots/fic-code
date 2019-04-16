from django.urls import path

from . import views

app_name = 'dashboard'
urlpatterns = [
    path('', views.index, name='index'),
    path('cluster/launch', views.cluster_launch, name='cluster-launch'),
    path('cluster/launch_action', views.cluster_launch_action, name='cluster-launch-action'),
    path('cluster/launch_result', views.cluster_launch_result, name='cluster-launch-result'),
    path('cluster/list', views.cluster_list, name='cluster-list'),
    path('cluster/terminate', views.cluster_terminate, name='cluster-terminate'),
    path('cluster/terminate_action', views.cluster_terminate_action, name='cluster-terminate-action'),
    path('cluster/terminate_result', views.cluster_terminate_result, name='cluster-terminate-result'),
    path('recommend', views.recommend, name='recommend'),

    path('recommend/prepare_action', views.recommend_prepare_action, name='recomm-prepare'),
    path('recommend/prepare_result', views.recommend_prepare_result, name='recomm-prepare-result'),
    path('recommend/generate_shards', views.recomm_generate_shards, name='recomm-generate-shards'),
    path('recommend/generate_users', views.recomm_generate_users, name='recomm-generate-users'),
    path('recommend/compute', views.recomm_compute, name='recomm-compute'),
    path('result', views.result, name='result'),
]



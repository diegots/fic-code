from django.urls import path

from . import views

app_name = 'dashboard'

urlpatterns = [
    path('', views.index, name='index'),

    path('cluster/launch', views.cluster_launch, name='cluster-launch'),

    path('cluster/launch_action',
         views.cluster_launch_action,
         name='cluster-launch-action'),

    path('cluster/launch_result',
         views.cluster_launch_result,
         name='cluster-launch-result'),

    path('cluster/list', views.cluster_list, name='cluster-list'),

    path('cluster/terminate',
         views.cluster_terminate,
         name='cluster-terminate'),

    path('cluster/terminate_action',
         views.cluster_terminate_action,
         name='cluster-terminate-action'),

    path('cluster/terminate_result',
         views.cluster_terminate_result,
         name='cluster-terminate-result'),

    path('recommend', views.recommend, name='recommend'),

    path('recommend/load_action',
         views.recommend_load_action,
         name='recommend-load-action'),

    path('recommend/unique_items_action',
         views.recommend_unique_items_action,
         name='recommend_unique_items_action'),

    path('recommend/load_result',
         views.recommend_load_result,
         name='recommend-load-result'),

    path('recommend/generate_shards_action',
         views.recommend_generate_shards_action,
         name='recommend-generate-shards-action'),

    path('recommend/generate_active_users',
         views.recommend_generate_active_users_action,
         name='recommend-generate-active-users'),

    path('recommend/compute',
         views.recommend_compute_action,
         name='recommend-compute-action'),

    path('result', views.result, name='result'),
]




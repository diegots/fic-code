from django.db import models

class Cluster(models.Model):
    cluster_id = models.CharField(max_length=30) # cluster id example: j-1LE1XBWJF3OCF
    number_nodes = models.IntegerField()

    def __str__(self):
        return 'aws emr id: ' + self.cluster_id + ', with ' + str(self.number_nodes) + ' nodes'

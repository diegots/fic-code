from django.db import models


class Cluster(models.Model):

    # cluster id example: j-1LE1XBWJF3OCF
    cluster_id = models.CharField(primary_key=True, max_length=30)

    # Name given to this cluster
    cluster_name = models.TextField(max_length=30,
                                    null=False,
                                    default='unnamed')

    # number of nodes of the cluster
    cluster_number_nodes = models.IntegerField()

    # date when the cluster turn on and off
    cluster_ready_date_time = models.DateTimeField(null=True)
    cluster_end_date_time = models.DateTimeField(null=True)

    # master public dns used to ssh into the cluster
    cluster_master_public_dns_name = models.TextField(max_length=60, null=True)

    def __str__(self):
        return 'EMR Cluster description -> ' \
               + 'id: ' + str(self.cluster_id) \
               + ', number_nodes: ' + str(self.cluster_name) \
               + ', node(s) ' + str(self.cluster_number_nodes) \
               + ', master dns: ' + str(self.cluster_master_public_dns_name)

#
# Algorithms parameters
#
K=5 # Number of neighbors
SPLITS_NUMBER=3 # Shards and reducer must be equal for computing recomendations


#
# Dirs and filenames
#

# Project base dirs
ARTIFACTS=out/artifacts

HDFS=
LOCAL=~/IdeaProjects/p1811002

ACTIVE_USERS=active-users
CACHED=cached
DATASET=dataset
INPUT=input
OUT=tmp
RECOMM=recommendations
UNIQUE_ITEMS=unique-items

# Datasets
DS_FILE=test-data-25.csv
#DS_FILE=movielens-100k.csv
#DS_FILE=movielens-20m.csv
DS=$(LOCAL)/$(INPUT)/$(DATASET)/$(DS_FILE)

# Jar files
GENERATE_JAR=$(LOCAL)/$(ARTIFACTS)/p1811002_generate/p1811002_generate.jar
HADOOP_RECOMENDATIONS_JAR=$(LOCAL)/$(ARTIFACTS)/p1811002_hadoop_recomendations/p1811002_hadoop-recomendations.jar
HADOOP_UNIQUE_ITEMS_JAR=$(LOCAL)/$(ARTIFACTS)/p1811002_hadoop_unique_items/hadoop-unique-items.jar

# Neighborhood computed data
A=encoded.sim.mat
B=encoded.user.ids
C=encoded.users.k.neighbors
D=plain.frequency.table
E=encoded.reassigned.sim.mat
SIMILARITIES_DEST=$(LOCAL)/$(INPUT)/similarities

# Shards generation variables
SHARDS_DEST=$(LOCAL)/$(INPUT)/shards
SHARDS_PREFIX=shard


#
# Targets
#
generate: generate-shards generate-similarities

generate-shards:
	java -jar $(GENERATE_JAR) -v -matrix $(DS) $(SPLITS_NUMBER) $(SHARDS_PREFIX)
	mv -v $(SHARDS_PREFIX)* $(SHARDS_DEST) 

generate-similarities:
	java -jar $(GENERATE_JAR) -v -neighborhood $(DS) $(K) $(A) $(B) $(C) $(D) $(E)  
	mv -v encoded.* $(SIMILARITIES_DEST) 
	mv -v plain.* $(SIMILARITIES_DEST)

generate-help:
	java -jar $(JAR) -h

generate-clean:
	rm -rfv $(SIMILARITIES_DEST)/* $(SHARDS_DEST)/*

hadoop-start: 
	hdfs namenode -format -force
	/usr/local/hadoop/sbin/start-dfs.sh
	#$$HADOOP_HOME/sbin/mr-jobhistory-daemon.sh start historyserver

hadoop-prepare: 
	hdfs dfs -mkdir $(HDFS)/$(CACHED) $(HDFS)/$(ACTIVE_USERS) $(HDFS)/$(DATASET)
	hdfs dfs -put $(LOCAL)/$(INPUT)/$(ACTIVE_USERS)/users.csv $(HDFS)/$(ACTIVE_USERS)
	hdfs dfs -put $(DS) $(HDFS)/$(DATASET)
	for i in $(SIMILARITIES_DEST)/*; do hdfs dfs -put $$i $(HDFS)/$(CACHED); done
	for i in $(SHARDS_DEST)/*; do hdfs dfs -put $$i $(HDFS)/$(CACHED); done

hadoop-unique-items: 
	hdfs dfs -rm -f -R $(HDFS)/$(UNIQUE_ITEMS)*
	$(shell export HADOOP_CLASSPATH=$(HADOOP_ACTIVE_USERS_JAR):$HADOOP_CLASSPATH)
	cd /$(OUT) && hadoop jar $(HADOOP_UNIQUE_ITEMS_JAR) $(HDFS)/$(DATASET) /$(UNIQUE_ITEMS) 3

hadoop-recomendations: 
	hdfs dfs -rm -f -R $(HDFS)/$(RECOMM)*
	$(shell export HADOOP_CLASSPATH=$(HADOOP_RECOMENDATIONS_JAR):$HADOOP_CLASSPATH)
	cd /$(OUT) && hadoop jar $(HADOOP_RECOMENDATIONS_JAR) $(HDFS)/$(DATASET) $(HDFS)/$(RECOMM) $(HDFS)/$(ACTIVE_USERS)/users.csv $(HDFS)/$(UNIQUE_ITEMS)/part-r-00000 $(SPLITS_NUMBER)

hadoop-stop: 
	/usr/local/hadoop/sbin/stop-all.sh

hadoop-clean: 
	hdfs dfs -rm -R $(HDFS)/$(CACHED) $(HDFS)/$(ACTIVE_USERS) $(HDFS)/$(DATASET)

.PHONY: generate generate-shards generate-similarities generate-help generate-clean hadoop-start hadoop-prepare hadoop-unique-items hadoop-recomendations hadoop-stop hadoop-clean


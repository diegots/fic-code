# Project base dir
BASE=~/IdeaProjects/p1811002
OUT=/tmp
ARTIFACTS=out/artifacts

# Jar files
GENERATE_JAR=$(BASE)/$(ARTIFACTS)/p1811002_generate/p1811002_generate.jar
HADOOP_ACTIVE_USERS_JAR=$(BASE)/$(ARTIFACTS)/p1811002_hadoop_active_users/hadoop-active-users.jar
HADOOP_RECOMENDATIONS_JAR=$(BASE)/$(ARTIFACTS)/p1811002_hadoop_recomendations/p1811002_hadoop-recomendations.jar

# Datasets
DS=$(BASE)/input/dataset/test-data-25.csv
#DS=$(BASE)/input/dataset/movielens-100k.csv

# Similarities generation variables
SIMILARITY_DATA=$(BASE)/input/similarity-data
K=5

# Neighborhood generation output file names
A=$(OUT)/encoded.sim.mat
B=$(OUT)/encoded.user.ids
C=$(OUT)/encoded.users.k.neighbors
D=$(OUT)/plain.frequency.table
E=$(OUT)/encoded.reassigned.sim.mat

# Active users generation
REDUCERS_NUMBER=3

# Shard generation variables
SHARDS_NUMBER=3
SHARDS_PREFIX=shard
SHARDS_DATA=$(BASE)/input/shards

generate: generate-shards generate-similarities

generate-shards:
	java -jar $(GENERATE_JAR) -v -matrix $(DS) $(SHARDS_NUMBER) $(SHARDS_PREFIX)
	mv -v $(SHARDS_PREFIX)* $(SHARDS_DATA) 

generate-similarities:
	java -jar $(GENERATE_JAR) -v -neighborhood $(DS) $(K) $(A) $(B) $(C) $(D) $(E)  
	mv -v $(OUT)/encoded.* $(SIMILARITY_DATA) 
	mv -v $(OUT)/plain.* $(SIMILARITY_DATA)

generate-help:
	java -jar $(JAR) -h

generate-clean:
	rm -fv $() $(SIMILARITY_DATA)/* $(SHARDS_DATA)/*

hadoop-start: 
	hdfs namenode -format -force
	/usr/local/hadoop/sbin/start-dfs.sh
	#$$HADOOP_HOME/sbin/mr-jobhistory-daemon.sh start historyserver

hadoop-prepare: 
	hdfs dfs -mkdir /input /cached /dataset
	hdfs dfs -put $(DS) /dataset
	hdfs dfs -put $(BASE)/input/active-user/active-user.csv /input
	for i in $(SHARDS_DATA)/*; do hdfs dfs -put $$i /cached; done
	for i in $(SIMILARITY_DATA)/*; do hdfs dfs -put $$i /cached; done

hadoop-active-users: 
	hdfs dfs -rm -f -R /active-users*
	$(shell export HADOOP_CLASSPATH=$(HADOOP_ACTIVE_USERS_JAR):$HADOOP_CLASSPATH)
	cd /tmp && hadoop jar $(HADOOP_ACTIVE_USERS_JAR) /dataset /active-users $(REDUCERS_NUMBER)

hadoop-recomendations: 
	hdfs dfs -rm -f -R /recommendations*
	$(shell export HADOOP_CLASSPATH=$(HADOOP_RECOMENDATIONS_JAR):$HADOOP_CLASSPATH)
	cd /tmp && hadoop jar $(HADOOP_RECOMENDATIONS_JAR) /active-users /recommendations $(SHARDS_NUMBER)

hadoop-stop: 
	/usr/local/hadoop/sbin/stop-all.sh

hadoop-clean: 
	hdfs dfs -rm -R /input /cached /dataset

.PHONY: generate generate-shards generate-similarities generate-help generate-clean hadoop-start hadoop-prepare hadoop-active-users hadoop-recomendations hadoop-stop hadoop-clean


# Project base dir
BASE=~/IdeaProjects/p1811002
OUT=/tmp

# Jar files
HADOOP_JAR=$(BASE)/out/artifacts/p1811002_hadoop/p1811002_hadoop.jar
GENERATE_JAR=$(BASE)/out/artifacts/p1811002_generate/p1811002_generate.jar

# Datasets
DS=$(BASE)/input/dataset/movielens-100k.csv

# Similarities generation variables
SIMILARITY_DATA=$(BASE)/input/similarity-data
K=5

# Neighborhood generation output file names
A=$(OUT)/encoded.sim.mat
B=$(OUT)/encoded.user.ids
C=$(OUT)/encoded.users.k.neighbors
D=$(OUT)/plain.frequency.table
E=$(OUT)/encoded.reassigned.sim.mat

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
	rm -fv $(SIMILARITY_DATA)/* $(SHARDS_DATA)/*

hadoop-start: 
	hdfs namenode -format -force
	/usr/local/hadoop/sbin/start-dfs.sh
	$$HADOOP_HOME/sbin/mr-jobhistory-daemon.sh start historyserver

hadoop-prepare: 
	hdfs dfs -mkdir /input /cached
	hdfs dfs -put $(BASE)/input/active-user/active-user.csv /input
	for i in $(SHARDS_DATA)/*; do hdfs dfs -put $$i /cached; done
	for i in $(SIMILARITY_DATA)/*; do hdfs dfs -put $$i /cached; done

hadoop-jar: 
	hdfs dfs -rm -f -R /job1-out
	$(shell export HADOOP_CLASSPATH=$(HADOOP_JAR):$HADOOP_CLASSPATH)
	cd /tmp && hadoop jar $(HADOOP_JAR) /input $(SHARDS_NUMBER)

hadoop-stop: 
	/usr/local/hadoop/sbin/stop-all.sh

hadoop-clean: 
	hdfs dfs -rm -R /input /cached

.PHONY: generate generate-shards generate-similarities generate-help generate-clean hadoop-start hadoop-prepare hadoop-jar hadoop-stop hadoop-clean


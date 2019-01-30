BASE=~/IdeaProjects/p1811002

HADOOP_JAR=$(BASE)/out/artifacts/p1811002_hadoop/p1811002_hadoop.jar
GENERATE_JAR=$(BASE)/out/artifacts/p1811002_generate/p1811002_generate.jar

DS=$(BASE)/input/dataset/movielens-100k.csv

SIMILARITY_DATA=$(BASE)/input/similarity-data
K=5

SHARDS_NUMBER=3
SHARDS_PREFIX=shard
SHARDS_DATA=$(BASE)/input/shards

# Neighborhood generation file names
A=encoded.sim.mat
B=encoded.user.ids
C=encoded.users.k.neighbors
D=plain.frequency.table
E=encoded.reassigned.sim.mat

generate: generate-shards generate-similarities

generate-shards:
	java -jar $(GENERATE_JAR) -v -matrix $(DS) $(SHARDS_NUMBER) $(SHARDS_PREFIX)
	mv -v $(SHARDS_PREFIX)* $(SHARDS_DATA) 

generate-similarities:
	java -jar $(GENERATE_JAR) -v -neighborhood $(DS) $(K) $(A) $(B) $(C) $(D) $(E)  
	mv -v encoded.* $(SIMILARITY_DATA) 
	mv -v plain.* $(SIMILARITY_DATA)

generate-help:
	java -jar $(JAR) -h

generate-clean:
	rm -fv $(SIMILARITY_DATA)/* $(SHARDS_DATA)/*

hadoop-start: 
	hdfs namenode -format -force
	/usr/local/hadoop/sbin/start-dfs.sh
	$(shell $HADOOP_HOME/sbin/mr-jobhistory-daemon.sh start historyserver)

hadoop-prepare: 
	hdfs dfs -mkdir /input /cached
	hdfs dfs -put $(BASE)/input/active-user/active-user.csv /input
	for i in $(SHARDS_DATA)/*; do hdfs dfs -put $$i /cached; done
	for i in $(SIMILARITY_DATA)/*; do hdfs dfs -put $$i /cached; done

hadoop-jar: 
	hdfs dfs -rm -f -R /job1-out
	$(shell export HADOOP_CLASSPATH=$(HADOOP_JAR):$HADOOP_CLASSPATH)
	cd /tmp && hadoop jar $(HADOOP_JAR) /input 3

hadoop-stop: 
	/usr/local/hadoop/sbin/stop-all.sh

hadoop-clean: 
	hdfs dfs -rm -R /input /cached

.PHONY: generate generate-shards generate-similarities generate-help generate-clean hadoop-start hadoop-prepare hadoop-jar hadoop-stop hadoop-clean


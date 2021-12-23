package com.company;

import com.company.recommender.SimpleUserBasedKnn;
import com.company.recommender.SimpleUserBasedKnnImpl;
import org.apache.commons.cli.*;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        String TAG = "[Main:main] ";

        System.err.print(TAG + "Starting...\n");

        // Path to data
        //String PATH = "/home/diego/1.workspace/fic-tfg/dataset-movielens-small/u.data";

        Options options = new Options();

        Option path = new Option("p","path",true,"Path to dataset");
        path.setRequired(true);
        options.addOption(path);

        Option user = new Option("u","user",true,"User from who obtain recommendations");
        user.setRequired(true);
        options.addOption(user);

        Option n = new Option("n","numberRecs",true,"Number of recommendations to compute");
        n.setRequired(true);
        options.addOption(n);

        Option k = new Option("k","neighborSize",true,"User's neighborhood size");
        k.setRequired(true);
        options.addOption(k);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine commandLine;

        try {
            commandLine = parser.parse(options,args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            formatter.printHelp("Simple Knn Recommender", options);

            System.exit(1);
            return;
        }

        String dataSetPath = commandLine.getOptionValue("path");
        System.out.println(TAG + "Reading dataset from" + dataSetPath);

        SimpleUserBasedKnn simpleRecommender = new SimpleUserBasedKnnImpl(dataSetPath);
        List<Integer> recommendations = simpleRecommender.recommendedItems(
                Integer.parseInt(commandLine.getOptionValue("user")),
                Integer.parseInt(commandLine.getOptionValue("numberRecs")),
                Integer.parseInt(commandLine.getOptionValue("neighborSize")));

        System.out.println(TAG + "System recommends: ");
        for (Integer i: recommendations)
            System.out.println(TAG + "item: " + i);
    }
}

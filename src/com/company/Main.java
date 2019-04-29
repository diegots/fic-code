package com.company;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class Main {

    static String separator = ",";
    static File input;
    static File outProfiles;
    static File outFile;

    public static void main(String[] args) {

        input = new File(args[0]);
        outProfiles = new File("profiles-" + args[1]);
        outFile = new File(args[1]);

        /**
         * UserId máximo
         */
        System.out.println("Calcula el userId máximo");
        int maxUserId = 0;
        String readLine;
        try {
            BufferedReader br = new BufferedReader(new FileReader(input));
            while ((readLine = br.readLine()) != null) {
                if (Integer.valueOf(readLine.split(separator)[0]) > maxUserId) {
                    maxUserId = Integer.valueOf(readLine.split(separator)[0]);
                }
            }
            br.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }


        /**
         *
         */
        List<Integer> itemIds;
        List<Double> ratings;

        /**
         * Calcula perfiles
         */
        System.out.print("Calcula perfiles");
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(outProfiles, false));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        for (int userId = 1; userId<=maxUserId; userId++) {
            itemIds = new LinkedList<>();
            ratings = new LinkedList<>();

            /**
             * Leer perfil del usuario
             */
            Double denom = 0.0;
            try {
                BufferedReader br = new BufferedReader(new FileReader(input));
                while ((readLine = br.readLine()) != null) {
                    if (Integer.valueOf(readLine.split(separator)[0]) == userId) {
                        itemIds.add(Integer.valueOf(readLine.split(separator)[1]));
                        Double rating = Double.valueOf(readLine.split(separator)[2]);
                        ratings.add(rating);
                        denom += rating * rating;
                    }
                }
                br.close();
                System.out.print(".");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                System.exit(1);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }

            /**
             * Escribir perfil a disco
             */
            StringBuilder line = new StringBuilder();
            line.append(userId + "," + Math.sqrt(denom) + ",");
            for (int i=0; i<itemIds.size(); i++) {
                line.append(itemIds.get(i) + "," + ratings.get(i) + ",");
            }
            line.append("\n");

            try {
                bufferedWriter.append(line);

            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        /**
         * Cierra fichero de perfiles
         */
        try {
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        /**
         * Calcula similaridades
         */
        System.out.print("\nCalcula similaridades");
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(outFile, false));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        String lineA;
        String lineB;
        try {
            BufferedReader brA = new BufferedReader(new FileReader(outProfiles));
            while ((lineA = brA.readLine()) != null) {
                String [] valuesA = lineA.split(separator);
                Integer userIdA = Integer.valueOf(valuesA[0]);


                try {
                    BufferedReader brB = new BufferedReader(new FileReader(outProfiles));
                    while ((lineB = brB.readLine()) != null) {
                        String[] valuesB = lineB.split(separator);
                        Integer userIdB = Integer.valueOf(valuesB[0]);

                        Double sum = 0.0;
                        if (userIdB > userIdA) {
                            for (int itemB = 2; itemB<valuesB.length; itemB+=2) {

                                for (int itemA = 2; itemA<valuesA.length; itemA+=2) {

                                    if (valuesA[itemA].equals(valuesB[itemB])) {
                                        sum += Double.valueOf(valuesA[itemA+1]) * Double.valueOf(valuesB[itemB+1]);
                                    }
                                }
                            }

                            bufferedWriter.append(userIdA + "\t" + userIdB + "\t"
                                    + sum/(Double.valueOf(valuesA[1]) + Double.valueOf(valuesB[1])) + "\n");
                        }
                    }
                    brB.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    System.exit(1);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
                System.out.print(".");
            }
            brA.close();
            bufferedWriter.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }


    }
}

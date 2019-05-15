package tfg;

import java.io.*;

public class ComputeProfile extends Thread {

    private String threadName;
    private int startId;
    private int endId;

    public ComputeProfile(String threadName, int startId, int endId) {
        this.threadName = threadName;
        this.startId = startId;
        this.endId = endId;
    }


    @Override
    public void run() {
        System.out.println("Running thread " + threadName);

        /**
         * Abre ficheros
         */
        BufferedWriter writer = null;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(Main.inputFile));
            writer = new BufferedWriter(new FileWriter(new File("profile"+threadName+"-"+Main.outputFile), false));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /**
         * Extrae profile
         */
        String line;
        StringBuilder profile = new StringBuilder();
        Double denom = 0.0;
        try {
            while ((line = reader.readLine()) != null) {
                int userId = Integer.valueOf(line.split(Main.separator)[0]);
                if (userId < startId) {
                    continue;
                } else if (userId > endId) {

                    if (profile.length() > 0) { // Último usuario, volcar a disco
                        writer.append(profile.append(Main.separator + Math.sqrt(denom)).append("\n").toString());
                    }

                    break;
                } else {

                    String itemId = line.split(Main.separator)[1];
                    String rating = line.split(Main.separator)[2];

                    if (profile.length() == 0) { // No tengo nada. Primera linea del fichero
                        profile.append(userId);
                        profile.append(Main.separator + itemId + Main.separator + rating);
                        denom += Double.valueOf(rating) * Double.valueOf(rating);

                    } else { // Ya hay información previa
                        if (profile.toString().split(Main.separator)[0].equals(""+userId)) { // Mismo usuario
                            profile.append(Main.separator + itemId + Main.separator + rating);
                            denom += Double.valueOf(rating) * Double.valueOf(rating);

                        } else { // Distinto usuario

                            // Almacenar la información en fichero
                            writer.append(profile.append(Main.separator + Math.sqrt(denom)).append("\n").toString());

                            // Guardar nueva cadena
                            profile = new StringBuilder().append(userId);
                            profile.append(Main.separator + itemId + Main.separator + rating);
                            denom = Double.valueOf(rating) * Double.valueOf(rating);

                            //System.out.printf(".");
                        }
                    }
                }
            }

            if (line == null) { // Trata la última linea del fichero
                writer.append(profile.append(Main.separator + Math.sqrt(denom)).append("\n").toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        /**
         * Cierra ficheros
         */
        try {
            reader.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("Thread " + threadName + " exiting");
    }
}

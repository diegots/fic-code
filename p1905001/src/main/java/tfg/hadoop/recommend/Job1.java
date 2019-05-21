package tfg.hadoop.recommend;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Job1 {

    public static class Map extends Mapper<LongWritable, Text, IntWritable, ActiveUser> {

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            // Envía a cada reducer/shard el usuario activo actual
            int max = context.getConfiguration().getInt(Main.NUMBER_OF_SHARDS, -1);
            for (int shard=0; shard<max; shard++) {
                context.write(new IntWritable(shard), new ActiveUser(value));
            }
        }
    }

    public static class Reduce extends Reducer<IntWritable, ActiveUser, IntWritable, PairWritable> {

        java.util.Map<Integer, java.util.Map<Integer, Double>> shard;
        static int numberOfSimilarityFiles;

        private List<Integer> getNeighbors(Integer activeUser) throws IOException {
            List<Integer> neighbors = new ArrayList<>();
            boolean found = false;
            for (int i=0; i<numberOfSimilarityFiles; i++) {
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(
                                    new File(Main.SIMILARITY_NEIGHBOR_PREFIX
                                            + i
                                            + Main.SIMILARITY_NEIGHBOR_SUFFIX).getName())));
                String line = bufferedReader.readLine();
                while (line != null) {
                    String[] lineParts = line.split(",");
                    if (activeUser == Integer.valueOf(lineParts[0])) {
                        // Leer todos los vecinos
                        for (int j=1; j<lineParts.length; j++) {
                            neighbors.add(Integer.valueOf(lineParts[j]));
                        }
                        bufferedReader.close();
                        found = true;
                        break;
                    }

                    line = bufferedReader.readLine();
                }
                if (found) {
                    break;
                }
            }

            return neighbors;
        }

        @Override
        protected void setup(Context context) {
            numberOfSimilarityFiles = context.getConfiguration().getInt(Main.SIMILARITY_NEIGHBOR_NUMBER, -1);
        }

        @Override
        protected void reduce(IntWritable key, Iterable<ActiveUser> values, Context context)
                throws IOException, InterruptedException {

            // Abrir shard para lectura. El shard del nodo depende de la clave recibida.
            // El shard contiene el perfil de cada user Id que se trata en este nodo
            shard = Util.mapFromFile(
                    new FileInputStream(
                            new File(Main.SHARD_NAME_PREFIX+key.get()).getName()));

            // Para cada usuario activo
            for (ActiveUser activeUser: values) {

                List<Integer> neighbors = getNeighbors(activeUser.getUserId().get()); // Obtener top vecinos
                for (Integer neighbor: neighbors) {

                    if (!shard.containsKey(neighbor)) {
                        continue;
                    } else {

                        // Obtener similaridad entre usuario activo y vecino / 1000
                        float similarity = 0.0f;

                        for (Integer nonRatedItem: activeUser.getNonRatedItemsArray()) {

                            // Obtener valoración del vecino para ese item
                            Double rating = shard.get(neighbor).get(nonRatedItem);
                            if (rating != null) {
                                context.write(
                                        activeUser.getUserId(),
                                        new PairWritable(
                                                new IntWritable(nonRatedItem),
                                                new DoubleWritable(similarity*rating)));
                            }
                        }
                    }
                }
            }
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            super.cleanup(context);
        }
    }

}

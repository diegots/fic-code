package tfg.common.util;

import it.unimi.dsi.io.InputBitStream;
import it.unimi.dsi.io.OutputBitStream;
import org.apache.commons.collections4.map.LinkedMap;
import tfg.generate.Conf;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class Utilities {

  public static <K, V extends Comparable<? super V>> LinkedMap<K, V> sortMapByValue(Map<K, V> map) {
    List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());

    Collections.sort( list, new Comparator<Map.Entry<K, V>>() {
      @Override
      public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
        return (o2.getValue()).compareTo(o1.getValue());
      }
    });

    LinkedMap<K, V> result = new LinkedMap<>();

    for (Map.Entry<K, V> entry : list)
      result.put(entry.getKey(), entry.getValue());

    return result;
  }

  public static <K, V> void mapToFile(OutputStream stream, Map<K, V> map) {
    try {
      ObjectOutputStream objectStream = new ObjectOutputStream(stream);
      objectStream.writeObject(map);
      objectStream.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static <K, V> Map<K, V> mapFromFile(InputStream stream) {

    Map<K, V> results = null;

    try {
      ObjectInputStream objectStream = new ObjectInputStream(stream);
      results = (Map<K, V>) objectStream.readObject();

      objectStream.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

    return results;
  }

  public static void writeLine(String outFile, String line) {
    BufferedWriter bufferedWriter;
    try {
      bufferedWriter = new BufferedWriter(new FileWriter(outFile, true));
      bufferedWriter.append(line);
      bufferedWriter.append('\n');
      bufferedWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void writeListToFileAsDeltas(String outFile, List<Integer> line) {

    Iterator<Integer> iterator = line.iterator();
    try {
      FileOutputStream fos = new FileOutputStream(outFile, true);
      OutputBitStream obs = new OutputBitStream(fos);

      while (iterator.hasNext()) {
        obs.writeDelta(iterator.next());
      }
      obs.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static List<Integer> readFileToListAsDeltas(String inFile) {

    List<Integer> res = new ArrayList();

    try {
      FileInputStream fis = new FileInputStream(inFile);
      InputBitStream ibs = new InputBitStream(fis);

      while (ibs.available() > 0) {
        int i = ibs.readDelta();
        res.add(i);
      }
      ibs.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (EOFException e) {
      // This is not an exception, the input was readSeekable completelly

    } catch (IOException e) {
      e.printStackTrace();
    }

    return res;
  }

  public static List<Integer> readOneRow(String inFile) {
    List<Integer> res = new ArrayList<>();

    try {
      FileInputStream fis = new FileInputStream(inFile);
      InputBitStream ibs = new InputBitStream(fis);

      while (ibs.available() > 0) {
        int i = ibs.readDelta();

        if (Conf.get().SIMILARITY_ROWS_DELIMITER == i) {
          break;
        }

        res.add(i);
      }
      ibs.close();

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (EOFException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return res;
  }

  public static void checkNull (Object o) {
    if (null == o) {
      throw new NullPointerException();
    }
  }

  public static long milisecondsToSeconds (long miliSeconds) {
    return miliSeconds / 1000;
  }

  public static void deleteFile(Path path) {
    //System.out.println("toAbsolutePath: " + path.toAbsolutePath().toString());

    File f = new File(path.toString());
    if (f.exists()) {
      f.delete();
    }
  }
}

package tfg.common.util;

import it.unimi.dsi.io.InputBitStream;
import it.unimi.dsi.io.OutputBitStream;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import tfg.generate.Conf;

import java.io.*;
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

  public static void objectToFile (OutputStream stream, Object o) {
    try {
      ObjectOutputStream objectStream = new ObjectOutputStream(stream);
      objectStream.writeObject(o);
      objectStream.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static <K, V> Map<K, V> objectFromFile (InputStream stream) {

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

  public static void writeLine (String outFile, String line) {
    BufferedWriter bw;
    try {
      bw = new BufferedWriter(new FileWriter(outFile, true));
      bw.append(line);
      bw.append('\n');
      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void writeLine (String outFile, List<Integer> line) {

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

  public static List<Integer> readFileInHDFS(FileStatus inFile) {
    List<Integer> res = new ArrayList();

    try {
      FileSystem fs = FileSystem.get(new Configuration());
      BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(inFile.getPath())));
      String line = br.readLine();
      while (line != null) {
        res.add(Integer.valueOf(line.trim()));
        line = br.readLine();
      }
      br.close();
      fs.close();
    } catch (IOException e) {
      e.printStackTrace();
    }


    return res;
  }

  public static List<Integer> readFile(File inFile) {
    List<Integer> res = new ArrayList();
    try {
      BufferedReader br = new BufferedReader(new FileReader(inFile));
      String line = br.readLine();
      while (line != null) {
        res.add(Integer.valueOf(line.trim()));
        line = br.readLine();
      }
      br.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return res;
  }

  public static List<Integer> readFileAsBitStream(String inFile) {

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

  public static List<FileStatus> getNonEmptyFilesInHDFSFolder(String path) {
    List<FileStatus> result = new ArrayList<>();
    try {
      FileSystem fs = FileSystem.get(new Configuration());
      FileStatus[] statuses = fs.listStatus(new Path(path));
      for (FileStatus fileStatus: statuses) {
        if (fileStatus.isFile() && fileStatus.getLen() > 0) {
          result.add(fileStatus);
        }
      }
      fs.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return result;
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
}

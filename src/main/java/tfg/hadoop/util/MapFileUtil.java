package tfg.hadoop.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.MapFile;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MapFileUtil {

  public static MapFile.Writer createMapFileWriter (
      String parent,
      String child,
      Configuration conf,
      java.lang.Class<? extends WritableComparable<?>> keyClass,
      java.lang.Class<? extends Writable> valueClass) throws IOException {

    return new MapFile.Writer(
        conf,
        new Path(parent, child),
        MapFile.Writer.keyClass(keyClass),
        MapFile.Writer.valueClass(valueClass));
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
}

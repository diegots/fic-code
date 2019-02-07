package tfg.hadooprec.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.MapFile;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;

import java.io.IOException;

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

  public static void shardToMapFile (MapFile.Writer writer) {

  }
}

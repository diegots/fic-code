package hadoop.model;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class PairWritable implements WritableComparable<PairWritable> {

  private final IntWritable first;
  private final IntWritable second;

  public PairWritable(IntWritable first, IntWritable second) {
    this.first = first;
    this.second = second;
  }

  public IntWritable getFirst() {
    return new IntWritable(first.get());
  }

  public IntWritable getSecond() {
    return new IntWritable(second.get());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PairWritable pairWritable = (PairWritable) o;
    return first.equals(pairWritable.first) &&
        second.equals(pairWritable.second);
  }

  @Override
  public int hashCode() {
    return first.hashCode() * 163 + second.hashCode();
  }

  @Override
  public String toString() {
    return first.get() + "\t" + second.get();
  }

  @Override
  public int compareTo(PairWritable o) {
    int cmp = first.compareTo(o.first);
    if (cmp != 0)
      return cmp;

    return second.compareTo(o.second);
  }

  @Override
  public void write(DataOutput dataOutput) throws IOException {
    first.write(dataOutput);
    second.write(dataOutput);
  }

  @Override
  public void readFields(DataInput dataInput) throws IOException {
    first.readFields(dataInput);
    second.readFields(dataInput);
  }
}

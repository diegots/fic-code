package tfg.hadoop.recommend;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class TripleWritable implements WritableComparable<TripleWritable> {

  private final IntWritable first;
  private final IntWritable second;
  private final DoubleWritable third;

  public TripleWritable() {
    first = new IntWritable(0);
    second = new IntWritable(0);
    third = new DoubleWritable(0);
  }

  public TripleWritable(IntWritable first, IntWritable second, DoubleWritable third) {
    this.first = first;
    this.second = second;
    this.third = third;
  }

  public IntWritable getFirst() {
    return first;
  }

  public IntWritable getSecond() {
    return second;
  }

  public DoubleWritable getThird() {
    return third;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TripleWritable tripleWritable = (TripleWritable) o;
    return first.equals(tripleWritable.first) &&
        second.equals(tripleWritable.second) &&
        third.equals(tripleWritable.third);
  }

  @Override
  public int hashCode() {
    return first.hashCode()
        + 163 * second.hashCode()
        + 163 * third.hashCode();
  }

  @Override
  public String toString() {
    return first.get() + "\t" + second.get() + "\t" + third.get();
  }

  @Override
  public int compareTo(TripleWritable o) {
    int cmp = first.compareTo(o.first);
    if (cmp != 0)
      return cmp;

    cmp = second.compareTo(o.second);
    if (cmp != 0)
      return cmp;

    return third.compareTo(o.third);
  }

  @Override
  public void write(DataOutput dataOutput) throws IOException {
    first.write(dataOutput);
    second.write(dataOutput);
    third.write(dataOutput);
  }

  @Override
  public void readFields(DataInput dataInput) throws IOException {
    first.readFields(dataInput);
    second.readFields(dataInput);
    third.readFields(dataInput);
  }
}

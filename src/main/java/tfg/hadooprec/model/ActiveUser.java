package tfg.hadooprec.model;

import org.apache.hadoop.io.*;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ActiveUser implements WritableComparable<ActiveUser> {

  private IntWritable userId;
  private ArrayWritable nonRatedItems;

  public ActiveUser() {
    userId = new IntWritable();
    nonRatedItems = new ArrayWritable(new String[] {});
  }

  public ActiveUser(Text userData) {
    String [] elements = userData.toString().split("\t");
    this.userId = new IntWritable(Integer.valueOf(elements[0]));
    this.nonRatedItems = new ArrayWritable(elements[1].split(","));
  }

  public IntWritable getUserId() {
    return userId;
  }

  public ArrayWritable getNonRatedItems() {
    return nonRatedItems;
  }

  public Integer[] getNonRatedItemsArray () {

    Integer[] result = new Integer[nonRatedItems.get().length];
    for (int i=0; i<nonRatedItems.get().length; i++) {
      result [i] = Integer.valueOf(nonRatedItems.get()[i].toString());
    }
    return result;
  }

  @Override
  public int hashCode() {
    int hash = 17;
    return 163 * hash + userId.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    return userId.equals(((ActiveUser) obj).userId);

  }

  @Override
  public String toString() {
    return userId.get() + "";
  }

  @Override
  public int compareTo(ActiveUser o) {
    return userId.compareTo(o.userId);
  }

  @Override
  public void write(DataOutput dataOutput) throws IOException {
    userId.write(dataOutput);
    nonRatedItems.write(dataOutput);
  }

  @Override
  public void readFields(DataInput dataInput) throws IOException {
    userId.readFields(dataInput);
    nonRatedItems.readFields(dataInput);

  }
}

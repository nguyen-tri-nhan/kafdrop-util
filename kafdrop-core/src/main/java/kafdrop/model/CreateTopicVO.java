package kafdrop.model;

public final class CreateTopicVO {
  String name;

  int partitionsNumber;

  int replicationFactor;

  public CreateTopicVO() {
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getPartitionsNumber() {
    return partitionsNumber;
  }

  public void setPartitionsNumber(int partitionsNumber) {
    this.partitionsNumber = partitionsNumber;
  }

  public int getReplicationFactor() {
    return replicationFactor;
  }

  public void setReplicationFactor(int replicationFactor) {
    this.replicationFactor = replicationFactor;
  }
}

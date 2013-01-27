package edu.syr.pcpratts.studio.code.sootexample.tochange;

public class ToChange {

  private String m_str;
  
  private String run() {
    return "hello world";
  }
  
  private void save(String str){
    m_str = str;
  }
  
  public static void main(String[] args) {
    ToChange to_change = new ToChange();
    String value = to_change.run();
    to_change.save(value);
  }
}

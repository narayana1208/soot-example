package org.trifort.sootexample;

import soot.G;
import soot.PackManager;
import soot.Transform;

public class SootExample {

  private void run(String output_format) {
    G.reset();
    
    Transform tform = new Transform("jtp.ExampleBodyTransformer", new ExampleBodyTransformer());
    PackManager.v().getPack("jtp").add(tform);
    
    String[] args = {
      "-pp",
      "-process-dir", "to-change/build/classes/",
      "-cp", "to-change/build/classes/",
      "-output-format", output_format,
      "-include-all",
      "-w"
    };
    
    soot.Main.main(args);
  }
  
  public static void main(String[] args) {
    SootExample soot_example = new SootExample();
    soot_example.run("J");
    soot_example.run("c");
  }
}

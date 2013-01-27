package org.trifort.sootexample;

import java.util.Map;
import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.PatchingChain;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.Expr;
import soot.jimple.InvokeExpr;
import soot.jimple.Jimple;
import soot.jimple.Stmt;

public class ExampleBodyTransformer extends BodyTransformer {

  private int m_localNumber;
  
  public ExampleBodyTransformer(){
    m_localNumber = 0;
  }
  
  private int getLocalNumber(){
    int ret = m_localNumber;
    m_localNumber++;
    return ret;
  }
  
  @Override
  protected void internalTransform(Body body, String string, Map map) {
    SootMethod soot_method = body.getMethod();
    System.out.println("transforming: "+soot_method.getSignature());
    
    PatchingChain<Unit> units = body.getUnits();
    Unit curr = units.getFirst();
    while(curr != null){
      if(curr instanceof AssignStmt){
        AssignStmt assign_stmt = (AssignStmt) curr;
        Value lhs = assign_stmt.getLeftOp();
        Value rhs = assign_stmt.getRightOp();
        
        if(rhs instanceof InvokeExpr){
          InvokeExpr invoke_expr = (InvokeExpr) rhs;
          SootMethod invoked_method = invoke_expr.getMethod();
          Type return_type = invoked_method.getReturnType();
          if(return_type instanceof RefType){
            RefType ref_type = (RefType) return_type;
            String class_name = ref_type.getClassName();
            if(class_name.equals("java.lang.String")){
              //System.out field ref
              Type printstream_type = RefType.v("java.io.PrintStream");
              Local system_out = Jimple.v().newLocal("system_out"+getLocalNumber(), printstream_type);
              
              SootClass system = Scene.v().getSootClass("java.lang.System");
              SootField out_field = system.getField("out", printstream_type);
              Value field_ref = Jimple.v().newStaticFieldRef(out_field.makeRef());
              
              Unit field_assign = Jimple.v().newAssignStmt(system_out, field_ref);
              body.getLocals().add(system_out);
              units.insertAfter(field_assign, curr);
              
              //call println
              SootClass printstream = Scene.v().getSootClass("java.io.PrintStream");
              SootMethod println = printstream.getMethod("void println(java.lang.String)");
              Expr println_expr = Jimple.v().newVirtualInvokeExpr(system_out, println.makeRef(), lhs);
              Stmt println_stmt = Jimple.v().newInvokeStmt(println_expr);
              units.insertAfter(println_stmt, field_assign);
            }
          }
        }
      }
      curr = units.getSuccOf(curr);
    }
  }
  
}

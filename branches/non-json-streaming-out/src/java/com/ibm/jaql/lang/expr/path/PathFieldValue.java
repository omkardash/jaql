/*
 * Copyright (C) IBM Corp. 2009.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.ibm.jaql.lang.expr.path;

import java.io.PrintStream;
import java.util.HashSet;

import com.ibm.jaql.json.schema.Schema;
import com.ibm.jaql.json.schema.SchemaFactory;
import com.ibm.jaql.json.schema.SchemaTransformation;
import com.ibm.jaql.json.type.JsonRecord;
import com.ibm.jaql.json.type.JsonString;
import com.ibm.jaql.json.type.JsonValue;
import com.ibm.jaql.lang.core.Context;
import com.ibm.jaql.lang.core.Var;
import com.ibm.jaql.lang.expr.core.ConstExpr;
import com.ibm.jaql.lang.expr.core.Expr;
import com.ibm.jaql.lang.expr.core.VarExpr;
import com.ibm.jaql.util.Bool3;


/** e.g., .a */
public class PathFieldValue extends PathStep
{

  /**
   * @param exprs
   */
  public PathFieldValue(Expr[] exprs)
  {
    super(exprs);
  }

  /**
   * @param name
   */
  public PathFieldValue(Expr name)
  {
    super(name, new PathReturn());
  }

  /**
   * @param expr0
   * @param expr1
   */
  public PathFieldValue(Expr name, Expr next)
  {
    super(name, next);
  }
  
  /**
   * The name of the field to return.
   * @return
   */
  public Expr nameExpr()
  {
    return exprs[0];
  }

  
  /**
   * 
   */
  public void decompile(PrintStream exprText, HashSet<Var> capturedVars)
  throws Exception
  {
    exprText.print(".(");
    exprs[0].decompile(exprText, capturedVars);
    exprText.print(")");
    exprs[1].decompile(exprText, capturedVars);
  }

  /* (non-Javadoc)
   * @see com.ibm.jaql.lang.expr.core.PathExpr#eval(com.ibm.jaql.lang.core.Context)
   */
  @Override
  public JsonValue eval(Context context) throws Exception
  {
    JsonRecord rec = (JsonRecord)input;
    if( rec == null )
    {
      return null;
    }
    JsonString name = (JsonString)exprs[0].eval(context);
    if( name == null )
    {
      return null;
    }
    JsonValue value = rec.get(name);
    return nextStep(context, value);
  }

  /**
   * make $recVar.name
   *  
   * @param inVar
   * @param fieldName
   * @return
   */
  public static Expr byName(Var recVar, String fieldName)
  {
    Expr f = new ConstExpr(new JsonString(fieldName));
    return new PathExpr(new VarExpr(recVar), new PathFieldValue(f));
  }

  /**
   * Assume fieldNameVar is $n, then make $inVar.n
   *  
   * @param inVar
   * @param fieldNameVar
   * @return
   */
  public static Expr byVarName(Var recVar, Var fieldNameVar)
  {
    return byName(recVar, fieldNameVar.nameAsField());
  }
  
  // -- schema ------------------------------------------------------------------------------------
  
  @Override
  public PathStepSchema getSchema(Schema inputSchema)
  {
    PathStepSchema s = staticResolveField(inputSchema, exprs[0], nextStep());
    switch (s.hasData)
    {
    case TRUE:
      return new PathStepSchema(s.schema, Bool3.TRUE);
    case FALSE:
      return new PathStepSchema(SchemaFactory.nullSchema(), Bool3.TRUE);
    case UNKNOWN:
    default:
      return new PathStepSchema(SchemaTransformation.addNullability(s.schema), Bool3.TRUE);
    }
  }
}
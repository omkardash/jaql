/*
 * Copyright (C) IBM Corp. 2008.
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
package com.ibm.jaql.lang.expr.core;

import java.io.PrintStream;
import java.util.HashSet;

import com.ibm.jaql.json.type.Item;
import com.ibm.jaql.json.type.JString;
import com.ibm.jaql.json.type.MemoryJRecord;
import com.ibm.jaql.lang.core.Context;
import com.ibm.jaql.lang.core.Var;
import com.ibm.jaql.lang.core.VarMap;
import com.ibm.jaql.util.Bool3;

/**
 * 
 */
public class NameValueBinding extends FieldExpr
{

  boolean required;

  /**
   * { exprs[0] : exprs[1] } required=true { exprs[0]?: exprs[1] }
   * required=false
   * 
   * @param required
   * @param exprs
   */
  public NameValueBinding(boolean required, Expr[] exprs)
  {
    super(exprs);
    this.required = required;
  }

  /**
   * @param nameExpr
   * @param valueExpr
   * @param required
   */
  public NameValueBinding(Expr nameExpr, Expr valueExpr, boolean required)
  {
    this(required, new Expr[]{nameExpr, valueExpr});
  }

  /**
   * @param name
   * @param value
   * @param required
   */
  public NameValueBinding(String name, Expr value, boolean required)
  {
    this(required, new Expr[]{new ConstExpr(new JString(name)), value});
  }

  /**
   * @param name
   * @param value
   */
  public NameValueBinding(Expr name, Expr value)
  {
    this(name, value, true);
  }

  /**
   * @param name
   * @param value
   */
  public NameValueBinding(String name, Expr value)
  {
    this(name, value, true);
  }

  /**
   * 
   * @param var var name is name, var value is value
   * @param required
   */
  public NameValueBinding(Var var, boolean required)
  {
    this(var.nameAsField(), new VarExpr(var), required);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.jaql.lang.expr.core.Expr#clone(com.ibm.jaql.lang.core.VarMap)
   */
  public NameValueBinding clone(VarMap varMap)
  {
    return new NameValueBinding(required, cloneChildren(varMap));
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.jaql.lang.expr.core.FieldExpr#staticNameMatches(com.ibm.jaql.json.type.JString)
   */
  public Bool3 staticNameMatches(JString name)
  {
    if (exprs[0] instanceof ConstExpr)
    {
      ConstExpr c = (ConstExpr) exprs[0];
      Item value = c.value;
      JString str = (JString) value.get();
      if (str != null && str.equals(name))
      {
        return Bool3.TRUE;
      }
      return Bool3.FALSE;
    }
    return Bool3.UNKNOWN;
  }

  /**
   * @return
   */
  public Expr nameExpr()
  {
    return exprs[0];
  }

  /**
   * @return
   */
  public Expr valueExpr()
  {
    return exprs[1];
  }

  /**
   * 
   */
  @Override
  public Bool3 evaluatesChildOnce(int i)
  {
    return Bool3.TRUE;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.jaql.lang.expr.core.FieldExpr#decompile(java.io.PrintStream,
   *      java.util.HashSet)
   */
  public void decompile(PrintStream exprText, HashSet<Var> capturedVars)
      throws Exception
  {
    exprText.print("(");
    exprs[0].decompile(exprText, capturedVars);
    exprText.print(")");
    if (!required)
    {
      exprText.print("?");
    }
    exprText.print(":(");
    exprs[1].decompile(exprText, capturedVars);
    exprText.print(")");
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.jaql.lang.expr.core.FieldExpr#eval(com.ibm.jaql.lang.core.Context,
   *      com.ibm.jaql.json.type.MemoryJRecord)
   */
  public void eval(Context context, MemoryJRecord rec) throws Exception
  {
    Item value = exprs[1].eval(context);
    if (required || !value.isNull())
    {
      // TODO: should we generate an error when the name is null or ignore the item?
      JString name = (JString) exprs[0].eval(context).get();
      if (name == null)
      {
        throw new NullPointerException("field name cannot be null");
      }
      rec.add(name, value);
    }
  }
}

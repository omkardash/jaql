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

import com.ibm.jaql.io.hadoop.JsonHolder;
import com.ibm.jaql.json.type.BufferedJsonArray;
import com.ibm.jaql.json.type.JsonArray;
import com.ibm.jaql.json.type.JsonNumber;
import com.ibm.jaql.json.type.JsonValue;
import com.ibm.jaql.json.type.SpilledJsonArray;
import com.ibm.jaql.json.util.JsonIterator;
import com.ibm.jaql.lang.core.Context;
import com.ibm.jaql.lang.core.Var;
import com.ibm.jaql.lang.expr.core.Expr;


public class UnrollIndex extends UnrollStep
{
  /**
   * @param exprs
   */
  public UnrollIndex(Expr[] exprs)
  {
    super(exprs);
  }

  /**
   * @param index
   */
  public UnrollIndex(Expr index)
  {
    super(index);
  }


  /**
   * 
   */
  public void decompile(PrintStream exprText, HashSet<Var> capturedVars)
  throws Exception
  {
    exprText.print("[");
    exprs[0].decompile(exprText, capturedVars);
    exprText.print("]");
  }

  /* (non-Javadoc)
   * @see com.ibm.jaql.lang.expr.core.PathExpr#eval(com.ibm.jaql.lang.core.Context)
   */
  @Override
  public JsonHolder expand(Context context, JsonHolder toExpand) throws Exception
  {
    JsonArray arr = (JsonArray)toExpand.value;
    if( arr == null )
    {
      return null;
    }
    JsonNumber index = (JsonNumber)exprs[0].eval(context);
    if( index == null )
    {
      return null;
    }
    long k = index.longValueExact();
    JsonIterator iter = arr.iter();
    JsonHolder hole = null;
    JsonArray out;
    if( arr instanceof BufferedJsonArray )
    {
      BufferedJsonArray fixed = new BufferedJsonArray((int)arr.count()); // TODO: memory
      out = fixed;
      long i = 0;
      for (JsonValue value : iter)
      {
        if( i == k )
        {
          hole = new JsonHolder(value); // TODO: memory
        }
        fixed.set((int)i, value); // TODO: shouldn't add() be used here? 
        i++;
      }
    }
    else
    {
      SpilledJsonArray spill = new SpilledJsonArray(); // TODO: memory
      out = spill;
      long i = 0;
      for (JsonValue value : iter)
      {
        if( i == k )
        {
          hole = new JsonHolder(value); // TODO: memory
        }
        spill.addCopy(value);
        i++;
      }
    }
    toExpand.value = out;
    return hole;
  }
}
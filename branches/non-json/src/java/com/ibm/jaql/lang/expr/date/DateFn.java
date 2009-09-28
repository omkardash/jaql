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
package com.ibm.jaql.lang.expr.date;

import java.util.Map;

import com.ibm.jaql.json.type.JsonDate;
import com.ibm.jaql.json.type.JsonString;
import com.ibm.jaql.json.type.MutableJsonDate;
import com.ibm.jaql.lang.core.Context;
import com.ibm.jaql.lang.expr.core.Expr;
import com.ibm.jaql.lang.expr.core.ExprProperty;
import com.ibm.jaql.lang.expr.function.DefaultBuiltInFunctionDescriptor;

public class DateFn extends Expr
{
  public static class Descriptor extends DefaultBuiltInFunctionDescriptor.Par12
  {
    public Descriptor()
    {
      super("date", DateFn.class);
    }
  }
  
  protected MutableJsonDate date = new MutableJsonDate();
  
  public DateFn(Expr[] exprs)
  {
    super(exprs);
  }

  @Override
  public JsonDate eval(Context context) throws Exception
  {
    JsonString dateStr = (JsonString)exprs[0].eval(context);
    JsonString formatStr = (JsonString)exprs[1].eval(context);
    if( formatStr == null)
    {
      date.set(dateStr.toString());
    }
    else
    {
      date.set(dateStr.toString(), JsonDate.getFormat(formatStr.toString()));
    }
    return date;
  }

  // needed for date(...) constructor
  @Override
  public Map<ExprProperty, Boolean> getProperties() 
  {
    Map<ExprProperty, Boolean> result = super.getProperties();
    result.put(ExprProperty.ALLOW_COMPILE_TIME_COMPUTATION, true);
    return result;
  } 
}

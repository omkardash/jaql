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
package com.ibm.jaql.lang.expr.numeric;

import com.ibm.jaql.json.type.JsonDecimal;
import com.ibm.jaql.json.type.JsonNumber;
import com.ibm.jaql.json.type.JsonNumeric;
import com.ibm.jaql.json.type.JsonString;
import com.ibm.jaql.json.type.JsonValue;
import com.ibm.jaql.lang.core.Context;
import com.ibm.jaql.lang.expr.core.Expr;
import com.ibm.jaql.lang.expr.core.JaqlFn;

/**
 * 
 */
@JaqlFn(fnName = "number", minArgs = 1, maxArgs = 1)
public class NumberFn extends Expr
{
  /**
   * number(numeric or string)
   * 
   * @param exprs
   */
  public NumberFn(Expr[] exprs)
  {
    super(exprs);
  }

  /**
   * @param num
   */
  public NumberFn(Expr num)
  {
    super(num);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.jaql.lang.expr.core.Expr#eval(com.ibm.jaql.lang.core.Context)
   */
  public JsonNumber eval(final Context context) throws Exception
  {
    JsonValue val = exprs[0].eval(context);
    if (val == null)
    {
      return null;
    }
    else if (val instanceof JsonNumber)
    {
      return (JsonNumber)val;
    }
    else if (val instanceof JsonNumeric)
    {
      JsonNumeric n = (JsonNumeric) val;
      return new JsonDecimal(n.decimalValue()); // TODO: memory
    }
    else if (val instanceof JsonString)
    {
      return new JsonDecimal(val.toString()); // TODO: memory
    }
    else
    {
      throw new ClassCastException("cannot convert "
          + val.getEncoding().getType().name() + " to number");
    }
  }
}
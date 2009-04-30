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
package com.ibm.jaql.lang.expr.regex;

import java.util.regex.Matcher;

import com.ibm.jaql.json.type.FixedJArray;
import com.ibm.jaql.json.type.Item;
import com.ibm.jaql.json.type.JRegex;
import com.ibm.jaql.json.type.JString;
import com.ibm.jaql.lang.core.Context;
import com.ibm.jaql.lang.expr.core.Expr;
import com.ibm.jaql.lang.expr.core.JaqlFn;
import com.ibm.jaql.util.Bool3;

// TODO: RegexExec: support regex () submatch  captures a la http://developer.mozilla.org/en/docs/Core_JavaScript_1.5_Guide:Working_with_Regular_Expressions:Using_Parenthesized_Substring_Matches

/**
 * 
 */
@JaqlFn(fnName = "regexExtract", minArgs = 2, maxArgs = 2)
public class RegexExtractFn extends Expr
{
  /**
   * @param args
   */
  public RegexExtractFn(Expr[] args)
  {
    super(args);
  }

  @Override
  public Bool3 isArray()
  {
    return Bool3.TRUE;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.jaql.lang.expr.core.IterExpr#iter(com.ibm.jaql.lang.core.Context)
   */
  public Item eval(final Context context) throws Exception
  {
    final JRegex regex = (JRegex) exprs[0].eval(context).get();
    if (regex == null)
    {
      return Item.NIL;
    }
    JString text = (JString) exprs[1].eval(context).get();
    if (text == null)
    {
      return Item.NIL;
    }
    final Matcher matcher = regex.takeMatcher();
    matcher.reset(text.toString());
    if (!matcher.find())
    {
      regex.returnMatcher(matcher);
      return Item.NIL;
    }
    int n = matcher.groupCount();    
    FixedJArray arr = new FixedJArray(n); // TODO: memory
    Item result = new Item(arr); // TODO: memory
    for(int i = 0 ; i < n ; i++)
    {
      String s = matcher.group(i+1);
      arr.set(i, new JString(s)); // TODO: memory
    }
    regex.returnMatcher(matcher);
    return result;
  }
}

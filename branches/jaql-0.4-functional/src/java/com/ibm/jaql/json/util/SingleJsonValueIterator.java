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
package com.ibm.jaql.json.util;

import com.ibm.jaql.json.type.JsonValue;

/** Wraps a single value into an {@link JsonIterator}. * 
 */
public class SingleJsonValueIterator extends JsonIterator
{
  boolean moved = false;
  
  /**
   * @param value
   */
  public SingleJsonValueIterator(JsonValue value)
  {
    this.currentValue = value;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.jaql.json.util.Iter#next()
   */
  public boolean moveNext()
  {
    if (moved) 
    {
      return false;
    }
    
    moved = true;
    return true;
  }

  public void reset(JsonValue value)
  {
    this.currentValue = value;
    moved = false;
  }
}

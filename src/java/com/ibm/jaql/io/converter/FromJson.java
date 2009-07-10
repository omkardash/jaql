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
package com.ibm.jaql.io.converter;

import com.ibm.jaql.json.type.JsonValue;

/** Interface for converters from JSON to T.
 * @param <T>
 */
public interface FromJson<T>
{

  /**
   * @return
   */
  T createInitialTarget();

  /**
   * @param src
   * @param tgt
   */
  T convert(JsonValue src, T tgt);
}
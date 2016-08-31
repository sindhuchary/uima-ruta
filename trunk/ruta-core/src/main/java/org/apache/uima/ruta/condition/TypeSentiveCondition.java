/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.uima.ruta.condition;

import org.apache.uima.ruta.expression.type.AbstractTypeListExpression;
import org.apache.uima.ruta.expression.type.ITypeExpression;

public abstract class TypeSentiveCondition extends TerminalRutaCondition {

  protected final ITypeExpression type;

  private final AbstractTypeListExpression list;

  public ITypeExpression getType() {
    return type;
  }

  public TypeSentiveCondition(ITypeExpression type) {
    super();
    this.type = type;
    this.list = null;
  }

  public TypeSentiveCondition(AbstractTypeListExpression list) {
    super();
    this.type = null;
    this.list = list;
  }

  public boolean isWorkingOnList() {
    return getList() != null;
  }

  public AbstractTypeListExpression getList() {
    return list;
  }
}

/*
 * Copyright 2022 S. Webber
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.projog.clp;

import static org.testng.Assert.assertSame;

class DummyConstraintStore implements ConstraintStore {
   private final Variable variable;
   private final VariableState state;

   DummyConstraintStore(Variable variable, long value) {
      this(variable, value, value);
   }

   DummyConstraintStore(Variable variable, long min, long max) {
      if (max < min) {
         throw new IllegalArgumentException();
      }
      this.variable = variable;
      this.state = new VariableState();
      state.setMin(min);
      state.setMax(max);
   }

   @Override
   public long getMin(Expression id) {
      assertSame(variable, id);
      return state.getMin();
   }

   @Override
   public long getMax(Expression id) {
      assertSame(variable, id);
      return state.getMax();
   }

   @Override
   public ExpressionResult setValue(Expression id, long value) {
      assertSame(variable, id);
      return toResult(state.setValue(value));
   }

   @Override
   public ExpressionResult setMin(Expression id, long min) {
      assertSame(variable, id);
      return toResult(state.setMin(min));
   }

   @Override
   public ExpressionResult setMax(Expression id, long max) {
      assertSame(variable, id);
      return toResult(state.setMax(max));
   }

   @Override
   public ExpressionResult setNot(Expression id, long not) {
      assertSame(variable, id);
      return toResult(state.setNot(not));
   }

   private ExpressionResult toResult(VariableStateResult s) {
      return s == VariableStateResult.FAILED ? ExpressionResult.INVALID : ExpressionResult.VALID;
   }
}

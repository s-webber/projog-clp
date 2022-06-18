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

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

final class DummyExpression implements Expression {
   private long min;
   private long max;

   DummyExpression(long min, long max) {
      this.min = min;
      this.max = max;
   }

   @Override
   public long getMin(ConstraintStore s) {
      Objects.requireNonNull(s);
      return min;
   }

   @Override
   public long getMax(ConstraintStore s) {
      Objects.requireNonNull(s);
      return max;
   }

   @Override
   public ExpressionResult setMin(ConstraintStore s, long min) {
      Objects.requireNonNull(s);
      if (min > max) {
         return ExpressionResult.FAILED;
      } else if (min > this.min) {
         this.min = min;
         return ExpressionResult.UPDATED;
      } else {
         return ExpressionResult.NO_CHANGE;
      }
   }

   @Override
   public ExpressionResult setMax(ConstraintStore s, long max) {
      Objects.requireNonNull(s);
      if (max < min) {
         return ExpressionResult.FAILED;
      } else if (max < this.max) {
         this.max = max;
         return ExpressionResult.UPDATED;
      } else {
         return ExpressionResult.NO_CHANGE;
      }
   }

   @Override
   public ExpressionResult setNot(ConstraintStore s, long not) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void walk(Consumer<Expression> r) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Expression replaceVariables(Function<Variable, Variable> r) {
      throw new UnsupportedOperationException();
   }
}

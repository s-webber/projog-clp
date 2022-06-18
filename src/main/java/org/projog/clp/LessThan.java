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

import java.util.function.Consumer;
import java.util.function.Function;

/** Enforces that the value of one {@code Expression} is less than another. */
public final class LessThan implements Constraint {
   private final Expression left;
   private final Expression right;

   /**
    * Enforces that the value of {@code left} is less than the value of {@code right}.
    *
    * @param left the expression whose value must be less than {@code right}
    * @param right the expression whose value must be greater than {@code left}
    */
   public LessThan(Expression left, Expression right) {
      this.left = left;
      this.right = right;
   }

   @Override
   public ConstraintResult enforce(ConstraintStore m) {
      long min = left.getMin(m);
      long max = right.getMax(m);
      if (min >= max) {
         return ConstraintResult.FAILED;
      }

      if (left.setMax(m, max - 1) == ExpressionResult.FAILED || right.setMin(m, min + 1) == ExpressionResult.FAILED) {
         return ConstraintResult.FAILED;
      }
      return left.getMax(m) < right.getMin(m) ? ConstraintResult.MATCHED : ConstraintResult.UNRESOLVED;
   }

   @Override
   public void walk(Consumer<Expression> r) {
      left.walk(r);
      right.walk(r);
   }

   @Override
   public LessThan replaceVariables(Function<Variable, Variable> r) {
      return new LessThan(left.replaceVariables(r), right.replaceVariables(r));
   }

   @Override
   public String toString() {
      return "LessThan [left=" + left + ", right=" + right + "]";
   }
}

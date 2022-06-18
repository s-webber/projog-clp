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

import static org.projog.clp.MathUtils.safeAdd;
import static org.projog.clp.MathUtils.safeSubtract;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/** The difference of two {@code Expression}s. */
public final class Subtract implements Expression {
   private final Expression left;
   private final Expression right;

   /**
    * @param left the expression to subtract {@code right} from
    * @param right the expression to subtract from {@code left}
    */
   public Subtract(Expression left, Expression right) {
      this.left = Objects.requireNonNull(left);
      this.right = Objects.requireNonNull(right);
   }

   @Override
   public long getMin(ReadConstraintStore s) {
      return safeSubtract(left.getMin(s), right.getMax(s));
   }

   @Override
   public long getMax(ReadConstraintStore s) {
      return safeSubtract(left.getMax(s), right.getMin(s));
   }

   @Override
   public ExpressionResult setMin(ConstraintStore s, long min) {
      long leftMax = left.getMax(s);
      long rightMin = right.getMin(s);
      if (safeSubtract(leftMax, rightMin) < min) {
         return ExpressionResult.FAILED;
      }

      ExpressionResult r1 = left.setMin(s, safeAdd(rightMin, min));
      if (r1 == ExpressionResult.FAILED) {
         return ExpressionResult.FAILED;
      }

      ExpressionResult r2 = right.setMax(s, safeSubtract(leftMax, min));
      if (r2 == ExpressionResult.FAILED) {
         return ExpressionResult.FAILED;
      }

      return r1 == ExpressionResult.UPDATED || r2 == ExpressionResult.UPDATED ? ExpressionResult.UPDATED : ExpressionResult.NO_CHANGE;
   }

   @Override
   public ExpressionResult setMax(ConstraintStore s, long max) {
      long leftMin = left.getMin(s);
      long rightMax = right.getMax(s);
      if (safeSubtract(leftMin, rightMax) > max) {
         return ExpressionResult.FAILED;
      }

      ExpressionResult r1 = left.setMax(s, safeAdd(rightMax, max));
      if (r1 == ExpressionResult.FAILED) {
         return ExpressionResult.FAILED;
      }

      ExpressionResult r2 = right.setMin(s, safeSubtract(leftMin, max));
      if (r2 == ExpressionResult.FAILED) {
         return ExpressionResult.FAILED;
      }

      return r1 == ExpressionResult.UPDATED || r2 == ExpressionResult.UPDATED ? ExpressionResult.UPDATED : ExpressionResult.NO_CHANGE;
   }

   @Override
   public ExpressionResult setNot(ConstraintStore s, long not) {
      if (getMax(s) == not) {
         return setMax(s, safeSubtract(not, 1));
      } else if (getMin(s) == not) {
         return setMin(s, safeAdd(not, 1));
      } else {
         return ExpressionResult.NO_CHANGE;
      }
   }

   @Override
   public void walk(Consumer<Expression> r) {
      r.accept(this);
      left.walk(r);
      right.walk(r);
   }

   @Override
   public Subtract replaceVariables(Function<Variable, Variable> r) {
      return new Subtract(left.replaceVariables(r), right.replaceVariables(r));
   }

   @Override
   public String toString() {
      return "Subtract [left=" + left + ", right=" + right + "]";
   }
}

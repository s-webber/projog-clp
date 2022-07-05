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
package org.projog.clp.compare;

import java.util.function.Consumer;
import java.util.function.Function;

import org.projog.clp.Constraint;
import org.projog.clp.ConstraintResult;
import org.projog.clp.ConstraintStore;
import org.projog.clp.Expression;
import org.projog.clp.ExpressionResult;
import org.projog.clp.LeafExpression;
import org.projog.clp.ReadConstraintStore;

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
      return enforce(left, right, m);
   }

   static ConstraintResult enforce(Expression left, Expression right, ConstraintStore m) {
      long min = left.getMin(m);
      long max = right.getMax(m);
      if (min >= max) {
         return ConstraintResult.FAILED;
      }

      if (left.setMax(m, max - 1) == ExpressionResult.INVALID || right.setMin(m, min + 1) == ExpressionResult.INVALID) {
         return ConstraintResult.FAILED;
      }
      return left.getMax(m) < right.getMin(m) ? ConstraintResult.MATCHED : ConstraintResult.UNRESOLVED;
   }

   @Override
   public ConstraintResult reify(ReadConstraintStore constraintStore) {
      if (left.getMax(constraintStore) < right.getMin(constraintStore)) {
         return ConstraintResult.MATCHED;
      } else if (left.getMin(constraintStore) >= right.getMax(constraintStore)) {
         return ConstraintResult.FAILED;
      } else {
         return ConstraintResult.UNRESOLVED;
      }
   }

   @Override
   public ConstraintResult prevent(ConstraintStore constraintStore) {
      return LessThanOrEqualTo.enforce(right, left, constraintStore);
   }

   @Override
   public void walk(Consumer<Expression> r) {
      left.walk(r);
      right.walk(r);
   }

   @Override
   public LessThan replace(Function<LeafExpression, LeafExpression> r) {
      return new LessThan(left.replace(r), right.replace(r));
   }

   @Override
   public String toString() {
      return "LessThan [left=" + left + ", right=" + right + "]";
   }
}

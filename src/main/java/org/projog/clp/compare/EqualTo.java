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

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import org.projog.clp.Constraint;
import org.projog.clp.ConstraintResult;
import org.projog.clp.ConstraintStore;
import org.projog.clp.Expression;
import org.projog.clp.ExpressionResult;
import org.projog.clp.LeafExpression;
import org.projog.clp.ReadConstraintStore;

/** Enforces that two {@code Expression}s have the same value. */
public final class EqualTo implements Constraint {
   private final Expression left;
   private final Expression right;

   public EqualTo(Expression left, Expression right) {
      this.left = Objects.requireNonNull(left);
      this.right = Objects.requireNonNull(right);
   }

   @Override
   public ConstraintResult enforce(ConstraintStore m) {
      return enforce(left, right, m);
   }

   static ConstraintResult enforce(Expression left, Expression right, ConstraintStore m) {
      final long minLeft = left.getMin(m);
      final long maxLeft = left.getMax(m);
      final long minRight = right.getMin(m);
      final long maxRight = right.getMax(m);

      if (minLeft > maxRight || maxLeft < minRight) {
         return ConstraintResult.FAILED;
      }
      if (left.setMin(m, minRight) == ExpressionResult.INVALID
          || left.setMax(m, maxRight) == ExpressionResult.INVALID
          || right.setMin(m, minLeft) == ExpressionResult.INVALID
          || right.setMax(m, maxLeft) == ExpressionResult.INVALID) {
         return ConstraintResult.FAILED;
      }

      return left.getMin(m) == right.getMax(m) && left.getMax(m) == right.getMin(m) ? ConstraintResult.MATCHED : ConstraintResult.UNRESOLVED;
   }

   @Override
   public ConstraintResult reify(ReadConstraintStore constraintStore) {
      final long minLeft = left.getMin(constraintStore);
      final long maxLeft = left.getMax(constraintStore);
      final long minRight = right.getMin(constraintStore);
      final long maxRight = right.getMax(constraintStore);

      if (minLeft == maxRight && maxLeft == minRight) {
         return ConstraintResult.MATCHED;
      } else if (minLeft > maxRight || maxLeft < minRight) {
         return ConstraintResult.FAILED;
      } else {
         return ConstraintResult.UNRESOLVED;
      }
   }

   @Override
   public ConstraintResult prevent(ConstraintStore constraintStore) {
      return NotEqualTo.enforce(left, right, constraintStore);
   }

   @Override
   public void walk(Consumer<Expression> r) {
      left.walk(r);
      right.walk(r);
   }

   @Override
   public EqualTo replace(Function<LeafExpression, LeafExpression> r) {
      return new EqualTo(left.replace(r), right.replace(r));
   }

   @Override
   public String toString() {
      return "EqualTo [left=" + left + ", right=" + right + "]";
   }
}

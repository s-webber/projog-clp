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
package org.projog.clp.math;

import static org.projog.clp.math.MathUtils.safeAdd;
import static org.projog.clp.math.MathUtils.safeSubtract;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import org.projog.clp.ConstraintStore;
import org.projog.clp.Expression;
import org.projog.clp.ExpressionResult;
import org.projog.clp.LeafExpression;
import org.projog.clp.ReadConstraintStore;

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
         return ExpressionResult.INVALID;
      }

      if (left.setMin(s, safeAdd(rightMin, min)) == ExpressionResult.INVALID) {
         return ExpressionResult.INVALID;
      }

      if (right.setMax(s, safeSubtract(leftMax, min)) == ExpressionResult.INVALID) {
         return ExpressionResult.INVALID;
      }

      return ExpressionResult.VALID;
   }

   @Override
   public ExpressionResult setMax(ConstraintStore s, long max) {
      long leftMin = left.getMin(s);
      long rightMax = right.getMax(s);
      if (safeSubtract(leftMin, rightMax) > max) {
         return ExpressionResult.INVALID;
      }

      if (left.setMax(s, safeAdd(rightMax, max)) == ExpressionResult.INVALID) {
         return ExpressionResult.INVALID;
      }

      if (right.setMin(s, safeSubtract(leftMin, max)) == ExpressionResult.INVALID) {
         return ExpressionResult.INVALID;
      }

      return ExpressionResult.VALID;
   }

   @Override
   public void walk(Consumer<Expression> r) {
      r.accept(this);
      left.walk(r);
      right.walk(r);
   }

   @Override
   public Subtract replace(Function<LeafExpression, LeafExpression> r) {
      return new Subtract(left.replace(r), right.replace(r));
   }

   @Override
   public String toString() {
      return "Subtract [left=" + left + ", right=" + right + "]";
   }
}

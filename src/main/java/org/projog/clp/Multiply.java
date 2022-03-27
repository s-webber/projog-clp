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
import static org.projog.clp.MathUtils.safeMultiply;
import static org.projog.clp.MathUtils.safeSubtract;

import java.util.function.Consumer;
import java.util.function.Function;

/** The product of two {@code Expression}s. */
public final class Multiply implements Expression {
   private final Expression left;
   private final Expression right;

   public Multiply(Expression left, Expression right) {
      this.left = left;
      this.right = right;
   }

   @Override
   public long getMin(ConstraintStore s) {
      long leftMin = left.getMin(s);
      long leftMax = left.getMax(s);
      long rightMin = right.getMin(s);
      long rightMax = right.getMax(s);
      long r = safeMultiply(leftMin, rightMin);
      long r1 = safeMultiply(leftMin, rightMax);
      if (r1 < r) {
         r = r1;
      }
      r1 = safeMultiply(leftMax, rightMin);
      if (r1 < r) {
         r = r1;
      }
      r1 = safeMultiply(leftMax, rightMax);
      if (r1 < r) {
         r = r1;
      }
      return r;
   }

   @Override
   public long getMax(ConstraintStore s) {
      long leftMin = left.getMin(s);
      long leftMax = left.getMax(s);
      long rightMin = right.getMin(s);
      long rightMax = right.getMax(s);
      long r = safeMultiply(leftMin, rightMin);
      long r1 = safeMultiply(leftMin, rightMax);
      if (r1 > r) {
         r = r1;
      }
      r1 = safeMultiply(leftMax, rightMin);
      if (r1 > r) {
         r = r1;
      }
      r1 = safeMultiply(leftMax, rightMax);
      if (r1 > r) {
         r = r1;
      }
      return r;
   }

   @Override
   public ExpressionResult setMin(ConstraintStore s, long min) {
      long leftMin = left.getMin(s);
      long leftMax = left.getMax(s);
      long rightMin = right.getMin(s);
      long rightMax = right.getMax(s);

      if (min == 0 || leftMin < 1 || rightMin < 1) {
         // TODO currently ignore anything where either arg is negative or min is 0
         return ExpressionResult.NO_CHANGE;
      }

      long newMinLeft = Math.min(divideWhole(min, rightMin), divideWhole(min, rightMax));
      ExpressionResult r1 = newMinLeft > leftMin ? left.setMin(s, newMinLeft) : ExpressionResult.NO_CHANGE;
      if (r1 == ExpressionResult.FAILED) {
         return ExpressionResult.FAILED;
      }

      long newMinRight = Math.min(divideWhole(min, leftMin), divideWhole(min, leftMax));
      ExpressionResult r2 = newMinRight > rightMin ? right.setMin(s, newMinRight) : ExpressionResult.NO_CHANGE;
      if (r2 == ExpressionResult.FAILED) {
         return ExpressionResult.FAILED;
      }

      return r1 == ExpressionResult.UPDATED || r2 == ExpressionResult.UPDATED ? ExpressionResult.UPDATED : ExpressionResult.NO_CHANGE;
   }

   private static long divideWhole(long dividend, long divisor) {
      long result = dividend / divisor;
      if (divisor * result == dividend) {
         return result;
      } else {
         return result + 1;
      }
   }

   @Override
   public ExpressionResult setMax(ConstraintStore s, long max) {
      long leftMin = left.getMin(s);
      long leftMax = left.getMax(s);
      long rightMin = right.getMin(s);
      long rightMax = right.getMax(s);

      if (max == 0 || leftMin < 1 || rightMin < 1) {
         // TODO currently ignore anything where either arg is negative or min is 0
         return ExpressionResult.NO_CHANGE;
      }

      long newMaxLeft = Math.max(max / rightMin, max / rightMax);
      ExpressionResult r1 = newMaxLeft < leftMax ? left.setMax(s, newMaxLeft) : ExpressionResult.NO_CHANGE;
      if (r1 == ExpressionResult.FAILED) {
         return ExpressionResult.FAILED;
      }

      long newMaxRight = Math.max(max / leftMin, max / leftMax);
      ExpressionResult r2 = newMaxRight < rightMax ? right.setMax(s, newMaxRight) : ExpressionResult.NO_CHANGE;
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
   public Multiply replace(Function<Expression, Expression> r) {
      return new Multiply(left.replace(r), right.replace(r));
   }

   @Override
   public String toString() {
      return "Multiply [left=" + left + ", right=" + right + "]";
   }
}

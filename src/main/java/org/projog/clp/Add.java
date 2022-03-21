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

public final class Add implements Expression {
   private final Expression left;
   private final Expression right;

   public Add(Expression left, Expression right) {
      this.left = Objects.requireNonNull(left);
      this.right = Objects.requireNonNull(right);
   }

   @Override
   public long getMin(Variables m) {
      return safeAdd(left.getMin(m), right.getMin(m));
   }

   @Override
   public long getMax(Variables m) {
      return safeAdd(left.getMax(m), right.getMax(m));
   }

   @Override
   public ExpressionResult setMin(Variables m, long min) {
      long leftMax = left.getMax(m);
      long rightMax = right.getMax(m);
      if (safeAdd(leftMax, rightMax) < min) {
         return ExpressionResult.FAILED;
      }

      ExpressionResult r1 = left.setMin(m, safeSubtract(min, rightMax));
      if (r1 == ExpressionResult.FAILED) {
         return ExpressionResult.FAILED;
      }
      ExpressionResult r2 = right.setMin(m, safeSubtract(min, leftMax));
      if (r2 == ExpressionResult.FAILED) {
         return ExpressionResult.FAILED;
      }
      return r1 == ExpressionResult.UPDATED || r2 == ExpressionResult.UPDATED ? ExpressionResult.UPDATED : ExpressionResult.NO_CHANGE;
   }

   @Override
   public ExpressionResult setMax(Variables m, long max) {
      long leftMin = left.getMin(m);
      long rightMin = right.getMin(m);
      if (safeAdd(leftMin, rightMin) > max) {
         return ExpressionResult.FAILED;
      }

      ExpressionResult r1 = left.setMax(m, safeSubtract(max, rightMin));
      if (r1 == ExpressionResult.FAILED) {
         return ExpressionResult.FAILED;
      }
      ExpressionResult r2 = right.setMax(m, safeSubtract(max, leftMin));
      if (r2 == ExpressionResult.FAILED) {
         // TODO never gets here?
         return ExpressionResult.FAILED;
      }
      return r1 == ExpressionResult.UPDATED || r2 == ExpressionResult.UPDATED ? ExpressionResult.UPDATED : ExpressionResult.NO_CHANGE;
   }

   @Override
   public ExpressionResult setNot(Variables m, long not) {
      if (getMax(m) == not && getMin(m) == not) {
         return ExpressionResult.FAILED;
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
   public Add replace(Function<Expression, Expression> r) {
      return new Add(left.replace(r), right.replace(r));
   }

   @Override
   public String toString() {
      return "Add [left=" + left + ", right=" + right + "]";
   }
}

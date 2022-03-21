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

public final class EqualTo implements Constraint {
   private final Expression left;
   private final Expression right;

   public EqualTo(Expression left, Expression right) {
      this.left = Objects.requireNonNull(left);
      this.right = Objects.requireNonNull(right);
   }

   @Override
   public ConstraintResult fire(Variables m) {
      final long minLeft = left.getMin(m);
      final long maxLeft = left.getMax(m);
      final long minRight = right.getMin(m);
      final long maxRight = right.getMax(m);

      if (minLeft > maxRight || maxLeft < minRight) {
         return ConstraintResult.FAILED;
      }
      if (left.setMin(m, minRight) == ExpressionResult.FAILED
          || left.setMax(m, maxRight) == ExpressionResult.FAILED
          || right.setMin(m, minLeft) == ExpressionResult.FAILED
          || right.setMax(m, maxLeft) == ExpressionResult.FAILED) {
         return ConstraintResult.FAILED;
      }

      return left.getMin(m) == right.getMax(m) && left.getMax(m) == right.getMin(m) ? ConstraintResult.MATCHED : ConstraintResult.UNRESOLVED;
   }

   @Override
   public void walk(Consumer<Expression> r) {
      left.walk(r);
      right.walk(r);
   }

   @Override
   public EqualTo replace(Function<Expression, Expression> r) {
      return new EqualTo(left.replace(r), right.replace(r));
   }

   @Override
   public String toString() {
      return "EqualTo [left=" + left + ", right=" + right + "]";
   }
}

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

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import org.projog.clp.ConstraintStore;
import org.projog.clp.Expression;
import org.projog.clp.ExpressionResult;
import org.projog.clp.LeafExpression;
import org.projog.clp.ReadConstraintStore;

public class Maximum implements Expression {
   private final Expression left;
   private final Expression right;

   public Maximum(Expression left, Expression right) {
      this.left = Objects.requireNonNull(left);
      this.right = Objects.requireNonNull(right);
   }

   @Override
   public long getMin(ReadConstraintStore constraintStore) {
      return Math.max(left.getMin(constraintStore), right.getMin(constraintStore));
   }

   @Override
   public long getMax(ReadConstraintStore constraintStore) {
      return Math.max(left.getMax(constraintStore), right.getMax(constraintStore));
   }

   @Override
   public ExpressionResult setMin(ConstraintStore constraintStore, long min) {
      if (left.getMax(constraintStore) < min) {
         return right.setMin(constraintStore, min);
      }

      if (right.getMax(constraintStore) < min) {
         return left.setMin(constraintStore, min);
      }

      return ExpressionResult.VALID;
   }

   @Override
   public ExpressionResult setMax(ConstraintStore constraintStore, long max) {
      if (left.setMax(constraintStore, max) == ExpressionResult.INVALID) {
         return ExpressionResult.INVALID;
      }

      if (right.setMax(constraintStore, max) == ExpressionResult.INVALID) {
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
   public Maximum replace(Function<LeafExpression, LeafExpression> r) {
      return new Maximum(left.replace(r), right.replace(r));
   }

   @Override
   public String toString() {
      return "Maximum [left=" + left + ", right=" + right + "]";
   }
}

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

import static org.projog.clp.MathUtils.safeSubtract;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class Absolute implements Expression {
   private final Expression e;

   public Absolute(Expression e) {
      this.e = Objects.requireNonNull(e);
   }

   @Override
   public long getMin(ReadConstraintStore s) {
      long min = e.getMin(s);
      long max = e.getMax(s);
      if (min < 1 && max > -1) {
         return 0;
      } else {
         return Math.min(safeAbs(min), safeAbs(max));
      }
   }

   @Override
   public long getMax(ReadConstraintStore s) {
      long min = e.getMin(s);
      long max = e.getMax(s);
      return Math.max(safeAbs(min), safeAbs(max));
   }

   private long safeAbs(long v) { // TODO move to MathUtils
      return v == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(v);
   }

   @Override
   public ExpressionResult setMin(ConstraintStore s, long value) {
      if (value < 1) {
         return ExpressionResult.VALID;
      }

      long min = e.getMin(s);
      long max = e.getMax(s);
      long negative = safeSubtract(0, value);

      if (max < value && e.setMax(s, negative) == ExpressionResult.INVALID) {
         return ExpressionResult.INVALID;
      }

      if (min > negative && e.setMin(s, value) == ExpressionResult.INVALID) {
         return ExpressionResult.INVALID;
      }

      return ExpressionResult.VALID;
   }

   @Override
   public ExpressionResult setMax(ConstraintStore s, long value) {
      if (value < 0) {
         return ExpressionResult.INVALID;
      }

      if (e.setMax(s, value) == ExpressionResult.INVALID) {
         return ExpressionResult.INVALID;
      }

      if (e.setMin(s, safeSubtract(0, value)) == ExpressionResult.INVALID) {
         return ExpressionResult.INVALID;
      }

      return ExpressionResult.VALID;
   }

   @Override
   public void walk(Consumer<Expression> consumer) {
      consumer.accept(this);
      e.walk(consumer);
   }

   @Override
   public Absolute replaceVariables(Function<Variable, Variable> function) {
      return new Absolute(e.replaceVariables(function));
   }

   @Override
   public String toString() {
      return "Absolute [e=" + e + "]";
   }
}

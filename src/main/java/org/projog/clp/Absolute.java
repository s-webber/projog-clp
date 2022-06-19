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

   private long safeAbs(long v) {
      return v == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(v);
   }

   @Override
   public ExpressionResult setNot(ConstraintStore s, long not) {
      return ExpressionResult.NO_CHANGE; // TODO
   }

   @Override
   public ExpressionResult setMin(ConstraintStore s, long value) {
      if (value < 1) {
         return ExpressionResult.NO_CHANGE;
      }

      long min = e.getMin(s);
      long max = e.getMax(s);
      long negative = safeSubtract(0, value);

      ExpressionResult r1 = max < value ? e.setMax(s, negative) : ExpressionResult.NO_CHANGE;
      if (r1 == ExpressionResult.FAILED) {
         return ExpressionResult.FAILED;
      }

      ExpressionResult r2 = min > negative ? e.setMin(s, value) : ExpressionResult.NO_CHANGE;
      if (r2 == ExpressionResult.FAILED) {
         return ExpressionResult.FAILED;
      }

      return r1 == ExpressionResult.UPDATED || r2 == ExpressionResult.UPDATED ? ExpressionResult.UPDATED : ExpressionResult.NO_CHANGE;
   }

   @Override
   public ExpressionResult setMax(ConstraintStore s, long value) {
      if (value < 0) {
         return ExpressionResult.FAILED;
      }

      ExpressionResult r1 = e.setMax(s, value);
      if (r1 == ExpressionResult.FAILED) {
         return ExpressionResult.FAILED;
      }
      ExpressionResult r2 = e.setMin(s, safeSubtract(0, value));
      if (r2 == ExpressionResult.FAILED) {
         return ExpressionResult.FAILED;
      }
      return r1 == ExpressionResult.UPDATED || r2 == ExpressionResult.UPDATED ? ExpressionResult.UPDATED : ExpressionResult.NO_CHANGE;
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

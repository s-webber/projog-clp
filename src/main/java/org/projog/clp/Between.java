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

import java.util.function.Consumer;
import java.util.function.Function;

public final class Between implements Constraint {
   private final Expression e;
   private final long min;
   private final long max;

   public Between(Expression e, long min, long max) {
      if (max < min) {
         throw new IllegalArgumentException(max + " < " + min);
      }
      this.e = e;
      this.min = min;
      this.max = max;
   }

   @Override
   public ConstraintResult fire(Variables m) {
      if (e.setMin(m, min) == ExpressionResult.FAILED || e.setMax(m, max) == ExpressionResult.FAILED) {
         return ConstraintResult.FAILED;
      }

      return e.getMin(m) >= min && e.getMax(m) <= max ? ConstraintResult.MATCHED : ConstraintResult.UNRESOLVED;
   }

   @Override
   public void walk(Consumer<Expression> r) {
      e.walk(r);
   }

   @Override
   public Between replace(Function<Expression, Expression> r) {
      return new Between(e.replace(r), min, max);
   }

   @Override
   public String toString() {
      return "Between [e=" + e + ", min=" + min + ", max=" + max + "]";
   }
}

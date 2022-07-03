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
import org.projog.clp.ReadConstraintStore;
import org.projog.clp.Variable;

/** Enforces that the possible values of a given {@code Expression} are within the given range. */
public final class Between implements Constraint {
   private final Expression e;
   private final long min;
   private final long max;

   /**
    * Enforces that the possible values of the given {@code Expression} are within the given range (inclusive).
    *
    * @param e
    * @param min the minimum possible value (inclusive) for the expression
    * @param max the maximum possible value (inclusive) for the expression
    */
   public Between(Expression e, long min, long max) {
      if (max < min) {
         throw new IllegalArgumentException(max + " < " + min);
      }
      this.e = e;
      this.min = min;
      this.max = max;
   }

   @Override
   public ConstraintResult enforce(ConstraintStore m) {
      if (e.setMin(m, min) == ExpressionResult.INVALID || e.setMax(m, max) == ExpressionResult.INVALID) {
         return ConstraintResult.FAILED;
      }

      return e.getMin(m) >= min && e.getMax(m) <= max ? ConstraintResult.MATCHED : ConstraintResult.UNRESOLVED;
   }

   @Override
   public ConstraintResult reify(ReadConstraintStore constraintStore) {
      final long minValue = e.getMin(constraintStore);
      final long maxValue = e.getMax(constraintStore);

      if (minValue >= min && maxValue <= max) {
         return ConstraintResult.MATCHED;
      } else if (minValue > max || maxValue < min) {
         return ConstraintResult.FAILED;
      } else {
         return ConstraintResult.UNRESOLVED;
      }
   }

   @Override
   public ConstraintResult prevent(ConstraintStore constraintStore) {
      switch (reify(constraintStore)) {
         case MATCHED:
            return ConstraintResult.FAILED;
         case FAILED:
            return ConstraintResult.MATCHED;
         default:
            return ConstraintResult.UNRESOLVED;
      }
   }

   @Override
   public void walk(Consumer<Expression> r) {
      e.walk(r);
   }

   @Override
   public Between replaceVariables(Function<Variable, Variable> r) {
      return new Between(e.replaceVariables(r), min, max);
   }

   @Override
   public String toString() {
      return "Between [e=" + e + ", min=" + min + ", max=" + max + "]";
   }
}

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

/** A rule that restricts the numeric values that can be used to solve a problem. */
public interface Constraint extends Expression {
   /** Attempts to enforce this constraint using the given {@code ConstraintStore}. */
   ConstraintResult enforce(ConstraintStore constraintStore);

   /** Attempts to prevent this constraint using the given {@code ConstraintStore}. */
   ConstraintResult prevent(ConstraintStore constraintStore);

   ConstraintResult reify(ReadConstraintStore constraintStore);

   /**
    * Traverse this constraint.
    *
    * @param consumer will be called for each {@code Expression} contained within this {@code Constraint}.
    */
   @Override
   void walk(Consumer<Expression> consumer);

   /**
    * Returns new {@code Constraint} with {@code LeafExpression}s in this {@code Constraint} replaced with values
    * returned from the given function.
    *
    * @param function returns the {@code LeafExpression} to use as a replacement for the {@code LeafExpression} it is
    * called with, or {@code null} if the original {@code LeafExpression} should continue to be used.
    * @return a new {@code Constraint} with {@code LeafExpression}s in this {@code Constraint} replaced with versions
    * returned from {@code function}.
    */
   @Override
   Constraint replace(Function<LeafExpression, LeafExpression> function);

   @Override
   default long getMin(ReadConstraintStore constraintStore) {
      ConstraintResult r = reify(constraintStore);
      return r == ConstraintResult.MATCHED ? 1 : 0;
   }

   @Override
   default long getMax(ReadConstraintStore constraintStore) {
      ConstraintResult r = reify(constraintStore);
      return r == ConstraintResult.FAILED ? 0 : 1;
   }

   @Override
   default ExpressionResult setMin(ConstraintStore constraintStore, long min) {
      if (min == 1) {
         ConstraintResult r = enforce(constraintStore);
         return r == ConstraintResult.FAILED ? ExpressionResult.INVALID : ExpressionResult.VALID;
      } else if (min > 1) {
         return ExpressionResult.INVALID;
      } else {
         return ExpressionResult.VALID;
      }
   }

   @Override
   default ExpressionResult setMax(ConstraintStore constraintStore, long max) {
      if (max == 0) {
         ConstraintResult r = prevent(constraintStore);
         return r == ConstraintResult.FAILED ? ExpressionResult.INVALID : ExpressionResult.VALID;
      } else if (max < 0) {
         return ExpressionResult.INVALID;
      } else {
         return ExpressionResult.VALID;
      }
   }
}

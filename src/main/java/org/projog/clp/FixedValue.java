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

/** Represents a single immutable number. */
public final class FixedValue implements LeafExpression {
   private static final int TRUE = 1;
   private static final int FALSE = 0;

   private final long value;

   public FixedValue(long value) {
      this.value = value;
   }

   @Override
   public long getMin(ReadConstraintStore s) {
      return value;
   }

   @Override
   public long getMax(ReadConstraintStore s) {
      return value;
   }

   @Override
   public ExpressionResult setNot(ConstraintStore s, long not) {
      return value == not ? ExpressionResult.INVALID : ExpressionResult.VALID;
   }

   @Override
   public ExpressionResult setMin(ConstraintStore s, long min) {
      return min > value ? ExpressionResult.INVALID : ExpressionResult.VALID;
   }

   @Override
   public ExpressionResult setMax(ConstraintStore s, long max) {
      return max < value ? ExpressionResult.INVALID : ExpressionResult.VALID;
   }

   @Override
   public ConstraintResult enforce(ConstraintStore constraintStore) {
      return reify(constraintStore);
   }

   @Override
   public ConstraintResult prevent(ConstraintStore constraintStore) {
      if (value == TRUE) {
         return ConstraintResult.FAILED;
      } else if (value == FALSE) {
         return ConstraintResult.MATCHED;
      } else {
         throw new IllegalStateException("Expected 0 or 1 but got " + value);
      }
   }

   @Override
   public ConstraintResult reify(ReadConstraintStore constraintStore) {
      if (value == TRUE) {
         return ConstraintResult.MATCHED;
      } else if (value == FALSE) {
         return ConstraintResult.FAILED;
      } else {
         throw new IllegalStateException("Expected 0 or 1 but got " + value);
      }
   }

   @Override
   public void walk(Consumer<Expression> r) {
      r.accept(this);
   }

   @Override
   public LeafExpression replace(Function<LeafExpression, LeafExpression> function) {
      LeafExpression r = function.apply(this);
      if (r != null) {
         return r;
      }
      return this;
   }

   @Override
   public String toString() {
      return "FixedValue [value=" + value + "]";
   }
}

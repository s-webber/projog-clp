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

/** An expression that can have one or more possible numeric values. */
public final class Variable implements Expression, Constraint {
   private static final int TRUE = 1;
   private static final int FALSE = 0;

   private final int id;

   /** @see ClpConstraintStore.Builder#createVariable() */
   Variable(int id) {
      this.id = id;
   }

   int getId() {
      return id;
   }

   @Override
   public long getMin(ReadConstraintStore s) {
      return s.getMin(this);
   }

   @Override
   public long getMax(ReadConstraintStore s) {
      return s.getMax(this);
   }

   public ExpressionResult setValue(ConstraintStore s, long value) {
      return s.setValue(this, value);
   }

   @Override
   public ExpressionResult setNot(ConstraintStore s, long not) {
      return s.setNot(this, not);
   }

   @Override
   public ExpressionResult setMin(ConstraintStore s, long min) {
      return s.setMin(this, min);
   }

   @Override
   public ExpressionResult setMax(ConstraintStore s, long max) {
      return s.setMax(this, max);
   }

   @Override
   public ConstraintResult enforce(ConstraintStore s) {
      long min = getMin(s);
      long max = getMax(s);
      if (min > TRUE || max < FALSE) {
         throw new IllegalStateException("Expected 0 or 1");
      } else if (s.setValue(this, TRUE) == ExpressionResult.INVALID) {
         return ConstraintResult.FAILED;
      } else {
         return ConstraintResult.MATCHED;
      }
   }

   @Override
   public ConstraintResult prevent(ConstraintStore s) {
      long min = getMin(s);
      long max = getMax(s);
      if (min > TRUE || max < FALSE) {
         throw new IllegalStateException("Expected 0 or 1");
      } else if (s.setValue(this, FALSE) == ExpressionResult.INVALID) {
         return ConstraintResult.FAILED;
      } else {
         return ConstraintResult.MATCHED;
      }
   }

   @Override
   public ConstraintResult reify(ReadConstraintStore s) {
      long min = getMin(s);
      long max = getMax(s);

      if (min != max) {
         return ConstraintResult.UNRESOLVED;
      } else if (min == TRUE) {
         return ConstraintResult.MATCHED;
      } else if (min == FALSE) {
         return ConstraintResult.FAILED;
      } else {
         throw new IllegalStateException("Expected 0 or 1 but got " + min);
      }
   }

   @Override
   public void walk(Consumer<Expression> r) {
      r.accept(this);
   }

   @Override
   public Variable replaceVariables(Function<Variable, Variable> function) {
      Variable r = function.apply(this);
      if (r != null) {
         return r;
      }
      return this;
   }

   @Override
   public String toString() {
      return "Variable [id=" + id + "]";
   }
}

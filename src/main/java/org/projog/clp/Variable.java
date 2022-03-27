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
public final class Variable implements Expression {
   private final int id;

   /** @see ClpConstraintStore.Builder#createVariable() */
   Variable(int id) {
      this.id = id;
   }

   int getId() {
      return id;
   }

   @Override
   public long getMin(ConstraintStore s) {
      return s.getMin(this);
   }

   @Override
   public long getMax(ConstraintStore s) {
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
   public void walk(Consumer<Expression> r) {
      r.accept(this);
   }

   @Override
   public Expression replace(Function<Expression, Expression> function) {
      Expression r = function.apply(this);
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

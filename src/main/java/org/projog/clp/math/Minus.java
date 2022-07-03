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

import static org.projog.clp.math.MathUtils.safeMinus;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import org.projog.clp.ConstraintStore;
import org.projog.clp.Expression;
import org.projog.clp.ExpressionResult;
import org.projog.clp.ReadConstraintStore;
import org.projog.clp.Variable;

public final class Minus implements Expression {
   private final Expression e;

   public Minus(Expression e) {
      this.e = Objects.requireNonNull(e);
   }

   @Override
   public long getMin(ReadConstraintStore s) {
      return safeMinus(e.getMax(s));
   }

   @Override
   public long getMax(ReadConstraintStore s) {
      return safeMinus(e.getMin(s));
   }

   @Override
   public ExpressionResult setMin(ConstraintStore s, long value) {
      return e.setMax(s, safeMinus(value));
   }

   @Override
   public ExpressionResult setMax(ConstraintStore s, long value) {
      return e.setMin(s, safeMinus(value));
   }

   @Override
   public void walk(Consumer<Expression> consumer) {
      consumer.accept(this);
      e.walk(consumer);
   }

   @Override
   public Minus replaceVariables(Function<Variable, Variable> function) {
      return new Minus(e.replaceVariables(function));
   }

   @Override
   public String toString() {
      return "Minus [e=" + e + "]";
   }
}

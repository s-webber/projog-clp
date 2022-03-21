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

public final class FixedValue implements Expression {
   private final long value;

   public FixedValue(long value) {
      this.value = value;
   }

   @Override
   public long getMin(Variables m) {
      return value;
   }

   @Override
   public long getMax(Variables m) {
      return value;
   }

   @Override
   public ExpressionResult setNot(Variables m, long not) {
      if (value == not) {
         return ExpressionResult.FAILED;
      }
      return ExpressionResult.NO_CHANGE;
   }

   @Override
   public ExpressionResult setMin(Variables m, long min) {
      if (min > value) {
         return ExpressionResult.FAILED;
      }
      return ExpressionResult.NO_CHANGE;
   }

   @Override
   public ExpressionResult setMax(Variables m, long max) {
      if (max < value) {
         return ExpressionResult.FAILED;
      }
      return ExpressionResult.NO_CHANGE;
   }

   @Override
   public void walk(Consumer<Expression> r) {
      r.accept(this);
   }

   @Override
   public Expression replace(Function<Expression, Expression> r) {
      Expression r2 = r.apply(this);
      if (r2 != null) {
         return r2;
      }
      return this;
   }

   @Override
   public String toString() {
      return "FixedValue [value=" + value + "]";
   }
}
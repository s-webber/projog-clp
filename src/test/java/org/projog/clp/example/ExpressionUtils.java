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
package org.projog.clp.example;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import org.projog.clp.Absolute;
import org.projog.clp.Add;
import org.projog.clp.Expression;
import org.projog.clp.FixedValue;
import org.projog.clp.Multiply;
import org.projog.clp.Subtract;

// added to support construction of examples TODO if useful then move from test to main
final class ExpressionUtils {
   static Expression diff(Expression left, Expression right) {
      return new Absolute(new Subtract(left, right));
   }

   static Expression multiply(long fixed, Expression... args) {
      return new Multiply(new FixedValue(fixed), multiply(args));
   }

   static Expression add(long fixed, Expression... args) {
      return new Add(new FixedValue(fixed), add(args));
   }

   static Expression multiply(Expression... args) {
      return apply(Multiply::new, args);
   }

   static Expression add(List<? extends Expression> args) {
      return apply(Add::new, args.toArray(new Expression[args.size()]));
   }

   static Expression add(Expression... args) {
      return apply(Add::new, args);
   }

   private static Expression apply(BiFunction<Expression, Expression, Expression> constructor, Expression... args) {
      Map<Expression, Integer> frequencies = new LinkedHashMap<>();
      for (Expression expression : args) {
         Integer i = frequencies.get(expression);
         frequencies.put(expression, i == null ? 1 : i + 1);
      }

      Expression result = null;
      for (Map.Entry<Expression, Integer> entry : frequencies.entrySet()) {
         Expression expression = frequencies.get(entry.getKey()) == 1 ? entry.getKey() : new Multiply(entry.getKey(), new FixedValue(entry.getValue()));
         if (result == null) {
            result = expression;
         } else {
            result = constructor.apply(result, expression);
         }
      }
      return result;
   }
}

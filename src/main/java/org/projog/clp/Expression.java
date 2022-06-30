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

import static org.projog.clp.MathUtils.safeAdd;
import static org.projog.clp.MathUtils.safeSubtract;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Represents something that can have a range of numeric values.
 * <p>
 * Could be a single immutable value (see {@link FixedValue}), a variable that can have one or more possible values (see
 * {@link Variable}) or a composite that contains other {@code Expression} objects (e.g. {@link Add}).
 */
public interface Expression {
   long getMin(ReadConstraintStore constraintStore);

   long getMax(ReadConstraintStore constraintStore);

   default ExpressionResult setNot(ConstraintStore store, long not) {
      if (getMax(store) == not) {
         return setMax(store, safeSubtract(not, 1));
      } else if (getMin(store) == not) {
         return setMin(store, safeAdd(not, 1));
      } else {
         return ExpressionResult.VALID;
      }
   }

   ExpressionResult setMin(ConstraintStore constraintStore, long min);

   ExpressionResult setMax(ConstraintStore constraintStore, long max);

   /**
    * Traverse this expression.
    * <p>
    * If this expression is a composite then all its sub-expressions will be traversed.
    *
    * @param consumer will be called for each {@code Expression} contained within this {@code Expression}.
    */
   void walk(Consumer<Expression> consumer);

   /**
    * Returns a {@code Expression} with {@code Variable}s in this {@code Expression} replaced with values returned from
    * the given function.
    *
    * @param function returns the {@code Variable} to use as a replacement for the {@code Variable} it is called with,
    * or {@code null} if the original {@code Variable} should continue to be used.
    * @return a new {@code Expression} with {@code Variable}s in this {@code Expression} replaced with versions returned
    * from {@code function}.
    */
   Expression replaceVariables(Function<Variable, Variable> function);
}

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
public interface Constraint {
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
   Constraint replace(Function<LeafExpression, LeafExpression> function);
}

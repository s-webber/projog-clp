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
package org.projog.clp.bool;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import org.projog.clp.Constraint;
import org.projog.clp.ConstraintResult;
import org.projog.clp.ConstraintStore;
import org.projog.clp.Expression;
import org.projog.clp.LeafExpression;
import org.projog.clp.ReadConstraintStore;

public final class Not implements Constraint {
   private final Constraint constraint;

   public Not(Constraint constraint) {
      this.constraint = Objects.requireNonNull(constraint);
   }

   @Override
   public ConstraintResult enforce(ConstraintStore constraintStore) {
      return constraint.prevent(constraintStore);
   }

   @Override
   public ConstraintResult prevent(ConstraintStore constraintStore) {
      return constraint.enforce(constraintStore);
   }

   @Override
   public ConstraintResult reify(ReadConstraintStore constraintStore) {
      ConstraintResult r = constraint.reify(constraintStore);
      switch (r) {
         case MATCHED:
            return ConstraintResult.FAILED;
         case FAILED:
            return ConstraintResult.MATCHED;
         case UNRESOLVED:
            return ConstraintResult.UNRESOLVED;
         default:
            throw new IllegalArgumentException("ConstraintResult: " + r);
      }
   }

   @Override
   public void walk(Consumer<Expression> r) {
      constraint.walk(r);
   }

   @Override
   public Not replace(Function<LeafExpression, LeafExpression> r) {
      return new Not(constraint.replace(r));
   }

   @Override
   public String toString() {
      return "Not [constraint=" + constraint + "]";
   }
}

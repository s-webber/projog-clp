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
import org.projog.clp.ReadConstraintStore;
import org.projog.clp.Variable;

public final class Equivalent implements Constraint {
   private final Constraint left;
   private final Constraint right;

   public Equivalent(Constraint left, Constraint right) {
      this.left = Objects.requireNonNull(left);
      this.right = Objects.requireNonNull(right);
   }

   @Override
   public ConstraintResult enforce(ConstraintStore constraintStore) {
      ConstraintResult r = enforce(constraintStore, left, right);
      if (r != ConstraintResult.UNRESOLVED) {
         return r;
      } else {
         return enforce(constraintStore, right, left);
      }
   }

   private static ConstraintResult enforce(ConstraintStore constraintStore, Constraint a, Constraint b) {
      ConstraintResult r = a.reify(constraintStore);
      if (r == ConstraintResult.MATCHED) {
         return b.enforce(constraintStore);
      } else if (r == ConstraintResult.FAILED) {
         return b.prevent(constraintStore);
      } else {
         return ConstraintResult.UNRESOLVED;
      }
   }

   @Override
   public ConstraintResult reify(ReadConstraintStore constraintStore) {
      ConstraintResult r1 = left.reify(constraintStore);
      if (r1 == ConstraintResult.UNRESOLVED) {
         return ConstraintResult.UNRESOLVED;
      }

      ConstraintResult r2 = right.reify(constraintStore);
      if (r2 == ConstraintResult.UNRESOLVED) {
         return ConstraintResult.UNRESOLVED;
      }

      return r1 == r2 ? ConstraintResult.MATCHED : ConstraintResult.FAILED;
   }

   @Override
   public ConstraintResult prevent(ConstraintStore constraintStore) {
      return new Xor(left, right).enforce(constraintStore);
   }

   @Override
   public void walk(Consumer<Expression> r) {
      left.walk(r);
      right.walk(r);
   }

   @Override
   public Equivalent replaceVariables(Function<Variable, Variable> r) {
      return new Equivalent(left.replaceVariables(r), right.replaceVariables(r));
   }

   @Override
   public String toString() {
      return "Equivalent [left=" + left + ", right=" + right + "]";
   }
}

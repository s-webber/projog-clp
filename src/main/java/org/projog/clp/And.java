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

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class And implements Constraint {
   private final Constraint left;
   private final Constraint right;

   public And(Constraint left, Constraint right) {
      this.left = Objects.requireNonNull(left);
      this.right = Objects.requireNonNull(right);
   }

   @Override
   public ConstraintResult enforce(ConstraintStore constraintStore) {
      ConstraintResult r1 = left.enforce(constraintStore);
      if (r1 == ConstraintResult.FAILED) {
         return ConstraintResult.FAILED;
      }

      ConstraintResult r2 = right.enforce(constraintStore);
      if (r2 == ConstraintResult.FAILED) {
         return ConstraintResult.FAILED;
      }

      return r1 == ConstraintResult.MATCHED && r2 == ConstraintResult.MATCHED ? ConstraintResult.MATCHED : ConstraintResult.UNRESOLVED;
   }

   @Override
   public ConstraintResult reify(ReadConstraintStore constraintStore) {
      ConstraintResult r1 = left.reify(constraintStore);
      if (r1 == ConstraintResult.FAILED) {
         return ConstraintResult.FAILED;
      }

      ConstraintResult r2 = right.reify(constraintStore);
      if (r2 == ConstraintResult.FAILED) {
         return ConstraintResult.FAILED;
      }

      return r1 == ConstraintResult.MATCHED && r2 == ConstraintResult.MATCHED ? ConstraintResult.MATCHED : ConstraintResult.UNRESOLVED;
   }

   @Override
   public ConstraintResult prevent(ConstraintStore constraintStore) {
      ConstraintResult r1 = left.reify(constraintStore);
      if (r1 == ConstraintResult.FAILED) {
         return ConstraintResult.MATCHED;
      }

      ConstraintResult r2 = right.reify(constraintStore);
      if (r2 == ConstraintResult.FAILED) {
         return ConstraintResult.MATCHED;
      }

      if (r1 == ConstraintResult.MATCHED && r2 == ConstraintResult.MATCHED) {
         return ConstraintResult.FAILED;
      } else if (r1 == ConstraintResult.MATCHED) {
         return right.prevent(constraintStore);
      } else if (r2 == ConstraintResult.MATCHED) {
         return left.prevent(constraintStore);
      } else { // must be both UNRESOLVED
         return ConstraintResult.UNRESOLVED;
      }
   }

   @Override
   public void walk(Consumer<Expression> r) {
      left.walk(r);
      right.walk(r);
   }

   @Override
   public And replaceVariables(Function<Variable, Variable> r) {
      return new And(left.replaceVariables(r), right.replaceVariables(r));
   }

   @Override
   public String toString() {
      return "And [left=" + left + ", right=" + right + "]";
   }
}

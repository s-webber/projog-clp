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

public final class And implements Constraint {
   private final Constraint left;
   private final Constraint right;

   public And(Constraint left, Constraint right) {
      this.left = Objects.requireNonNull(left);
      this.right = Objects.requireNonNull(right);
   }

   @Override
   public ConstraintResult enforce(ConstraintStore constraintStore) {
      return enforce(left, right, constraintStore);
   }

   static ConstraintResult enforce(Constraint left, Constraint right, ConstraintStore constraintStore) {
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
      return Nand.enforce(left, right, constraintStore);
   }

   @Override
   public void walk(Consumer<Expression> r) {
      left.walk(r);
      right.walk(r);
   }

   @Override
   public And replace(Function<LeafExpression, LeafExpression> r) {
      return new And(left.replace(r), right.replace(r));
   }

   @Override
   public String toString() {
      return "And [left=" + left + ", right=" + right + "]";
   }
}

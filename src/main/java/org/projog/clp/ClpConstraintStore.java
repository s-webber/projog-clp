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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.projog.clp.bool.Equivalent;
import org.projog.clp.compare.Between;
import org.projog.clp.compare.EqualTo;
import org.projog.clp.compare.LessThan;
import org.projog.clp.compare.LessThanOrEqualTo;
import org.projog.clp.compare.NotEqualTo;

/** A collection of constraints and variables that represent a problem domain. */
public final class ClpConstraintStore implements ConstraintStore {
   // variables
   private final int variableCtr;
   private final Variable[] variables;
   private final VariableState[] variableStates;
   // constraints
   private final int constraintCtr;
   private final Constraint[] constraints;
   private final Map<Variable, List<Integer>> constraintsByVariable;
   private final Queue<Integer> constraintQueue;

   private ClpConstraintStore(Builder b) {
      this.variableCtr = b.variables.size();
      this.variables = b.variables.toArray(new Variable[variableCtr]);
      this.variableStates = new VariableState[variableCtr];
      for (int i = 0; i < variableCtr; i++) {
         variableStates[i] = new VariableState();
      }

      this.constraintCtr = b.constraints.size();
      this.constraints = b.constraints.toArray(new Constraint[constraintCtr]);
      this.constraintsByVariable = b.constraintsByVariable;
      this.constraintQueue = new LinkedList<>();
      for (int i = 0; i < constraintCtr; i++) {
         constraintQueue.add(i);
      }
   }

   private ClpConstraintStore(ClpConstraintStore original) {
      this.variableCtr = original.variableCtr;
      this.variables = original.variables; // TODO copy?
      this.variableStates = new VariableState[variableCtr];
      for (int i = 0; i < variableCtr; i++) {
         this.variableStates[i] = original.variableStates[i].copy();
      }

      this.constraintCtr = original.constraintCtr;
      this.constraints = new Constraint[constraintCtr];
      for (int i = 0; i < constraintCtr; i++) {
         this.constraints[i] = original.constraints[i];
      }
      this.constraintsByVariable = original.constraintsByVariable; // TODO copy?
      this.constraintQueue = new LinkedList<>();
   }

   ClpConstraintStore copy() {
      return new ClpConstraintStore(this);
   }

   public int getVariablesCount() {
      return variableCtr;
   }

   public Variable getVariable(int idx) {
      return variables[idx];
   }

   public VariableState getVariableState(int idx) {
      return variableStates[idx];
   }

   public long getValue(Variable id) {
      long max = variableStates[id.getId()].getMax();
      long min = variableStates[id.getId()].getMin();
      if (min == max) {
         return min;
      } else {
         throw new IllegalStateException(min + " " + max);
      }
   }

   private ExpressionResult update(Expression e, VariableStateResult r) {
      if (r == VariableStateResult.UPDATED) {
         List<Integer> list = constraintsByVariable.get(e);
         if (list != null) {
            for (Integer constraintId : list) {
               if (constraints[constraintId] != null && !constraintQueue.contains(constraintId)) {
                  constraintQueue.add(constraintId);
               }
            }
         }
      }
      return r == VariableStateResult.FAILED ? ExpressionResult.INVALID : ExpressionResult.VALID;
   }

   public boolean resolve() {
      while (!constraintQueue.isEmpty()) {
         Integer next = constraintQueue.poll();
         Constraint c = constraints[next];
         if (c != null) {
            ConstraintResult result = c.enforce(this);
            if (result == ConstraintResult.FAILED) {
               return false;
            }
            if (result == ConstraintResult.MATCHED) {
               constraints[next] = null;
            }
         }
      }
      return true;
   }

   private VariableState getExpression(Expression e) {
      return variableStates[((Variable) e).getId()];
   }

   @Override
   public long getMin(Expression id) {
      return getExpression(id).getMin();
   }

   @Override
   public long getMax(Expression id) {
      return getExpression(id).getMax();
   }

   @Override
   public ExpressionResult setValue(Expression id, long value) {
      return update(id, getExpression(id).setValue(value));
   }

   @Override
   public ExpressionResult setMin(Expression id, long min) {
      return update(id, getExpression(id).setMin(min));
   }

   @Override
   public ExpressionResult setMax(Expression id, long max) {
      return update(id, getExpression(id).setMax(max));
   }

   @Override
   public ExpressionResult setNot(Expression id, long not) {
      return update(id, getExpression(id).setNot(not));
   }

   public static class Builder {
      private final List<Variable> variables = new ArrayList<>();
      private final List<Constraint> constraints = new ArrayList<>();
      private final Map<Variable, List<Integer>> constraintsByVariable = new HashMap<>();

      public ClpConstraintStore build() {
         return new ClpConstraintStore(this);
      }

      public Variable createVariable() {
         Variable v = new Variable(variables.size());
         variables.add(v);
         return v;
      }

      public void addConstraint(Constraint constraint) {
         int constraintId = constraints.size();
         constraints.add(constraint);
         Set<Variable> variables = new HashSet<>();
         constraint.walk(v -> {
            if (v instanceof Variable) {
               variables.add((Variable) v);
            }
         });
         for (Variable v : variables) {
            List<Integer> list = constraintsByVariable.get(v);
            if (list == null) {
               list = new ArrayList<>();
               constraintsByVariable.put(v, list);
            }
            list.add(constraintId);
         }
      }

      public EnforceAll enforce(List<Variable> e) {
         return new EnforceAll(this, e.toArray(new Variable[e.size()]));
      }

      public EnforceAll enforce(Variable... e) {
         return new EnforceAll(this, e);
      }

      public Enforce enforce(long i) {
         return new Enforce(this, new FixedValue(i));
      }

      public Enforce enforce(Expression e) {
         return new Enforce(this, e);
      }

      @Deprecated
      @Override
      public boolean equals(Object o) {
         throw new UnsupportedOperationException();
      }
   }

   public static class EnforceAll {
      private final Builder builder;
      private final Variable[] variables;

      private EnforceAll(Builder builder, Variable[] variables) {
         this.builder = builder;
         this.variables = variables;
      }

      public EnforceAll distinct() {
         for (int i1 = 0; i1 < variables.length - 1; i1++) {
            for (int i2 = i1 + 1; i2 < variables.length; i2++) {
               builder.addConstraint(new NotEqualTo(variables[i1], variables[i2]));
            }
         }
         return this;
      }

      public EnforceAll notEqualTo(long opposite) {
         return notEqualTo(new FixedValue(opposite));
      }

      public EnforceAll notEqualTo(Expression opposite) {
         for (Variable v : variables) {
            builder.addConstraint(new NotEqualTo(v, opposite));
         }
         return this;
      }

      public EnforceAll between(long min, long max) {
         for (Variable v : variables) {
            builder.addConstraint(new Between(v, min, max));
         }
         return this;
      }

      @Deprecated
      @Override
      public boolean equals(Object o) {
         throw new UnsupportedOperationException();
      }
   }

   public static class Enforce {
      private final Builder builder;
      private final Expression expression;

      private Enforce(Builder builder, Expression expression) {
         this.builder = builder;
         this.expression = expression;
      }

      public void equalTo(Expression opposite) {
         builder.addConstraint(new EqualTo(expression, opposite));
      }

      public void notEqualTo(Expression opposite) {
         builder.addConstraint(new NotEqualTo(expression, opposite));
      }

      public void lessThan(Expression opposite) {
         builder.addConstraint(new LessThan(expression, opposite));
      }

      public void lessThanOrEqualTo(Expression opposite) {
         builder.addConstraint(new LessThanOrEqualTo(expression, opposite));
      }

      public void between(long min, long max) {
         builder.addConstraint(new Between(expression, min, max));
      }

      public void equivalentTo(Constraint equivalent) {
         // TODO avoid cast
         builder.addConstraint(new Equivalent((Constraint) expression, equivalent));
      }

      @Deprecated
      @Override
      public boolean equals(Object o) {
         throw new UnsupportedOperationException();
      }
   }
}

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

public final class ClpEnvironment implements Variables {
   // variables
   private final int variableCtr;
   private final Variable[] variables;
   private final VariableState[] variableStates;
   // constraints
   private final int constraintCtr;
   private final Constraint[] constraints;
   private final Map<Variable, List<Integer>> constraintsByVariable;
   private final Queue<Integer> constraintQueue;

   private ClpEnvironment(Builder b) {
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

   private ClpEnvironment(ClpEnvironment original) {
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

   ClpEnvironment copy() {
      return new ClpEnvironment(this);
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

   private ExpressionResult update(Expression e, ExpressionResult r) {
      if (r == ExpressionResult.UPDATED) {
         List<Integer> list = constraintsByVariable.get(e);
         if (list != null) {
            for (Integer constraintId : list) {
               if (constraints[constraintId] != null && !constraintQueue.contains(constraintId)) {
                  constraintQueue.add(constraintId);
               }
            }
         }
      }
      return r;
   }

   public boolean resolve() {
      while (!constraintQueue.isEmpty()) {
         Integer next = constraintQueue.poll();
         Constraint c = constraints[next];
         if (c != null) {
            ConstraintResult result = c.fire(this);
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
      private List<Variable> variables = new ArrayList<>();
      private List<Constraint> constraints = new ArrayList<>();
      private Map<Variable, List<Integer>> constraintsByVariable = new HashMap<>();

      public ClpEnvironment build() {
         return new ClpEnvironment(this);
      }

      public Variable createVariable(String name) {
         // TODO ensure name is unique?
         Variable v = new Variable(variables.size(), name);
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

      public EnforceAll enforce(Variable... e) {
         return new EnforceAll(this, e);
      }

      public Enforce enforce(int i) {
         return new Enforce(this, new FixedValue(i));
      }

      public Enforce enforce(Expression e) {
         return new Enforce(this, e);
      }
   }

   public static class EnforceAll {
      private final Builder b;
      private final Variable[] v;

      private EnforceAll(Builder b, Variable[] v) {
         this.b = b;
         this.v = v;
      }

      public void distinct() {
         for (int i1 = 0; i1 < v.length - 1; i1++) {
            for (int i2 = i1 + 1; i2 < v.length; i2++) {
               b.addConstraint(new NotEqualTo(v[i1], v[i2]));
            }
         }
      }
   }

   public static class Enforce {
      private final Builder b;
      private final Expression e;

      private Enforce(Builder b, Expression e) {
         this.b = b;
         this.e = e;
      }

      public void equalTo(Expression opposite) {
         b.addConstraint(new EqualTo(e, opposite));
      }

      public void lessThan(Expression opposite) {
         b.addConstraint(new LessThan(e, opposite));
      }

      public void between(int min, int max) {
         b.addConstraint(new Between(e, min, max));
      }
   }
}

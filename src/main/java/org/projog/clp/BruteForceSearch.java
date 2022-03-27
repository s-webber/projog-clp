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

/**
 * Performs a brute force search.
 * <p>
 * Tries all possible values in search of a valid solution. When a solution is found it backtracks to find alternative
 * solutions.
 */
public final class BruteForceSearch {
   private final ClpConstraintStore original;
   private final ClpConstraintStore[] copies;
   private final Possibilities[] p;
   private final int[] indexes;
   private int idx = 0;

   public BruteForceSearch(ClpConstraintStore environment) {
      this.original = environment;
      int variablesCount = environment.getVariablesCount();
      if (variablesCount == 0) {
         throw new IllegalStateException();
      }
      this.copies = new ClpConstraintStore[variablesCount];
      this.p = new Possibilities[variablesCount];
      this.indexes = new int[variablesCount];
      for (int i = 0; i < variablesCount; i++) {
         indexes[i] = i;
      }
   }

   /**
    * Finds a valid solution.
    * <p>
    * If a valid solution was found on a previous call then it will backtrack in an attempt to find an alternative
    * solution.
    *
    * @return the next solution or, if no remaining solutions, {@code null}
    */
   public ClpConstraintStore next() {
      Possibilities current;
      while ((current = getCurrent()) != null) {
         long next = current.next();
         copies[idx] = (idx == 0 ? original : copies[idx - 1]).copy();
         if (copies[idx].getVariable(indexes[idx]).setValue(copies[idx], next) == ExpressionResult.FAILED) {
            // do nothing
         } else if (!copies[idx].resolve()) {
            // do nothing
         } else if (idx == p.length - 1) {
            return copies[idx];
         } else {
            idx++;
         }
      }
      return null;
   }

   private Possibilities getCurrent() {
      if (idx == -1) {
         return null;
      }

      Possibilities result = p[idx];
      if (result == null) {
         ClpConstraintStore copy = (idx == 0 ? original : copies[idx - 1]).copy();
         copies[idx] = copy;

         // sort in ascending order of least possibilities
         long min = copy.getVariableState(indexes[idx]).count();
         int minIdx = idx;
         for (int i = idx + 1; i < indexes.length; i++) {
            long count = copy.getVariableState(indexes[i]).count();
            if (count < min) {
               min = count;
               minIdx = i;
            }
         }
         if (min > Integer.MAX_VALUE) {
            // exit to avoid thread spending long time in search
            // TODO what max value to use? TODO throw projog-clp specific subclass of IllegalStateException
            throw new IllegalStateException("Variables not sufficiently bound. Too many possibilities.");
         }
         if (idx != minIdx) {
            int tmp = indexes[idx];
            indexes[idx] = indexes[minIdx];
            indexes[minIdx] = tmp;
         }

         result = copy.getVariableState(indexes[idx]).getPossibilities();
         p[idx] = result;
      }

      while (!result.hasNext()) {
         p[idx] = null;
         copies[idx] = null;
         idx--;
         if (idx == -1) {
            return null;
         }
         result = p[idx];
      }

      return result;
   }
}

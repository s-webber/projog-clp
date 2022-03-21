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

public final class BruteForceSearch {
   private final ClpEnvironment original;
   private final ClpEnvironment[] copies;
   private final Possibilities[] p;
   private final int[] indexes;
   private int idx = 0;

   public BruteForceSearch(ClpEnvironment v) {
      this.original = v.copy(); //TODO required?
      int variablesCount = v.getVariablesCount();
      this.copies = new ClpEnvironment[variablesCount];
      this.p = new Possibilities[variablesCount];
      this.indexes = new int[variablesCount];
      for (int i = 0; i < variablesCount; i++) {
         indexes[i] = i;
      }
   }

   public ClpEnvironment next() {
      Possibilities current;
      while ((current = getcurrent()) != null) {
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

   private Possibilities getcurrent() {
      if (idx == -1) {
         return null;
      }

      Possibilities current = p[idx];
      if (current == null) {
         ClpEnvironment copy = (idx == 0 ? original : copies[idx - 1]).copy();
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
         if (idx != minIdx) {
            int tmp = indexes[idx];
            indexes[idx] = indexes[minIdx];
            indexes[minIdx] = tmp;
         }

         current = copy.getVariableState(indexes[idx]).getPossibilities();
         p[idx] = current;
      }

      while (!current.hasNext()) {
         p[idx] = null;
         copies[idx] = null;
         idx--;
         if (idx == -1) {
            return null;
         }
         current = p[idx];
      }

      return current;
   }
}

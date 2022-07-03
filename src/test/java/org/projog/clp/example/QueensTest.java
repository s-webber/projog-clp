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
package org.projog.clp.example;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import org.projog.clp.BruteForceSearch;
import org.projog.clp.ClpConstraintStore;
import org.projog.clp.FixedValue;
import org.projog.clp.Variable;
import org.projog.clp.compare.NotEqualTo;
import org.projog.clp.math.Subtract;
import org.testng.annotations.Test;

/**
 * Example of solving the eight queens puzzle.
 * <p>
 * See: https://en.wikipedia.org/wiki/Eight_queens_puzzle
 */
public class QueensTest {
   private static final int NUM_QUEENS = 8;
   private static final int NUM_SOLUTIONS = 92;

   @Test
   public void test() {
      ClpConstraintStore.Builder builder = new ClpConstraintStore.Builder();
      Variable[] variables = new Variable[NUM_QUEENS];
      for (int i = 0; i < variables.length; i++) {
         variables[i] = builder.createVariable();
      }

      builder.enforce(variables).distinct().between(1, NUM_QUEENS);

      for (int i1 = 0; i1 < NUM_QUEENS - 1; i1++) {
         for (int i2 = i1 + 1; i2 < NUM_QUEENS; i2++) {
            FixedValue v = new FixedValue(i2 - i1);
            Variable x = variables[i1];
            Variable y = variables[i2];
            builder.addConstraint(new NotEqualTo(v, new Subtract(x, y)));
            builder.addConstraint(new NotEqualTo(v, new Subtract(y, x)));
         }
      }

      ClpConstraintStore environment = builder.build();
      environment.resolve();
      BruteForceSearch bruteForceSearch = new BruteForceSearch(environment);
      ClpConstraintStore solution = bruteForceSearch.next();

      // confirm values in first solution found
      int[] expected = {1, 5, 8, 6, 3, 7, 2, 4};
      for (int i = 0; i < NUM_QUEENS; i++) {
         assertEquals(expected[i], solution.getValue(variables[i]));
      }

      // confirm that, as well as the solution already found, there are 92 solutions in total
      for (int i = 2; i <= NUM_SOLUTIONS; i++) {
         assertNotNull(bruteForceSearch.next());
      }
      assertNull(bruteForceSearch.next());
   }
}

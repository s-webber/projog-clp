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

import static org.projog.clp.example.ExpressionUtils.add;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.projog.clp.BruteForceSearch;
import org.projog.clp.ClpConstraintStore;
import org.projog.clp.FixedValue;
import org.projog.clp.Variable;
import org.projog.clp.compare.EqualTo;
import org.testng.annotations.Test;

/**
 * Example of using constraints as expressions.
 * <p>
 * "Ten cells numbered 0,..,9 inscribe a 10-digit number such that each cell, say <i>i</i>, indicates the total number
 * of occurrences of the digit <i>i</i> in this number. Find this number." Reference: Krzysztof Apt, <i>Principles of
 * Constraint Programming</i>, Cambridge University Press, 2003, ISBN 0-521-82583-0.
 */
public class DigitOccurrencesTest {
   @Test
   public void test() {
      ClpConstraintStore.Builder b = new ClpConstraintStore.Builder();

      // create variables
      Variable[] variables = new Variable[10];
      for (int i = 0; i < variables.length; i++) {
         variables[i] = b.createVariable();
      }

      // for each variable add a constraint to enforce that
      // its value is equal to the number of occurrences of its ordinal
      for (int i = 0; i < variables.length; i++) {
         FixedValue f = new FixedValue(i);
         EqualTo[] e = new EqualTo[10];
         for (int q = 0; q < variables.length; q++) {
            e[q] = new EqualTo(f, variables[q]);
         }
         b.enforce(variables[i]).equalTo(add(e));
      }

      ClpConstraintStore store = b.build();
      store.resolve();
      BruteForceSearch bruteForceSearch = new BruteForceSearch(store);

      ClpConstraintStore result = bruteForceSearch.next();
      int[] expected = {6, 2, 1, 0, 0, 0, 1, 0, 0, 0};
      for (int i = 0; i < variables.length; i++) {
         assertEquals(expected[i], result.getValue(variables[i]));
      }

      assertNull(bruteForceSearch.next());
   }
}

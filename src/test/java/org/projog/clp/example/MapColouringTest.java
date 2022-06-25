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
import static org.testng.Assert.assertNull;

import org.testng.annotations.Test;
import org.projog.clp.BruteForceSearch;
import org.projog.clp.ClpConstraintStore;
import org.projog.clp.Variable;

public class MapColouringTest {
   @Test
   public void test() {
      ClpConstraintStore.Builder builder = new ClpConstraintStore.Builder();
      Variable a = builder.createVariable();
      Variable b = builder.createVariable();
      Variable c = builder.createVariable();
      Variable d = builder.createVariable();

      // a|b|c
      // -----
      //   d
      builder.enforce(a, b, c, d).between(1, 3);
      builder.enforce(a, b, c).notEqualTo(d);
      builder.enforce(a, c).notEqualTo(b);

      ClpConstraintStore environment = builder.build();
      environment.resolve();
      BruteForceSearch bruteForceSearch = new BruteForceSearch(environment);

      int[][] expected = {
                  // expected values for a, b, c and d
                  {1, 2, 1, 3},
                  {1, 3, 1, 2},
                  {2, 1, 2, 3},
                  {2, 3, 2, 1},
                  {3, 1, 3, 2},
                  {3, 2, 3, 1},};
      for (int[] e : expected) {
         ClpConstraintStore solution = bruteForceSearch.next();
         assertEquals(e[0], solution.getValue(a));
         assertEquals(e[1], solution.getValue(b));
         assertEquals(e[2], solution.getValue(c));
         assertEquals(e[3], solution.getValue(d));
      }

      assertNull(bruteForceSearch.next());
   }
}

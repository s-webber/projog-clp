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
import static org.projog.clp.example.ExpressionUtils.add;

import org.testng.annotations.Test;
import org.projog.clp.BruteForceSearch;
import org.projog.clp.ClpConstraintStore;
import org.projog.clp.Variable;

public class SimpleExampleTest {
   @Test
   public void test() {
      ClpConstraintStore.Builder builder = new ClpConstraintStore.Builder();
      Variable x = builder.createVariable();
      Variable y = builder.createVariable();
      Variable z = builder.createVariable();

      // x in 1..5
      // y in 0..4
      // x < y
      // z = 1 + x + y
      builder.enforce(x).between(1, 5);
      builder.enforce(y).between(0, 4);
      builder.enforce(x).lessThan(y);
      builder.enforce(z).equalTo(add(1, x, y));

      ClpConstraintStore environment = builder.build();
      assertEquals(Long.MIN_VALUE, x.getMin(environment));
      assertEquals(Long.MAX_VALUE, x.getMax(environment));
      assertEquals(Long.MIN_VALUE, y.getMin(environment));
      assertEquals(Long.MAX_VALUE, y.getMax(environment));
      assertEquals(Long.MIN_VALUE, z.getMin(environment));
      assertEquals(Long.MAX_VALUE, z.getMax(environment));

      environment.resolve();

      assertEquals(1, x.getMin(environment));
      assertEquals(3, x.getMax(environment));
      assertEquals(2, y.getMin(environment));
      assertEquals(4, y.getMax(environment));
      assertEquals(4, z.getMin(environment));
      assertEquals(8, z.getMax(environment));

      BruteForceSearch bruteForceSearch = new BruteForceSearch(environment);

      ClpConstraintStore solution = bruteForceSearch.next();
      assertEquals(1, solution.getValue(x));
      assertEquals(2, solution.getValue(y));
      assertEquals(4, solution.getValue(z));

      solution = bruteForceSearch.next();
      assertEquals(1, solution.getValue(x));
      assertEquals(3, solution.getValue(y));
      assertEquals(5, solution.getValue(z));

      solution = bruteForceSearch.next();
      assertEquals(1, solution.getValue(x));
      assertEquals(4, solution.getValue(y));
      assertEquals(6, solution.getValue(z));

      solution = bruteForceSearch.next();
      assertEquals(2, solution.getValue(x));
      assertEquals(3, solution.getValue(y));
      assertEquals(6, solution.getValue(z));

      solution = bruteForceSearch.next();
      assertEquals(2, solution.getValue(x));
      assertEquals(4, solution.getValue(y));
      assertEquals(7, solution.getValue(z));

      solution = bruteForceSearch.next();
      assertEquals(3, solution.getValue(x));
      assertEquals(4, solution.getValue(y));
      assertEquals(8, solution.getValue(z));

      assertNull(bruteForceSearch.next());
   }
}

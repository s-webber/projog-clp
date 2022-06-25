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

import static org.projog.clp.example.ConstraintUtils.is;
import static org.projog.clp.example.ExpressionUtils.add;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import org.projog.clp.BruteForceSearch;
import org.projog.clp.ClpConstraintStore;
import org.projog.clp.Variable;
import org.testng.annotations.Test;

public class SingleAnswerTest {
   @Test
   public void test() {
      ClpConstraintStore.Builder builder = new ClpConstraintStore.Builder();
      Variable a1 = builder.createVariable();
      Variable a2 = builder.createVariable();
      Variable a3 = builder.createVariable();
      Variable a4 = builder.createVariable();
      Variable a5 = builder.createVariable();
      Variable a6 = builder.createVariable();

      builder.enforce(a1, a2, a3, a4, a5, a6).between(0, 1);

      // All of the below.
      builder.enforce(a1).equivalentTo(is(5).equalTo(add(a2, a3, a4, a5, a6)));
      // None of the below.
      builder.enforce(a2).equivalentTo(is(0).equalTo(add(a3, a4, a5, a6)));
      // All of the above.
      builder.enforce(a3).equivalentTo(is(2).equalTo(add(a1, a2)));
      // One of the above.
      builder.enforce(a4).equivalentTo(is(1).equalTo(add(a1, a2, a3)));
      // None of the above.
      builder.enforce(a5).equivalentTo(is(0).equalTo(add(a1, a2, a3, a4)));
      // None of the above.
      builder.enforce(a6).equivalentTo(is(0).equalTo(add(a1, a2, a3, a4, a5)));

      ClpConstraintStore environment = builder.build();
      assertTrue(environment.resolve());

      BruteForceSearch bruteForceSearch = new BruteForceSearch(environment);
      ClpConstraintStore solution = bruteForceSearch.next();
      assertEquals(0, solution.getValue(a1));
      assertEquals(0, solution.getValue(a2));
      assertEquals(0, solution.getValue(a3));
      assertEquals(0, solution.getValue(a4));
      assertEquals(1, solution.getValue(a5));
      assertEquals(0, solution.getValue(a6));

      assertNull(bruteForceSearch.next());
   }
}

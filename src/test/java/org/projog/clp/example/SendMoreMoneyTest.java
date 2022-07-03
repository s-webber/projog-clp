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
import static org.projog.clp.example.ExpressionUtils.multiply;

import org.testng.annotations.Test;
import org.projog.clp.BruteForceSearch;
import org.projog.clp.ClpConstraintStore;
import org.projog.clp.Expression;
import org.projog.clp.Variable;
import org.projog.clp.compare.EqualTo;

public class SendMoreMoneyTest {
   @Test
   public void test() {
      ClpConstraintStore.Builder builder = new ClpConstraintStore.Builder();
      Variable s = builder.createVariable();
      Variable e = builder.createVariable();
      Variable n = builder.createVariable();
      Variable d = builder.createVariable();
      Variable m = builder.createVariable();
      Variable o = builder.createVariable();
      Variable r = builder.createVariable();
      Variable y = builder.createVariable();

      builder.enforce(s, e, n, d, m, o, r, y).distinct().between(0, 9);
      builder.enforce(s, m).notEqualTo(0);

      //           1000*S + 100*E + 10*N + D +
      //           1000*M + 100*O + 10*R + E #=
      // 10000*M + 1000*O + 100*N + 10*E + Y,
      Expression send = add(multiply(1000, s), multiply(100, e), multiply(10, n), d);
      Expression more = add(multiply(1000, m), multiply(100, o), multiply(10, r), e);
      Expression money = add(multiply(10000, m), multiply(1000, o), multiply(100, n), multiply(10, e), y);
      builder.addConstraint(new EqualTo(add(send, more), money));

      ClpConstraintStore environment = builder.build();
      environment.resolve();
      BruteForceSearch bruteForceSearch = new BruteForceSearch(environment);
      ClpConstraintStore solution = bruteForceSearch.next();

      assertEquals(9, solution.getValue(s));
      assertEquals(5, solution.getValue(e));
      assertEquals(6, solution.getValue(n));
      assertEquals(7, solution.getValue(d));
      assertEquals(1, solution.getValue(m));
      assertEquals(0, solution.getValue(o));
      assertEquals(8, solution.getValue(r));
      assertEquals(2, solution.getValue(y));

      assertNull(bruteForceSearch.next());
   }
}

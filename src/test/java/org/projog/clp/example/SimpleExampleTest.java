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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.projog.clp.Add;
import org.projog.clp.BruteForceSearch;
import org.projog.clp.ClpEnvironment;
import org.projog.clp.Expression;
import org.projog.clp.FixedValue;
import org.projog.clp.Variable;

public class SimpleExampleTest {
   @Test
   public void test() {
      ClpEnvironment.Builder b = new ClpEnvironment.Builder();
      Variable x = b.createVariable("x");
      Variable y = b.createVariable("y");
      Variable z = b.createVariable("z");

      b.enforce(x).between(1, 5);
      b.enforce(y).between(0, 4);
      b.enforce(x).lessThan(y);
      b.enforce(z).equalTo(add(x, y, new FixedValue(1)));

      ClpEnvironment v = b.build();
      assertEquals(Long.MIN_VALUE, x.getMin(v));
      assertEquals(Long.MAX_VALUE, x.getMax(v));
      assertEquals(Long.MIN_VALUE, y.getMin(v));
      assertEquals(Long.MAX_VALUE, y.getMax(v));
      assertEquals(Long.MIN_VALUE, z.getMin(v));
      assertEquals(Long.MAX_VALUE, z.getMax(v));

      v.resolve();

      assertEquals(1, x.getMin(v));
      assertEquals(3, x.getMax(v));
      assertEquals(2, y.getMin(v));
      assertEquals(4, y.getMax(v));
      assertEquals(4, z.getMin(v));
      assertEquals(8, z.getMax(v));

      BruteForceSearch f = new BruteForceSearch(v);

      ClpEnvironment q = f.next();
      assertEquals(1, q.getValue(x));
      assertEquals(2, q.getValue(y));
      assertEquals(4, q.getValue(z));

      q = f.next();
      assertEquals(1, q.getValue(x));
      assertEquals(3, q.getValue(y));
      assertEquals(5, q.getValue(z));

      q = f.next();
      assertEquals(1, q.getValue(x));
      assertEquals(4, q.getValue(y));
      assertEquals(6, q.getValue(z));

      q = f.next();
      assertEquals(2, q.getValue(x));
      assertEquals(3, q.getValue(y));
      assertEquals(6, q.getValue(z));

      q = f.next();
      assertEquals(2, q.getValue(x));
      assertEquals(4, q.getValue(y));
      assertEquals(7, q.getValue(z));

      q = f.next();
      assertEquals(3, q.getValue(x));
      assertEquals(4, q.getValue(y));
      assertEquals(8, q.getValue(z));

      assertNull(f.next());
   }

   private static Expression add(Expression... args) {
      Expression result = null;
      for (Expression e : args) {
         if (result == null) {
            result = e;
         } else {
            result = new Add(result, e);
         }
      }
      return result;
   }
}

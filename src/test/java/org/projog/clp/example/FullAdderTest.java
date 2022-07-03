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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import org.projog.clp.BruteForceSearch;
import org.projog.clp.ClpConstraintStore;
import org.projog.clp.ExpressionResult;
import org.projog.clp.Variable;
import org.projog.clp.bool.And;
import org.projog.clp.bool.Or;
import org.projog.clp.bool.Xor;
import org.projog.clp.test.TestData;
import org.projog.clp.test.TestDataProvider;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Example of implementing a full adder.
 * <p>
 * See: https://en.wikipedia.org/wiki/Adder_(electronics)#Full_adder
 */
public class FullAdderTest {
   private ClpConstraintStore store;
   private Variable i1;
   private Variable i2;
   private Variable i3;
   private Variable o1;
   private Variable o2;

   @BeforeMethod
   public void beforeEachTest() {
      ClpConstraintStore.Builder b = new ClpConstraintStore.Builder();

      // create variables:
      i1 = b.createVariable(); // TODO add createBooleanVariable()?
      i2 = b.createVariable();
      i3 = b.createVariable();
      o1 = b.createVariable();
      o2 = b.createVariable();

      // add constraints:
      // o1 #<==> (i1 #\ i2) #\ i3,
      b.enforce(o1).equivalentTo(new Xor(new Xor(i1, i2), i3));
      // o2 #<==> (i1 #/\ i2) #\/ (i3 #/\ (i1 #\ i2)).
      b.enforce(o2).equivalentTo(new Or(new And(i1, i2), new And(i3, new Xor(i1, i2))));

      store = b.build();

      // restrict possible values of variables to 0 or 1:
      for (Variable v : new Variable[] {i1, i2, i3, o1, o2}) {
         v.setMin(store, 0);
         v.setMax(store, 1);
      }
   }

   @Test(dataProvider = "process", dataProviderClass = TestDataProvider.class)
   @TestData({"1,1,1,1,1", "1,1,0,0,1", "1,0,1,0,1", "0,1,1,0,1", "1,0,0,1,0", "0,1,0,1,0", "0,0,1,1,0", "0,0,0,0,0"})
   public void test(Long input1, Long input2, Long input3, Long expected1, Long expected2) {
      set(i1, input1);
      set(i2, input2);
      set(i3, input3);

      assertTrue(store.resolve());

      assertEquals(expected1, o1.getMin(store));
      assertEquals(expected1, o1.getMax(store));
      assertEquals(expected2, o2.getMin(store));
      assertEquals(expected2, o2.getMax(store));
   }

   @Test
   public void testBothOutputsFalse() {
      setFalse(o1);
      setFalse(o2);

      assertTrue(store.resolve());
      assertEquals(0, i1.getMin(store));
      assertEquals(1, i1.getMax(store));
      assertEquals(0, i2.getMin(store));
      assertEquals(1, i2.getMax(store));
      assertEquals(0, i3.getMin(store));
      assertEquals(1, i3.getMax(store));

      BruteForceSearch search = new BruteForceSearch(store);
      ClpConstraintStore solution = search.next();
      assertEquals(0, i1.getMin(solution));
      assertEquals(0, i1.getMax(solution));
      assertEquals(0, i2.getMin(solution));
      assertEquals(0, i2.getMax(solution));
      assertEquals(0, i3.getMin(solution));
      assertEquals(0, i3.getMax(solution));

      assertNull(search.next());
   }

   @Test
   public void testBothOutputsTrue() {
      setTrue(o1);
      setTrue(o2);

      assertTrue(store.resolve());
      assertEquals(0, i1.getMin(store));
      assertEquals(1, i1.getMax(store));
      assertEquals(0, i2.getMin(store));
      assertEquals(1, i2.getMax(store));
      assertEquals(0, i3.getMin(store));
      assertEquals(1, i3.getMax(store));

      BruteForceSearch search = new BruteForceSearch(store);
      ClpConstraintStore solution = search.next();
      assertEquals(1, i1.getMin(solution));
      assertEquals(1, i1.getMax(solution));
      assertEquals(1, i2.getMin(solution));
      assertEquals(1, i2.getMax(solution));
      assertEquals(1, i3.getMin(solution));
      assertEquals(1, i3.getMax(solution));

      assertNull(search.next());
   }

   @Test
   public void test1___1() {
      setFalse(i1);
      setTrue(o2);

      assertTrue(store.resolve());

      assertEquals(1, i2.getMin(store));
      assertEquals(1, i2.getMax(store));
      assertEquals(1, i3.getMin(store));
      assertEquals(1, i3.getMax(store));
      assertEquals(0, o1.getMin(store));
      assertEquals(0, o1.getMax(store));
   }

   @Test
   public void test_1__1() {
      setFalse(i2);
      setTrue(o2);

      assertTrue(store.resolve());

      assertEquals(1, i1.getMin(store));
      assertEquals(1, i1.getMax(store));
      assertEquals(1, i3.getMin(store));
      assertEquals(1, i3.getMax(store));
      assertEquals(0, o1.getMin(store));
      assertEquals(0, o1.getMax(store));
   }

   @Test
   public void test__1_1() {
      setFalse(i3);
      setTrue(o2);

      assertTrue(store.resolve());

      assertEquals(1, i1.getMin(store));
      assertEquals(1, i1.getMax(store));
      assertEquals(1, i2.getMin(store));
      assertEquals(1, i2.getMax(store));
      assertEquals(0, o1.getMin(store));
      assertEquals(0, o1.getMax(store));
   }

   @Test
   public void test1__00() {
      setTrue(i1);
      setFalse(o1);
      setFalse(o2);

      assertFalse(store.resolve());
   }

   @Test
   public void test_1_00() {
      setTrue(i2);
      setFalse(o1);
      setFalse(o2);

      assertFalse(store.resolve());
   }

   @Test
   public void test__100() {
      setTrue(i3);
      setFalse(o1);
      setFalse(o2);

      assertTrue(store.resolve());
      assertNull(new BruteForceSearch(store).next());
   }

   private void setTrue(Variable v) {
      set(v, 1);
   }

   private void setFalse(Variable v) {
      set(v, 0);
   }

   private void set(Variable v, long value) {
      assertSame(ExpressionResult.VALID, v.setMin(store, value));
      assertSame(ExpressionResult.VALID, v.setMax(store, value));
   }
}

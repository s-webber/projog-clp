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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

public class PossibilitiesTest {
   @Test
   public void testWithoutBitSet() {
      int min = 3;
      int max = 10;
      Possibilities p = new Possibilities(min, max, null);
      for (int i = min; i <= max; i++) {
         assertTrue(p.hasNext());
         assertEquals(i, p.next());
      }
      assertFalse(p.hasNext());
   }

   @Test
   public void testWithBitSet() {
      NumberSet b = new NumberSet(3, 10);
      int min = 3;
      int max = 10;
      b.clear(4);
      b.clear(7);
      b.clear(9);

      Possibilities p = new Possibilities(min, max, b);
      assertTrue(p.hasNext());
      assertEquals(3, p.next());
      assertTrue(p.hasNext());
      assertEquals(5, p.next());
      assertTrue(p.hasNext());
      assertEquals(6, p.next());
      assertTrue(p.hasNext());
      assertEquals(8, p.next());
      assertTrue(p.hasNext());
      assertEquals(10, p.next());
      assertFalse(p.hasNext());
   }

   @Test
   public void testWithBitSetNegative() {
      NumberSet b = new NumberSet(-5, 3);
      int min = -5;
      int max = 3;
      b.clear(-3);

      Possibilities p = new Possibilities(min, max, b);
      assertTrue(p.hasNext());
      assertEquals(-5, p.next());
      assertTrue(p.hasNext());
      assertEquals(-4, p.next());
      assertTrue(p.hasNext());
      assertEquals(-2, p.next());
      assertTrue(p.hasNext());
      assertEquals(-1, p.next());
      assertTrue(p.hasNext());
      assertEquals(0, p.next());
      assertTrue(p.hasNext());
      assertEquals(1, p.next());
      assertTrue(p.hasNext());
      assertEquals(2, p.next());
      assertTrue(p.hasNext());
      assertEquals(3, p.next());
      assertFalse(p.hasNext());
   }
}

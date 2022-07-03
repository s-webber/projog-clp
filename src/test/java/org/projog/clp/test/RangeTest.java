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
package org.projog.clp.test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

public class RangeTest {
   @Test
   public void testGetters() {
      Range r = new Range(-42, 5);
      assertEquals(r.min(), -42);
      assertEquals(r.max(), 5);
   }

   @Test
   public void testMaxRange() {
      Range r = new Range(Long.MIN_VALUE, Long.MAX_VALUE);
      assertEquals(r.min(), Long.MIN_VALUE);
      assertEquals(r.max(), Long.MAX_VALUE);
      assertTrue(r.equals(new Range(Long.MIN_VALUE, Long.MAX_VALUE)));
      assertEquals(r.hashCode(), new Range(Long.MIN_VALUE, Long.MAX_VALUE).hashCode());
   }

   @Test
   public void testEquals() {
      Range r = new Range(-42, 5);

      assertTrue(r.equals(r));
      assertTrue(r.equals(new Range(-42, 5)));

      assertFalse(r.equals(new Range(-41, 4)));
      assertFalse(r.equals(new Range(-41, 5)));
      assertFalse(r.equals(new Range(-41, 6)));
      assertFalse(r.equals(new Range(-42, 4)));
      assertFalse(r.equals(new Range(-42, 6)));
      assertFalse(r.equals(new Range(-43, 4)));
      assertFalse(r.equals(new Range(-43, 5)));
      assertFalse(r.equals(new Range(-43, 6)));
      assertFalse(r.equals(new Range(5, 42)));
      assertFalse(r.equals(new Range(-5, 42)));
      assertFalse(r.equals(new Range(Long.MIN_VALUE, Long.MAX_VALUE)));
      assertFalse(r.equals(new Object()));
   }

   @Test
   public void testHashCode() {
      assertEquals(new Range(-42, 5), new Range(-42, 5));
      assertEquals(new Range(5, 42), new Range(5, 42));
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testMinGreaterThanMax() {
      new Range(2, 1);
   }
}

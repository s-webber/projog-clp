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

import org.testng.annotations.Test;

public class RangeParserTest {
   @Test
   public void testParseLong() {
      assertEquals(RangeParser.parseLong("0"), 0);
      assertEquals(RangeParser.parseLong("1"), 1);
      assertEquals(RangeParser.parseLong("-1"), -1);
      assertEquals(RangeParser.parseLong("MAX"), Long.MAX_VALUE);
      assertEquals(RangeParser.parseLong("MIN"), Long.MIN_VALUE);
   }

   @Test
   public void testParseRange() {
      Range r = RangeParser.parseRange("-42:180");
      assertEquals(r.min(), -42);
      assertEquals(r.max(), 180);
   }

   @Test
   public void testParseRangeSingleValue() {
      Range r = RangeParser.parseRange("7");
      assertEquals(r.min(), 7);
      assertEquals(r.max(), 7);
   }

   @Test
   public void testParseMaxRange() {
      Range r = RangeParser.parseRange("MIN:MAX");
      assertEquals(r.min(), Long.MIN_VALUE);
      assertEquals(r.max(), Long.MAX_VALUE);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testParseRangeMinGreaterThanMax() {
      RangeParser.parseRange("9:8");
   }
}

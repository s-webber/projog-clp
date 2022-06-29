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

public class SubtractTest extends AbstractExpressionTest {
   public SubtractTest() {
      super(Subtract::new, false);

      when("0", "0").then("0"); // 0/0
      when("0", "42").then("-42"); // 0/+
      when("42", "0").then("42"); // +/0
      when("0", "-42").then("42"); // 0/-
      when("-42", "0").then("-42"); // -/0
      when("5", "42").then("-37"); // +/+
      when("42", "5").then("37"); // +/+
      when("-5", "-42").then("37"); // -/-
      when("-42", "-5").then("-37"); // -/-
      when("5", "-42").then("47"); // +/-
      when("-42", "5").then("-47"); // -/+
      // combinations of MIN/MAX
      when("MIN", "MIN").then("0");
      when("MAX", "MAX").then("0");
      when("MIN", "MAX").then("MIN");
      when("MAX", "MIN").then("MAX");
      // MIN
      when("MIN", "3").then("MIN");
      when("MIN", "2").then("MIN");
      when("MIN", "1").then("MIN");
      when("MIN", "0").then("MIN");
      when("MIN", "-1").then("MIN+1");
      when("MIN", "-2").then("MIN+2");
      when("MIN", "-3").then("MIN+3");
      // MIN+1
      when("MIN+1", "3").then("MIN");
      when("MIN+1", "2").then("MIN");
      when("MIN+1", "1").then("MIN");
      when("MIN+1", "0").then("MIN+1");
      when("MIN+1", "-1").then("MIN+2");
      when("MIN+1", "-2").then("MIN+3");
      when("MIN+1", "-3").then("MIN+4");
      // MIN+2
      when("MIN+2", "3").then("MIN");
      when("MIN+2", "2").then("MIN");
      when("MIN+2", "1").then("MIN+1");
      when("MIN+2", "0").then("MIN+2");
      when("MIN+2", "-1").then("MIN+3");
      when("MIN+2", "-2").then("MIN+4");
      when("MIN+2", "-3").then("MIN+5");
      // MAX
      when("MAX", "3").then("MAX-3");
      when("MAX", "2").then("MAX-2");
      when("MAX", "1").then("MAX-1");
      when("MAX", "0").then("MAX");
      when("MAX", "-1").then("MAX");
      when("MAX", "-2").then("MAX");
      when("MAX", "-3").then("MAX");
      // MAX-1
      when("MAX-1", "3").then("MAX-4");
      when("MAX-1", "2").then("MAX-3");
      when("MAX-1", "1").then("MAX-2");
      when("MAX-1", "0").then("MAX-1");
      when("MAX-1", "-1").then("MAX");
      when("MAX-1", "-2").then("MAX");
      when("MAX-1", "-3").then("MAX");
      // MAX-2
      when("MAX-2", "3").then("MAX-5");
      when("MAX-2", "2").then("MAX-4");
      when("MAX-2", "1").then("MAX-3");
      when("MAX-2", "0").then("MAX-2");
      when("MAX-2", "-1").then("MAX-1");
      when("MAX-2", "-2").then("MAX");
      when("MAX-2", "-3").then("MAX");
      // -ve/+ve
      when("1:10", "1:10").then("-9:9"); // all positive
      when("-10:-1", "-10:-1").then("-9:9"); // all negative
      when("-7:12", "-6:13").then("-20:18"); // neg:positive,neg:positive

      given("9", "0:3").setMin(5).then("9", "0:3");
      given("MIN:9", "0:3").setMin(5).then("5:9", "0:3");
      given("9", "0:3").setMin(6).then("9", "0:3");
      given("MIN:9", "0:3").setMin(6).then("6:9", "0:3");
      given("9", "0:3").setMin(7).then("9", "0:2");
      given("MIN:9", "0:3").setMin(7).then("7:9", "0:2");
      given("9", "0:3").setMin(8).then("9", "0:1");
      given("MIN:9", "0:3").setMin(8).then("8:9", "0:1");
      given("9", "0:3").setMin(9).then("9", "0:0");
      given("MIN:9", "0:3").setMin(9).then("9:9", "0:0");
      given("9", "1:3").setMin(5).then("9", "1:3");
      given("MIN:9", "1:3").setMin(5).then("6:9", "1:3");
      given("9", "1:3").setMin(6).then("9", "1:3");
      given("MIN:9", "1:3").setMin(6).then("7:9", "1:3");
      given("9", "1:3").setMin(7).then("9", "1:2");
      given("MIN:9", "1:3").setMin(7).then("8:9", "1:2");
      given("9", "1:3").setMin(8).then("9", "1:1");
      given("MIN:9", "1:3").setMin(8).then("9:9", "1:1");
      given("9", "-2:3").setMin(5).then("9", "-2:3");
      given("MIN:9", "-2:3").setMin(5).then("3:9", "-2:3");
      given("9", "-2:3").setMin(6).then("9", "-2:3");
      given("MIN:9", "-2:3").setMin(6).then("4:9", "-2:3");
      given("9", "-2:3").setMin(7).then("9", "-2:2");
      given("MIN:9", "-2:3").setMin(7).then("5:9", "-2:2");
      given("9", "-2:3").setMin(8).then("9", "-2:1");
      given("MIN:9", "-2:3").setMin(8).then("6:9", "-2:1");
      given("9", "-2:3").setMin(9).then("9", "-2:0");
      given("MIN:9", "-2:3").setMin(9).then("7:9", "-2:0");
      given("9", "-2:3").setMin(10).then("9", "-2:-1");
      given("MIN:9", "-2:3").setMin(10).then("8:9", "-2:-1");
      given("9", "-2:3").setMin(11).then("9", "-2:-2");
      given("MIN:9", "-2:3").setMin(11).then("9:9", "-2:-2");
      given("-9", "0:3").setMin(-13).then("-9", "0:3");
      given("MIN:-9", "0:3").setMin(-13).then("-13:-9", "0:3");
      given("-9", "0:3").setMin(-12).then("-9", "0:3");
      given("MIN:-9", "0:3").setMin(-12).then("-12:-9", "0:3");
      given("-9", "0:3").setMin(-11).then("-9", "0:2");
      given("MIN:-9", "0:3").setMin(-11).then("-11:-9", "0:2");
      given("-9", "0:3").setMin(-10).then("-9", "0:1");
      given("MIN:-9", "0:3").setMin(-10).then("-10:-9", "0:1");
      given("-9", "0:3").setMin(-9).then("-9", "0:0");
      given("MIN:-9", "0:3").setMin(-9).then("-9:-9", "0:0");
      given("-9", "1:3").setMin(-13).then("-9", "1:3");
      given("MIN:-9", "1:3").setMin(-13).then("-12:-9", "1:3");
      given("-9", "1:3").setMin(-12).then("-9", "1:3");
      given("MIN:-9", "1:3").setMin(-12).then("-11:-9", "1:3");
      given("-9", "1:3").setMin(-11).then("-9", "1:2");
      given("MIN:-9", "1:3").setMin(-11).then("-10:-9", "1:2");
      given("-9", "1:3").setMin(-10).then("-9", "1:1");
      given("MIN:-9", "1:3").setMin(-10).then("-9:-9", "1:1");
      given("-9", "-2:3").setMin(-13).then("-9", "-2:3");
      given("MIN:-9", "-2:3").setMin(-13).then("-15:-9", "-2:3");
      given("-9", "-2:3").setMin(-12).then("-9", "-2:3");
      given("MIN:-9", "-2:3").setMin(-12).then("-14:-9", "-2:3");
      given("-9", "-2:3").setMin(-11).then("-9", "-2:2");
      given("MIN:-9", "-2:3").setMin(-11).then("-13:-9", "-2:2");
      given("-9", "-2:3").setMin(-10).then("-9", "-2:1");
      given("MIN:-9", "-2:3").setMin(-10).then("-12:-9", "-2:1");
      given("-9", "-2:3").setMin(-9).then("-9", "-2:0");
      given("MIN:-9", "-2:3").setMin(-9).then("-11:-9", "-2:0");
      given("-9", "-2:3").setMin(-8).then("-9", "-2:-1");
      given("MIN:-9", "-2:3").setMin(-8).then("-10:-9", "-2:-1");
      given("-9", "-2:3").setMin(-7).then("-9", "-2:-2");
      given("MIN:-9", "-2:3").setMin(-7).then("-9:-9", "-2:-2");
      given("0", "0:3").setMin(-4).then("0", "0:3");
      given("MIN:0", "0:3").setMin(-4).then("-4:0", "0:3");
      given("0", "0:3").setMin(-3).then("0", "0:3");
      given("MIN:0", "0:3").setMin(-3).then("-3:0", "0:3");
      given("0", "0:3").setMin(-2).then("0", "0:2");
      given("MIN:0", "0:3").setMin(-2).then("-2:0", "0:2");
      given("0", "0:3").setMin(-1).then("0", "0:1");
      given("MIN:0", "0:3").setMin(-1).then("-1:0", "0:1");
      given("0", "0:3").setMin(0).then("0", "0:0");
      given("MIN:0", "0:3").setMin(0).then("0:0", "0:0");
      given("0", "1:3").setMin(-4).then("0", "1:3");
      given("MIN:0", "1:3").setMin(-4).then("-3:0", "1:3");
      given("0", "1:3").setMin(-3).then("0", "1:3");
      given("MIN:0", "1:3").setMin(-3).then("-2:0", "1:3");
      given("0", "1:3").setMin(-2).then("0", "1:2");
      given("MIN:0", "1:3").setMin(-2).then("-1:0", "1:2");
      given("0", "1:3").setMin(-1).then("0", "1:1");
      given("MIN:0", "1:3").setMin(-1).then("0:0", "1:1");
      given("0", "-2:3").setMin(-4).then("0", "-2:3");
      given("MIN:0", "-2:3").setMin(-4).then("-6:0", "-2:3");
      given("0", "-2:3").setMin(-3).then("0", "-2:3");
      given("MIN:0", "-2:3").setMin(-3).then("-5:0", "-2:3");
      given("0", "-2:3").setMin(-2).then("0", "-2:2");
      given("MIN:0", "-2:3").setMin(-2).then("-4:0", "-2:2");
      given("0", "-2:3").setMin(-1).then("0", "-2:1");
      given("MIN:0", "-2:3").setMin(-1).then("-3:0", "-2:1");
      given("0", "-2:3").setMin(0).then("0", "-2:0");
      given("MIN:0", "-2:3").setMin(0).then("-2:0", "-2:0");
      given("0", "-2:3").setMin(1).then("0", "-2:-1");
      given("MIN:0", "-2:3").setMin(1).then("-1:0", "-2:-1");
      given("0", "-2:3").setMin(2).then("0", "-2:-2");
      given("MIN:0", "-2:3").setMin(2).then("0:0", "-2:-2");

      given("9", "0:3").setMax(6).then("9", "3:3");
      given("9:MAX", "0:3").setMax(6).then("9:9", "3:3");
      given("9", "0:3").setMax(7).then("9", "2:3");
      given("9:MAX", "0:3").setMax(7).then("9:10", "2:3");
      given("9", "0:3").setMax(8).then("9", "1:3");
      given("9:MAX", "0:3").setMax(8).then("9:11", "1:3");
      given("9", "0:3").setMax(9).then("9", "0:3");
      given("9:MAX", "0:3").setMax(9).then("9:12", "0:3");
      given("9", "0:3").setMax(10).then("9", "0:3");
      given("9:MAX", "0:3").setMax(10).then("9:13", "0:3");
      given("9", "1:3").setMax(6).then("9", "3:3");
      given("9:MAX", "1:3").setMax(6).then("9:9", "3:3");
      given("9", "1:3").setMax(7).then("9", "2:3");
      given("9:MAX", "1:3").setMax(7).then("9:10", "2:3");
      given("9", "1:3").setMax(8).then("9", "1:3");
      given("9:MAX", "1:3").setMax(8).then("9:11", "1:3");
      given("9", "1:3").setMax(9).then("9", "1:3");
      given("9:MAX", "1:3").setMax(9).then("9:12", "1:3");
      given("9", "-2:3").setMax(6).then("9", "3:3");
      given("9:MAX", "-2:3").setMax(6).then("9:9", "3:3");
      given("9", "-2:3").setMax(7).then("9", "2:3");
      given("9:MAX", "-2:3").setMax(7).then("9:10", "2:3");
      given("9", "-2:3").setMax(8).then("9", "1:3");
      given("9:MAX", "-2:3").setMax(8).then("9:11", "1:3");
      given("9", "-2:3").setMax(9).then("9", "0:3");
      given("9:MAX", "-2:3").setMax(9).then("9:12", "0:3");
      given("9", "-2:3").setMax(10).then("9", "-1:3");
      given("9:MAX", "-2:3").setMax(10).then("9:13", "-1:3");
      given("9", "-2:3").setMax(11).then("9", "-2:3");
      given("9:MAX", "-2:3").setMax(11).then("9:14", "-2:3");
      given("9", "-2:3").setMax(12).then("9", "-2:3");
      given("9:MAX", "-2:3").setMax(12).then("9:15", "-2:3");
      given("-9", "0:3").setMax(-12).then("-9", "3:3");
      given("-9:MAX", "0:3").setMax(-12).then("-9:-9", "3:3");
      given("-9", "0:3").setMax(-11).then("-9", "2:3");
      given("-9:MAX", "0:3").setMax(-11).then("-9:-8", "2:3");
      given("-9", "0:3").setMax(-10).then("-9", "1:3");
      given("-9:MAX", "0:3").setMax(-10).then("-9:-7", "1:3");
      given("-9", "0:3").setMax(-9).then("-9", "0:3");
      given("-9:MAX", "0:3").setMax(-9).then("-9:-6", "0:3");
      given("-9", "0:3").setMax(-8).then("-9", "0:3");
      given("-9:MAX", "0:3").setMax(-8).then("-9:-5", "0:3");
      given("-9", "1:3").setMax(-12).then("-9", "3:3");
      given("-9:MAX", "1:3").setMax(-12).then("-9:-9", "3:3");
      given("-9", "1:3").setMax(-11).then("-9", "2:3");
      given("-9:MAX", "1:3").setMax(-11).then("-9:-8", "2:3");
      given("-9", "1:3").setMax(-10).then("-9", "1:3");
      given("-9:MAX", "1:3").setMax(-10).then("-9:-7", "1:3");
      given("-9", "1:3").setMax(-9).then("-9", "1:3");
      given("-9:MAX", "1:3").setMax(-9).then("-9:-6", "1:3");
      given("-9", "-2:3").setMax(-12).then("-9", "3:3");
      given("-9:MAX", "-2:3").setMax(-12).then("-9:-9", "3:3");
      given("-9", "-2:3").setMax(-11).then("-9", "2:3");
      given("-9:MAX", "-2:3").setMax(-11).then("-9:-8", "2:3");
      given("-9", "-2:3").setMax(-10).then("-9", "1:3");
      given("-9:MAX", "-2:3").setMax(-10).then("-9:-7", "1:3");
      given("-9", "-2:3").setMax(-9).then("-9", "0:3");
      given("-9:MAX", "-2:3").setMax(-9).then("-9:-6", "0:3");
      given("-9", "-2:3").setMax(-8).then("-9", "-1:3");
      given("-9:MAX", "-2:3").setMax(-8).then("-9:-5", "-1:3");
      given("-9", "-2:3").setMax(-7).then("-9", "-2:3");
      given("-9:MAX", "-2:3").setMax(-7).then("-9:-4", "-2:3");
      given("-9", "-2:3").setMax(-6).then("-9", "-2:3");
      given("-9:MAX", "-2:3").setMax(-6).then("-9:-3", "-2:3");
      given("0", "0:3").setMax(-3).then("0", "3:3");
      given("0:MAX", "0:3").setMax(-3).then("0:0", "3:3");
      given("0", "0:3").setMax(-2).then("0", "2:3");
      given("0:MAX", "0:3").setMax(-2).then("0:1", "2:3");
      given("0", "0:3").setMax(-1).then("0", "1:3");
      given("0:MAX", "0:3").setMax(-1).then("0:2", "1:3");
      given("0", "0:3").setMax(0).then("0", "0:3");
      given("0:MAX", "0:3").setMax(0).then("0:3", "0:3");
      given("0", "0:3").setMax(1).then("0", "0:3");
      given("0:MAX", "0:3").setMax(1).then("0:4", "0:3");
      given("0", "1:3").setMax(-3).then("0", "3:3");
      given("0:MAX", "1:3").setMax(-3).then("0:0", "3:3");
      given("0", "1:3").setMax(-2).then("0", "2:3");
      given("0:MAX", "1:3").setMax(-2).then("0:1", "2:3");
      given("0", "1:3").setMax(-1).then("0", "1:3");
      given("0:MAX", "1:3").setMax(-1).then("0:2", "1:3");
      given("0", "1:3").setMax(0).then("0", "1:3");
      given("0:MAX", "1:3").setMax(0).then("0:3", "1:3");
      given("0", "-2:3").setMax(-3).then("0", "3:3");
      given("0:MAX", "-2:3").setMax(-3).then("0:0", "3:3");
      given("0", "-2:3").setMax(-2).then("0", "2:3");
      given("0:MAX", "-2:3").setMax(-2).then("0:1", "2:3");
      given("0", "-2:3").setMax(-1).then("0", "1:3");
      given("0:MAX", "-2:3").setMax(-1).then("0:2", "1:3");
      given("0", "-2:3").setMax(0).then("0", "0:3");
      given("0:MAX", "-2:3").setMax(0).then("0:3", "0:3");
      given("0", "-2:3").setMax(1).then("0", "-1:3");
      given("0:MAX", "-2:3").setMax(1).then("0:4", "-1:3");
      given("0", "-2:3").setMax(2).then("0", "-2:3");
      given("0:MAX", "-2:3").setMax(2).then("0:5", "-2:3");
      given("0", "-2:3").setMax(3).then("0", "-2:3");
      given("0:MAX", "-2:3").setMax(3).then("0:6", "-2:3");

      for (String[] o : new String[][] {
                  {"0", "42"},
                  {"42", "0"},
                  {"0", "-42"},
                  {"-42", "0"},
                  {"5", "42"},
                  {"42", "5"},
                  {"5", "-42"},
                  {"-42", "5"},
                  {"1:10", "1:10"},
                  {"-10:-1", "-10:-1"},
                  {"-7:12", "-6:13"}}) {
         assertEquals(o.length, 2);
         String inputLeft = o[0];
         String inputRight = o[1];
         Range range = getMinMax(inputLeft, inputRight);

         given(inputLeft, inputRight).setMin(range.min).unchanged();
         given(inputLeft, inputRight).setMin(range.min - 1).unchanged();
         given(inputLeft, inputRight).setMin(Long.MIN_VALUE).unchanged();

         given(inputLeft, inputRight).setMax(range.max).unchanged();
         given(inputLeft, inputRight).setMax(range.max + 1).unchanged();
         given(inputLeft, inputRight).setMax(Long.MAX_VALUE).unchanged();

         given(inputLeft, inputRight).setMin(range.max + 1).failed();
         given(inputLeft, inputRight).setMin(Long.MAX_VALUE).failed();

         given(inputLeft, inputRight).setMax(range.min - 1).failed();
         given(inputLeft, inputRight).setMax(Long.MIN_VALUE).failed();
      }

      given("MAX", "-1").setMax(Long.MAX_VALUE).failed();
      given("MAX-1", "-2").setMax(Long.MAX_VALUE).failed();
      given("MAX-1", "-3").setMax(Long.MAX_VALUE).failed();
      given("MAX-2", "-3").setMax(Long.MAX_VALUE).failed();
      given("MAX-3", "-4").setMax(Long.MAX_VALUE).failed();

      given("MIN", "1").setMin(Long.MIN_VALUE).failed();
      given("MIN+1", "2").setMin(Long.MIN_VALUE).failed();
      given("MIN+1", "3").setMin(Long.MIN_VALUE).failed();
      given("MIN+2", "3").setMin(Long.MIN_VALUE).failed();
      given("MIN+3", "4").setMin(Long.MIN_VALUE).failed();

      given("10", "5:7").setNot(6).unchanged();
      given("10", "5:7").setNot(5).then("10", "6:7");
      given("10", "5:7").setNot(4).unchanged();
      given("10", "5:7").setNot(3).then("10", "5:6");
      given("10", "5:7").setNot(2).unchanged();

      given("8:10", "5").setNot(6).unchanged();
      given("8:10", "5").setNot(5).then("8:9", "5");
      given("8:10", "5").setNot(4).unchanged();
      given("8:10", "5").setNot(3).then("9:10", "5");
      given("8:10", "5").setNot(2).unchanged();

      given("5", "3").setNot(1).unchanged();
      given("5", "3").setNot(2).failed();
      given("5", "3").setNot(3).unchanged();
   }
}

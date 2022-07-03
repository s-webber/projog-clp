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
package org.projog.clp.math;

import static org.testng.Assert.assertEquals;

import org.projog.clp.test.Range;

public class MultiplyTest extends AbstractTwoArgsExpressionTest {
   public MultiplyTest() {
      super(Multiply::new, true);

      multiply("0", "0").returns("0");
      multiply("42", "0").returns("0");
      multiply("-42", "0").returns("0");
      multiply("42", "5").returns("210");
      multiply("-5", "-42").returns("210");
      multiply("5", "-42").returns("-210");
      multiply("0:5", "0:5").returns("0:25");
      multiply("-5:0", "0:5").returns("-25:0");
      multiply("-5:0", "-5:0").returns("0:25");
      multiply("0:5", "1:5").returns("0:25");
      multiply("1:5", "1:5").returns("1:25");
      multiply("-5:-1", "1:5").returns("-25:-1");
      multiply("-5:-1", "-5:-1").returns("1:25");
      multiply("-5:1", "1:5").returns("-25:5");
      multiply("-5:1", "-5:1").returns("-5:25");
      multiply("MAX", "1").returns("MAX");
      multiply("MAX", "2").returns("MAX");
      multiply("MAX", "MAX").returns("MAX");
      multiply("MAX", "-1").returns("MIN+1");
      multiply("MAX", "-2").returns("MIN");
      multiply("MIN", "1").returns("MIN");
      multiply("MIN", "-1").returns("MAX");

      given("-1:0", "1:2").setMin(0).then("0", "1:2");
      given("0:1", "1:2").setMin(0).unchanged();
      given("-1:1", "1:2").setMin(0).then("0:1", "1:2");
      given("1", "2").setMin(2).unchanged();
      given("7", "3").setMin(20).unchanged();
      given("7", "3").setMin(21).unchanged();
      given("1:7", "2:3").setMin(14).then("5:7", "2:3");
      given("1:7", "2:3").setMin(15).then("5:7", "3");
      given("1:7", "2:3").setMin(16).then("6:7", "3");
      given("1:7", "2:3").setMin(17).then("6:7", "3");
      given("1:7", "2:3").setMin(18).then("6:7", "3");
      given("1:7", "2:3").setMin(19).then("7", "3");
      given("1:7", "2:3").setMin(20).then("7", "3");
      given("1:7", "2:3").setMin(21).then("7", "3");
      given("-1", "-2").setMin(2).unchanged();
      given("-7", "-3").setMin(20).unchanged();
      given("-7", "-3").setMin(21).unchanged();
      given("-7:-1", "-3:-2").setMin(14).then("-7:-5", "-3:-2");
      given("-7:-1", "-3:-2").setMin(15).then("-7:-5", "-3");
      given("-7:-1", "-3:-2").setMin(16).then("-7:-6", "-3");
      given("-7:-1", "-3:-2").setMin(17).then("-7:-6", "-3");
      given("-7:-1", "-3:-2").setMin(18).then("-7:-6", "-3");
      given("-7:-1", "-3:-2").setMin(19).then("-7", "-3");
      given("-7:-1", "-3:-2").setMin(20).then("-7", "-3");
      given("-7:-1", "-3:-2").setMin(21).then("-7", "-3");

      given("-1:0", "-2:-1").setMax(0).then("0", "-2:-1");
      given("-1:0", "-2:1").setMax(0).unchanged();
      given("-1:1", "1:2").setMax(0).then("-1:0", "1:2");
      given("1", "2").setMax(2).unchanged();
      given("4", "6").setMax(24).unchanged();
      given("4", "6").setMax(25).unchanged();
      given("7", "15").setMax(105).unchanged();
      given("4:7", "6:15").setMax(24).then("4", "6");
      given("4:7", "6:15").setMax(25).then("4", "6");
      given("4:7", "6:15").setMax(26).then("4", "6");
      given("4:7", "6:15").setMax(27).then("4", "6");
      given("4:7", "6:15").setMax(28).then("4", "6:7");
      given("4:7", "6:15").setMax(29).then("4", "6:7");
      given("4:7", "6:15").setMax(30).then("4:5", "6:7");
      given("4:7", "6:15").setMax(31).then("4:5", "6:7");
      given("4:7", "6:15").setMax(32).then("4:5", "6:8");
      given("4:7", "6:15").setMax(105).unchanged();
      given("-1", "-2").setMax(2).unchanged();
      given("-4", "-6").setMax(24).unchanged();
      given("-4", "-6").setMax(25).unchanged();
      given("-7", "-15").setMax(105).unchanged();
      given("-7:-4", "-15:-6").setMax(24).then("-4", "-6");
      given("-7:-4", "-15:-6").setMax(25).then("-4", "-6");
      given("-7:-4", "-15:-6").setMax(26).then("-4", "-6");
      given("-7:-4", "-15:-6").setMax(27).then("-4", "-6");
      given("-7:-4", "-15:-6").setMax(28).then("-4", "-7:-6");
      given("-7:-4", "-15:-6").setMax(29).then("-4", "-7:-6");
      given("-7:-4", "-15:-6").setMax(30).then("-5:-4", "-7:-6");
      given("-7:-4", "-15:-6").setMax(31).then("-5:-4", "-7:-6");
      given("-7:-4", "-15:-6").setMax(32).then("-5:-4", "-8:-6");
      given("-7:-4", "-15:-6").setMax(105).unchanged();

      for (String[] o : new String[][] { //
                  {"0", "48"},
                  {"0", "-48"},
                  {"5", "48"},
                  {"5", "-48"},
                  {"1:10", "1:10"},
                  {"-10:-1", "-10:-1"},
                  {"-7:12", "-6:13"}}) {
         assertEquals(o.length, 2);
         String inputLeft = o[0];
         String inputRight = o[1];
         Range range = getMinMax(inputLeft, inputRight);

         given(inputLeft, inputRight).setMin(range.min()).unchanged();
         given(inputLeft, inputRight).setMin(range.min() - 1).unchanged();
         given(inputLeft, inputRight).setMin(Long.MIN_VALUE).unchanged();

         given(inputLeft, inputRight).setMax(range.max()).unchanged();
         given(inputLeft, inputRight).setMax(range.max() + 1).unchanged();
         given(inputLeft, inputRight).setMax(Long.MAX_VALUE).unchanged();

         given(inputLeft, inputRight).setMin(range.max() + 1).failed();
         given(inputLeft, inputRight).setMin(Long.MAX_VALUE).failed();

         given(inputLeft, inputRight).setMax(range.min() - 1).failed();
         given(inputLeft, inputRight).setMax(Long.MIN_VALUE).failed();
      }

      given("7", "3").setMin(22).failed();
      given("7", "3").setMin(23).failed();
      given("1:7", "2").setMin(22).failed();
      given("-1", "2").setMin(0).failed();
      given("-1", "2").setMin(1).failed();

      given("4", "6").setMax(22).failed();
      given("4", "6").setMax(23).failed();
      given("4:7", "6:15").setMax(23).failed();
      given("1", "2").setMax(0).failed();
      given("-1", "-2").setMax(1).failed();
      given("1", "2").setMax(-1).failed();
      given("-1", "-2").setMax(-1).failed();

      given("3", "5:7").setNot(14).unchanged();
      given("3", "5:7").setNot(15).then("3", "6:7");
      given("3", "5:7").setNot(16).unchanged();
      given("3", "5:7").setNot(17).unchanged();
      given("3", "5:7").setNot(18).unchanged();
      given("3", "5:7").setNot(19).unchanged();
      given("3", "5:7").setNot(20).unchanged();
      given("3", "5:7").setNot(11).unchanged();
      given("3", "5:7").setNot(21).then("3", "5:6");
      given("3", "5:7").setNot(22).unchanged();

      given("5", "3").setNot(14).unchanged();
      given("5", "3").setNot(15).failed();
      given("5", "3").setNot(16).unchanged();
   }

   private GetterTest multiply(String left, String right) {
      return expression(left, right);
   }
}

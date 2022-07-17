/*
 * Copyright 2022 a. Webber
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

public class MaximumTest extends AbstractTwoArgsExpressionTest {
   public MaximumTest() {
      super(Maximum::new, true);

      max("0", "0").returns("0"); // 0/0
      max("42", "0").returns("42"); // +/0
      max("-42", "0").returns("0"); // -/0
      max("42", "5").returns("42"); // +/+
      max("-5", "-42").returns("-5"); // -/-
      max("5", "-42").returns("5"); // +/-
      // combinations of MIN/MAX
      max("MIN", "MIN").returns("MIN");
      max("MAX", "MAX").returns("MAX");
      max("MAX", "MIN").returns("MAX");
      // -ve/+ve
      max("1:10", "1:10").returns("1:10"); // all positive
      max("-10:-1", "-10:-1").returns("-10:-1"); // all negative
      max("-10:-1", "1:10").returns("1:10"); // neg:neg:pos:pos
      max("-7:12", "-6:13").returns("-6:13"); // neg:positive,neg:positive
      max("-7:14", "-6:13").returns("-6:14"); // neg:positive,neg:positive

      given("3", "5").setMin(4).unchanged();
      given("3", "5").setMin(5).unchanged();
      given("3", "5").setMin(6).failed();

      given("3:5", "3:5").setMin(6).failed();
      given("3:5", "3:5").setMin(5).unchanged();
      given("3:5", "3:5").setMin(4).unchanged();
      given("3:5", "3:5").setMin(3).unchanged();
      given("3:5", "3:5").setMin(2).unchanged();

      given("3:5", "5:9").setMin(5).unchanged();
      given("3:5", "5:9").setMin(6).then("3:5", "6:9");
      given("3:5", "5:9").setMin(7).then("3:5", "7:9");
      given("3:5", "5:9").setMin(8).then("3:5", "8:9");
      given("3:5", "5:9").setMin(9).then("3:5", "9");
      given("3:5", "5:9").setMin(10).failed();

      given("-9:-6", "5:9").setMin(6).then("-9:-6", "6:9");
      given("-9:-6", "5:9").setMin(8).then("-9:-6", "8:9");

      given("3", "5").setMax(6).unchanged();
      given("3", "5").setMax(5).unchanged();
      given("3", "5").setMax(4).failed();

      given("3:5", "3:5").setMax(6).unchanged();
      given("3:5", "3:5").setMax(5).unchanged();
      given("3:5", "3:5").setMax(4).then("3:4", "3:4");
      given("3:5", "3:5").setMax(3).then("3", "3");
      given("3:5", "3:5").setMax(2).failed();

      given("3:5", "5:9").setMax(4).failed();
      given("3:5", "5:9").setMax(5).then("3:5", "5");
      given("3:5", "5:9").setMax(6).then("3:5", "5:6");
      given("3:5", "5:9").setMax(7).then("3:5", "5:7");
      given("3:5", "5:9").setMax(8).then("3:5", "5:8");
      given("3:5", "5:9").setMax(9).unchanged();
      given("3:5", "5:9").setMax(10).unchanged();

      given("-9:-6", "5:9").setMax(6).then("-9:-6", "5:6");
      given("-9:-6", "5:9").setMax(5).then("-9:-6", "5");

      given("3:5", "3:5").setNot(2).unchanged();
      given("3:5", "3:5").setNot(3).unchanged();
      given("3:5", "3:5").setNot(4).unchanged();
      given("3:5", "3:5").setNot(5).then("3:4", "3:4");
      given("3:5", "3:5").setNot(6).unchanged();

      given("4:5", "6:8").setNot(9).unchanged();
      given("4:5", "6:8").setNot(8).then("4:5", "6:7");
      given("4:5", "6:8").setNot(7).unchanged();
      given("4:5", "6:8").setNot(6).then("4:5", "7:8");
      given("4:5", "6:8").setNot(5).unchanged();

      given("3", "3").setNot(3).failed();
      given("3", "5").setNot(3).unchanged();
      given("3", "5").setNot(5).failed();
   }

   private GetterTest max(String left, String right) {
      return expression(left, right);
   }
}

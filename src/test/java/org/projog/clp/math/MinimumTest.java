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

public class MinimumTest extends AbstractTwoArgsExpressionTest {
   public MinimumTest() {
      super(Minimum::new, true);

      min("0", "0").returns("0"); // 0/0
      min("42", "0").returns("0"); // +/0
      min("-42", "0").returns("-42"); // -/0
      min("42", "5").returns("5"); // +/+
      min("-5", "-42").returns("-42"); // -/-
      min("5", "-42").returns("-42"); // +/-
      // combinations of MIN/MAX
      min("MIN", "MIN").returns("MIN");
      min("MAX", "MAX").returns("MAX");
      min("MAX", "MIN").returns("MIN");
      // -ve/+ve
      min("1:10", "1:10").returns("1:10"); // all positive
      min("-10:-1", "-10:-1").returns("-10:-1"); // all negative
      min("-10:-1", "1:10").returns("-10:-1"); // neg:neg:pos:pos
      min("-7:12", "-6:13").returns("-7:12"); // neg:positive,neg:positive
      min("-7:14", "-6:13").returns("-7:13"); // neg:positive,neg:positive

      given("3", "5").setMin(2).unchanged();
      given("3", "5").setMin(3).unchanged();
      given("3", "5").setMin(4).failed();

      given("3:5", "3:5").setMin(2).unchanged();
      given("3:5", "3:5").setMin(3).unchanged();
      given("3:5", "3:5").setMin(4).then("4:5", "4:5");
      given("3:5", "3:5").setMin(5).then("5", "5");
      given("3:5", "3:5").setMin(6).failed();

      given("3:5", "5:9").setMin(2).unchanged();
      given("3:5", "5:9").setMin(3).unchanged();
      given("3:5", "5:9").setMin(4).then("4:5", "5:9");
      given("3:5", "5:9").setMin(5).then("5", "5:9");
      given("3:5", "5:9").setMin(6).failed();

      given("-9:-6", "5:9").setMin(-7).then("-7:-6", "5:9");
      given("-9:-6", "5:9").setMin(-6).then("-6", "5:9");

      given("3", "5").setMax(4).unchanged();
      given("3", "5").setMax(3).unchanged();
      given("3", "5").setMax(2).failed();

      given("3:5", "3:5").setMax(6).unchanged();
      given("3:5", "3:5").setMax(5).unchanged();
      given("3:5", "3:5").setMax(4).unchanged();
      given("3:5", "3:5").setMax(3).unchanged();
      given("3:5", "3:5").setMax(2).failed();

      given("3:5", "5:9").setMax(6).unchanged();
      given("3:5", "5:9").setMax(5).unchanged();
      given("3:5", "5:9").setMax(4).then("3:4", "5:9");
      given("3:5", "5:9").setMax(3).then("3", "5:9");
      given("3:5", "5:9").setMax(2).failed();

      given("-9:-6", "5:9").setMax(-8).then("-9:-8", "5:9");
      given("-9:-6", "5:9").setMax(-9).then("-9", "5:9");

      given("3:5", "3:5").setNot(2).unchanged();
      given("3:5", "3:5").setNot(3).then("4:5", "4:5");
      given("3:5", "3:5").setNot(4).unchanged();
      given("3:5", "3:5").setNot(5).unchanged();
      given("3:5", "3:5").setNot(6).unchanged();

      given("3:5", "6:7").setNot(2).unchanged();
      given("3:5", "6:7").setNot(3).then("4:5", "6:7");
      given("3:5", "6:7").setNot(4).unchanged();
      given("3:5", "6:7").setNot(5).then("3:4", "6:7");
      given("3:5", "6:7").setNot(6).unchanged();

      given("3", "3").setNot(3).failed();
      given("3", "5").setNot(3).failed();
      given("3", "5").setNot(5).unchanged();
   }

   private GetterTest min(String left, String right) {
      return expression(left, right);
   }
}

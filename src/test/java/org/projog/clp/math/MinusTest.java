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

public class MinusTest extends AbstractSingleArgExpressionTest {
   public MinusTest() {
      super(Minus::new);

      minus("0").returns("0");
      minus("2").returns("-2");
      minus("-7").returns("7");
      minus("0:43").returns("-43:0");
      minus("-47:0").returns("0:47");
      minus("5:42").returns("-42:-5");
      minus("-42:-5").returns("5:42");
      minus("-42:5").returns("-5:42");
      minus("MIN").returns("MAX");
      minus("MAX").returns("MIN+1");
      minus("MIN:MAX").returns("MIN+1:MAX");

      given("-45:7").setMin(42).then("-45:-42");
      given("0:100").setMin(-100).unchanged();
      given("-100:0").setMin(0).unchanged();
      given("0:100").setMin(1).failed();
      given("-100:0").setMin(101).failed();

      given("-45:7").setMax(6).then("-6:7");
      given("0:100").setMax(0).unchanged();
      given("-100:0").setMax(100).unchanged();
      given("0:100").setMax(-101).failed();
      given("-100:0").setMax(-1).failed();

      given("7").setNot(-6).unchanged();
      given("7").setNot(-7).failed();
      given("7").setNot(-8).unchanged();

      given("6:8").setNot(-5).unchanged();
      given("6:8").setNot(-6).then("7:8");
      given("6:8").setNot(-7).unchanged();
      given("6:8").setNot(-8).then("6:7");
      given("6:8").setNot(-9).unchanged();
   }

   private GetterTest minus(String input) {
      return expression(input);
   }
}

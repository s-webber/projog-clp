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

public class AbsoluteTest extends AbstractSingleArgExpressionTest {
   public AbsoluteTest() {
      super(Absolute::new);

      when("0").then("0");
      when("2").then("2");
      when("-7").then("7");
      when("0:43").then("0:43");
      when("-47:0").then("0:47");
      when("5:42").then("5:42");
      when("-42:-5").then("5:42");
      when("-42:5").then("0:42");
      when("MIN").then("MAX");
      when("MAX").then("MAX");
      when("MIN:MAX").then("0:MAX");

      given("0:100").setMin(1).then("1:100");
      given("0:100").setMin(100).then("100");
      given("-100:0").setMin(1).then("-100:-1");
      given("-100:0").setMin(100).then("-100");
      given("7:100").setMin(8).then("8:100");
      given("-100:-7").setMin(8).then("-100:-8");
      given("-45:7").setMin(8).then("-45:-8");
      given("-7:45").setMin(8).then("8:45");

      given("0:100").setMin(0).unchanged();
      given("0:100").setMin(-1).unchanged();
      given("0:100").setMin(-101).unchanged();
      given("7:100").setMin(7).unchanged();
      given("7:100").setMin(6).unchanged();
      given("7:100").setMin(-1).unchanged();
      given("-100:0").setMin(0).unchanged();
      given("-100:0").setMin(-1).unchanged();
      given("-100:0").setMin(-101).unchanged();
      given("-100:-7").setMin(7).unchanged();
      given("-100:-7").setMin(6).unchanged();
      given("-100:100").setMin(7).unchanged();
      given("-100:100").setMin(0).unchanged();

      given("0:100").setMin(101).failed();
      given("-100:0").setMin(101).failed();
      given("-100:100").setMin(101).failed();

      given("0:100").setMax(0).then("0");
      given("0:100").setMax(1).then("0:1");
      given("0:100").setMax(99).then("0:99");
      given("-100:0").setMax(0).then("0");
      given("-100:0").setMax(1).then("-1:0");
      given("-100:0").setMax(99).then("-99:0");
      given("7:100").setMax(7).then("7");
      given("7:100").setMax(8).then("7:8");
      given("-100:-7").setMax(7).then("-7");
      given("-100:-7").setMax(8).then("-8:-7");
      given("-45:7").setMax(6).then("-6:6");
      given("-7:45").setMax(6).then("-6:6");
      given("-45:7").setMax(8).then("-8:7");
      given("-7:45").setMax(8).then("-7:8");

      given("0:100").setMax(100).unchanged();
      given("0:100").setMax(101).unchanged();
      given("7:100").setMax(100).unchanged();
      given("7:100").setMax(101).unchanged();
      given("-100:0").setMax(100).unchanged();
      given("-100:0").setMax(101).unchanged();
      given("-100:-7").setMax(100).unchanged();
      given("-100:-7").setMax(101).unchanged();
      given("-100:100").setMax(100).unchanged();
      given("-100:100").setMax(101).unchanged();

      given("1:100").setMax(0).failed();
      given("-100:-1").setMax(0).failed();
      given("-100:100").setMax(-101).failed();
      given("0").setMax(-1).failed();

      given("7").setNot(6).unchanged();
      given("7").setNot(7).failed();
      given("7").setNot(8).unchanged();

      given("6:8").setNot(5).unchanged();
      given("6:8").setNot(6).then("7:8");
      given("6:8").setNot(7).unchanged();
      given("6:8").setNot(8).then("6:7");
      given("6:8").setNot(9).unchanged();
   }
}

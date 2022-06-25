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

import static org.projog.clp.TestUtils.given;

import org.testng.annotations.Test;

public class NotEqualToTest extends AbstractConstraintTest {
   public NotEqualToTest() {
      super(NotEqualTo::new);
   }

   @Test(dataProvider = "process", dataProviderClass = TestDataProvider.class)
   @TestData({"1,2,1,2", "1:3,4:6,1:3,4:6", "-8:-1,1:8,-8:-1,1:8", "1,1:3,1,2:3", "3,1:3,3,1:2"})
   public void testEnforceMatched(String inputLeft, String inputRight, String outputLeft, String outputRight) {
      given(inputLeft, inputRight).when(enforce).then(ConstraintResult.MATCHED, outputLeft, outputRight);
      given(inputRight, inputLeft).when(enforce).then(ConstraintResult.MATCHED, outputRight, outputLeft);
   }

   @Test(dataProvider = "process", dataProviderClass = TestDataProvider.class)
   @TestData({"0:9,0:9", "-8:14,12:42", "2,1:3"})
   public void testEnforceUnresolved(String inputLeft, String inputRight) {
      // TODO for "2,1:3" should check that result "can only be 1 or 3 (not 2)", not just "min is 1 and max is 3".
      given(inputLeft, inputRight).when(enforce).then(ConstraintResult.UNRESOLVED, inputLeft, inputRight);
      given(inputRight, inputLeft).when(enforce).then(ConstraintResult.UNRESOLVED, inputRight, inputLeft);
   }

   @Test(dataProvider = "process", dataProviderClass = TestDataProvider.class)
   @TestData({"1,1", "-1,-1", "0,0"})
   public void testEnforceFailed(String inputLeft, String inputRight) {
      given(inputLeft, inputRight).when(enforce).then(ConstraintResult.FAILED);
      given(inputRight, inputLeft).when(enforce).then(ConstraintResult.FAILED);
   }

   @Test(dataProvider = "process", dataProviderClass = TestDataProvider.class)
   @TestData({ //
               "1,2,MATCHED",
               "1:3,4:6,MATCHED",
               "-8:-1,1:8,MATCHED",
               "1,1:3,UNRESOLVED",
               "3,1:3,UNRESOLVED",
               "0:9,0:9,UNRESOLVED",
               "-8:14,12:42,UNRESOLVED",
               "2,1:3,UNRESOLVED",
               "1,1,FAILED",
               "-1,-1,FAILED",
               "0,0,FAILED"})
   public void testReify(String inputLeft, String inputRight, ConstraintResult expected) {
      given(inputLeft, inputRight).when(reify).then(expected);
      given(inputRight, inputLeft).when(reify).then(expected);
   }

   @Test
   public void testPrevent() { // TODO add more examples
      given("7:9", "8").when(prevent).then(ConstraintResult.MATCHED, "8", "8");
      given("7", "8").when(prevent).then(ConstraintResult.FAILED);
   }
}

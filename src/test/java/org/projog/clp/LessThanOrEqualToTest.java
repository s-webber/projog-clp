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

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;

@RunWith(DataProviderRunner.class)
public class LessThanOrEqualToTest extends AbstractConstraintTest {
   public LessThanOrEqualToTest() {
      super(LessThanOrEqualTo::new);
   }

   @Test
   @DataProvider({"3,3,3,3", "3,4,3,4", "2,4,2,4", "-4,4,-4,4", "2:5,2,2,2", "2:5,3,2:3,3", "2:5,4,2:4,4", "2:5,5,2:5,5", "2,2:5,2,2:5", "3,2:5,3,3:5", "4,2:5,4,4:5", "5,2:5,5,5"})
   public void testEnforceMatched(String inputLeft, String inputRight, String expectedLeft, String expectedRight) {
      given(inputLeft, inputRight).when(enforce).then(ConstraintResult.MATCHED, expectedLeft, expectedRight);
   }

   @Test
   @DataProvider({"0:9,0:9,0:9,0:9", "0:10,0:9,0:9,0:9", "-8:14,12:42,-8:14,12:42"})
   public void testEnforceUnresolved(String inputLeft, String inputRight, String expectedLeft, String expectedRight) {
      given(inputLeft, inputRight).when(enforce).then(ConstraintResult.UNRESOLVED, expectedLeft, expectedRight);
   }

   @Test
   @DataProvider({"8,7", "9,7", "9,-7", "8:11,4:7", "8:11,4:6"})
   public void testEnforceFailed(String inputLeft, String inputRight) {
      given(inputLeft, inputRight).when(enforce).then(ConstraintResult.FAILED);
   }

   @Test
   @DataProvider({ //
               "3,3,MATCHED",
               "3,4,MATCHED",
               "2,4,MATCHED",
               "-4,4,MATCHED",
               "2:5,5,MATCHED",
               "2,2:5,MATCHED",
               "2:5,2,UNRESOLVED",
               "2:5,3,UNRESOLVED",
               "2:5,4,UNRESOLVED",
               "3,2:5,UNRESOLVED",
               "4,2:5,UNRESOLVED",
               "5,2:5,UNRESOLVED",
               "0:9,0:9,UNRESOLVED",
               "0:10,0:9,UNRESOLVED",
               "-8:14,12:42,UNRESOLVED",
               "8,7,FAILED",
               "9,7,FAILED",
               "9,-7,FAILED",
               "8:11,4:7,FAILED",
               "8:11,4:6,FAILED"})
   public void testReify(String inputLeft, String inputRight, ConstraintResult expected) {
      given(inputLeft, inputRight).when(reify).then(expected);
   }

   @Test
   public void testPrevent() { // TODO add more examples
      given("7:9", "8").when(prevent).then(ConstraintResult.MATCHED, "9", "8");
      given("8", "8").when(prevent).then(ConstraintResult.FAILED);
      given("7", "8").when(prevent).then(ConstraintResult.FAILED);
   }
}

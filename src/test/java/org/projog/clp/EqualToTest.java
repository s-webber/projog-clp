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
public class EqualToTest extends AbstractConstraintTest {
   public EqualToTest() {
      super(EqualTo::new);
   }

   @Test
   @DataProvider({"1,1,1", "8,7:9,8", "-8:14,14:42,14"})
   public void testEnforceMatched(String inputLeft, String inputRight, Long expected) {
      given(inputLeft, inputRight).when(enforce).then(ConstraintResult.MATCHED, expected.toString());
      given(inputRight, inputLeft).when(enforce).then(ConstraintResult.MATCHED, expected.toString());
   }

   @Test
   @DataProvider({"0:9,0:9,0:9", "-8:14,12:42,12:14"})
   public void testEnforceUnresolved(String inputLeft, String inputRight, String expected) {
      given(inputLeft, inputRight).when(enforce).then(ConstraintResult.UNRESOLVED, expected);
      given(inputRight, inputLeft).when(enforce).then(ConstraintResult.UNRESOLVED, expected);
   }

   @Test
   @DataProvider({"-1,0:9", "10,0:9", "-9:-1,1:9", "12:14,15:22"})
   public void testEnforceFailed(String inputLeft, String inputRight) {
      given(inputLeft, inputRight).when(enforce).then(ConstraintResult.FAILED);
      given(inputRight, inputLeft).when(enforce).then(ConstraintResult.FAILED);
   }

   @Test
   @DataProvider({ //
               "1,1,MATCHED",
               "8,7:9,UNRESOLVED",
               "-8:14,14:42,UNRESOLVED",
               "0:9,0:9,UNRESOLVED",
               "-8:14,12:42,UNRESOLVED",
               "-1,0:9,FAILED",
               "10,0:9,FAILED",
               "-9:-1,1:9,FAILED",
               "12:14,15:22,FAILED"})
   public void testReify(String inputLeft, String inputRight, ConstraintResult expected) {
      given(inputLeft, inputRight).when(reify).then(expected);
      given(inputRight, inputLeft).when(reify).then(expected);
   }

   @Test
   public void testPrevent() { // TODO add more examples
      given("7", "8").when(prevent).then(ConstraintResult.MATCHED);
      given("7", "7").when(prevent).then(ConstraintResult.FAILED);
   }
}

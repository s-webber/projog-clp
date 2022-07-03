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
package org.projog.clp.compare;

public class EqualToTest extends AbstractConstraintTest {
   public EqualToTest() {
      super(EqualTo::new, true);

      enforce("1", "1").matched("1");
      enforce("8", "7:9").matched("8");
      enforce("-8:14", "14:42").matched("14");

      enforce("0:9", "0:9").unresolved("0:9");
      enforce("-8:14", "12:42").unresolved("12:14");

      enforce("-1", "0:9").failed();
      enforce("10", "0:9").failed();
      enforce("-9:-1", "1:9").failed();
      enforce("12:14", "15:22").failed();

      prevent("7", "8").matched();
      prevent("7", "7").failed();
   }
}

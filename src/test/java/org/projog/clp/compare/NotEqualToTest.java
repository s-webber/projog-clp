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

import org.projog.clp.AbstractConstraintTest;

public class NotEqualToTest extends AbstractConstraintTest {
   public NotEqualToTest() {
      super(NotEqualTo::new, true);

      enforce("1", "2").matched();
      enforce("1:3", "4:6").matched();
      enforce("-8:-1", "1:8").matched();
      enforce("1", "1:3").matched("1", "2:3");
      enforce("3", "1:3").matched("3", "1:2");

      enforce("0:9", "0:9").unresolved();
      enforce("-8:14", "12:42").unresolved();
      // TODO for "2,1:3" should check that result "can only be 1 or 3 (not 2)", not just "min is 1 and max is 3".
      enforce("2", "1:3").unresolved();

      enforce("1", "1").failed();
      enforce("-1", "-1").failed();
      enforce("0", "0").failed();

      prevent("7:9", "8").matched("8");
      prevent("7", "8").failed();
   }
}

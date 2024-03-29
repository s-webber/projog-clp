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

public class LessThanTest extends AbstractConstraintTest {
   public LessThanTest() {
      super(LessThan::new, false);

      enforce("3", "4").matched();
      enforce("2", "4").matched();
      enforce("-4", "4").matched();
      enforce("2:5", "3").matched("2", "3");
      enforce("2:5", "4").matched("2:3", "4");
      enforce("2:5", "5").matched("2:4", "5");
      enforce("2", "2:5").matched("2", "3:5");
      enforce("3", "2:5").matched("3", "4:5");
      enforce("4", "2:5").matched("4", "5");

      enforce("0:9", "0:9").unresolved("0:8", "1:9");
      enforce("0:10", "-1:9").unresolved("0:8", "1:9");
      enforce("-8:14", "12:42").unresolved();

      enforce("7", "7").failed();
      enforce("8", "7").failed();
      enforce("9", "7").failed();
      enforce("9", "-7").failed();
      enforce("8:11", "4:7").failed();
      enforce("8:11", "4:6").failed();

      prevent("7:9", "8").matched("8:9", "8");
      prevent("8", "8").matched();
      prevent("7", "8").failed();
   }
}

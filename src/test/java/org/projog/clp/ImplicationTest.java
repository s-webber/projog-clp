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

import static org.projog.clp.ConstraintResult.FAILED;
import static org.projog.clp.ConstraintResult.MATCHED;
import static org.projog.clp.ConstraintResult.UNRESOLVED;

public class ImplicationTest extends AbstractReificationTest {
   public ImplicationTest() {
      super(Implication::new);

      given("1", "1").enforce(MATCHED).prevent(FAILED).reify(MATCHED);
      given("0", "0").enforce(MATCHED).prevent(FAILED).reify(MATCHED);
      given("1", "0").enforce(FAILED).prevent(MATCHED).reify(FAILED);
      given("0", "1").enforce(MATCHED).prevent(FAILED).reify(MATCHED);
      given("1", "0:1").enforce(MATCHED, "1").prevent(MATCHED, "1", "0").reify(UNRESOLVED);
      given("0:1", "1").enforce(MATCHED).prevent(FAILED).reify(MATCHED);
      given("0", "0:1").enforce(MATCHED).prevent(FAILED).reify(MATCHED);
      given("0:1", "0").enforce(MATCHED, "0").prevent(MATCHED, "1", "0").reify(UNRESOLVED);
      given("0:1", "0:1").enforce(UNRESOLVED).prevent(UNRESOLVED).reify(UNRESOLVED);
   }
}

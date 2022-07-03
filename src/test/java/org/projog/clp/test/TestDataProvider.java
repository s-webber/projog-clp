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
package org.projog.clp.test;

import java.lang.reflect.Method;

import org.testng.annotations.DataProvider;

public final class TestDataProvider {
   @DataProvider(name = "process")
   public static Object[][] process(Method m) {
      TestData d = m.getAnnotation(TestData.class);
      String[] input = d.value();

      Object[][] output = new Object[input.length][];

      for (int i = 0; i < input.length; i++) {
         output[i] = createRow(m, input[i]);
      }
      return output;
   }

   @SuppressWarnings({"rawtypes", "unchecked"})
   private static Object[] createRow(Method m, String csv) {
      String[] elements = csv.split(",");
      Object[] row = new Object[elements.length];
      for (int i = 0; i < elements.length; i++) {
         String element = elements[i];
         Class type = m.getParameters()[i].getType();
         if (type.isEnum()) {
            row[i] = Enum.valueOf(type, element);
         } else if (type == Long.class) {
            row[i] = Long.parseLong(element);
         } else {
            row[i] = element;
         }
      }
      return row;
   }
}

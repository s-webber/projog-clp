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

/**
 * Indicates the outcome of attempting to restrict the possible values of an expression.
 *
 * @see Expression
 */
public enum ExpressionResult {
   /**
    * The expression conforms to the given restriction.
    * <p>
    * Example a) Given X has a minimum value of 4 and a maximum value of 7, this result would be returned when
    * attempting to set the maximum value to 6.
    * <p>
    * Example b) Given X has a minimum value of 4 and a maximum value of 7, this result would be returned when
    * attempting to set the maximum value to 8.
    */
   VALID,
   /**
    * Applying the restriction would leave the expression with no possible values.
    * <p>
    * e.g. Given X has a minimum value of 4 and a maximum value of 7, this result would be returned when attempting to
    * set the maximum value to 3.
    */
   INVALID
}

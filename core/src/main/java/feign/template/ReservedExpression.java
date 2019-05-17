/*
 * Copyright 2019 OpenFeign Contributors
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

package feign.template;

/**
 * Expression that allows for characters in the Reserved to be included, without encoding.
 */
public class ReservedExpression extends SimpleExpression {

  ReservedExpression(String variableSpecification) {
    super(variableSpecification);
  }

  @Override
  protected boolean isCharacterAllowed(char character) {
    return super.isCharacterAllowed(character) || UriUtils.isReserved(character);
  }

  @Override
  protected String getPrefix() {
    return super.getPrefix();
  }
}

/*
 * Copyright 2021 HM Revenue & Customs
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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.eis

import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.UnitSpec
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.utils.Injector

class QuoteFormatterSpec extends UnitSpec with Injector {

  val quoteProtection = instanceOf[QuoteFormatter]

  "QuoteTransformer" must {

    "not transform value" when {

      def doesNotTransform(value: String) = quoteProtection.format(value) mustEqual value

      "value has no illegal characters" in {
        doesNotTransform("Value without illegal characters")
      }
      "value has double quotes in middle" in {
        doesNotTransform("""Must "retain" value""")
      }
      "value has single quotes in middle" in {
        doesNotTransform("""Must 'retain' value""")
      }
      "value has back-tick in middle" in {
        doesNotTransform("""Must `retain` value""")
      }
      "value has semi-colon in middle" in {
        doesNotTransform("""Must; retain value""")
      }
      "value ends with semi-colon" in {
        doesNotTransform("""Must retain value;""")
      }
    }

    "transform value" when {

      def doesTransform(value: String, expected: String) = quoteProtection.format(value) mustEqual expected

      "value starts with double quote" in {
        doesTransform(""""Transforms" this""", """["Transforms" this]""")
      }
      "value ends with double quote" in {
        doesTransform("""Transforms "this"""", """[Transforms "this"]""")
      }
      "value starts with single quote" in {
        doesTransform("""'Transforms' this""", """['Transforms' this]""")
      }
      "value ends with single quote" in {
        doesTransform("""Transforms 'this'""", """[Transforms 'this']""")
      }
      "value starts with back-tick" in {
        doesTransform("""`Transforms` this""", """[`Transforms` this]""")
      }
      "value ends with back-tick" in {
        doesTransform("""Transforms `this`""", """[Transforms `this`]""")
      }
      "value starts with semi-colon" in {
        doesTransform(""";Transforms this""", """[;Transforms this]""")
      }
    }

  }
}
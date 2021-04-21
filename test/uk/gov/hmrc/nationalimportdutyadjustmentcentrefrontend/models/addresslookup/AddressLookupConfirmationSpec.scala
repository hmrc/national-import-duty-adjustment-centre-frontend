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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.addresslookup

import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.UnitSpec

class AddressLookupConfirmationSpec extends UnitSpec {

  "AddressLookupConfirmation" should {
    "extract lines should work with an empty address" in {

      val addCountry = AddressLookupCountry(code = "UK", name = "United Kingdom")

      val confirmation = AddressLookupConfirmation(
        auditRef = "auditRef",
        Some("id"),
        AddressLookupAddress(List.empty[String], postcode = None, addCountry)
      )

      val el = confirmation.extractAddressLines()

      el._1 mustBe ""
      el._2 mustBe None
      el._3 mustBe None
      el._4 mustBe ""
    }
  }
}

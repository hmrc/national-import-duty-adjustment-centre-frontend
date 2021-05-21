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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create

import java.time.LocalDate

import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.{TestData, UnitSpec}

class EntryDetailsSpec extends UnitSpec with TestData {

  "EntryDetails" should {

    "strip spaces from EPU" in {
      EntryDetails("12 3", "123456Q", LocalDate.now()).entryProcessingUnit mustBe "123"
      EntryDetails(" 123 ", "123456Q", LocalDate.now()).entryProcessingUnit mustBe "123"
    }

    "strip spaces from Entry number" in {
      EntryDetails("123", "123 456Q", LocalDate.now()).entryNumber mustBe "123456Q"
      EntryDetails("123", " 123456Q ", LocalDate.now()).entryNumber mustBe "123456Q"
    }

  }

}

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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models

import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.{TestData, UnitSpec}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.Implicits.SanitizedString

class ImplicitsSpec extends UnitSpec with TestData {

  "Implicits.SanitizedString" should {

    "left-pad a string" in {
      "1234".leftPadToLength(7, 'x') mustBe "xxx1234"
    }

    "strip spaces" in {
      "12 34 56".stripSpacesAndDashes() mustBe "123456"
    }

    "strip dashes" in {
      "12-34-56".stripSpacesAndDashes() mustBe "123456"
    }

    "strip spaces and dashes" in {
      "12 - 34 - 56".stripSpacesAndDashes() mustBe "123456"
    }
  }

}

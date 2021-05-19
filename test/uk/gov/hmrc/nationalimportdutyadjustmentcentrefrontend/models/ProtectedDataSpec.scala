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

import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.{TestData, UnitSpec}

class ProtectedDataSpec extends UnitSpec with TestData {

  "ProtectedData" should {

    val data: ProtectedData =
      ProtectedData(createAnswers = Some(completeAnswers), amendAnswers = Some(completeAmendAnswers))

    "encrypt and decrypt data" in {

      val accountNumber = importerBankDetailsAnswer.accountNumber
      val caseNumber    = caseReferenceAnswer.number

      data.createAnswers.flatMap(_.bankDetails.map(_.accountNumber)) mustBe Some(accountNumber)
      data.amendAnswers.flatMap(_.caseReference).map(_.number) mustBe Some(caseNumber)

      val json: JsValue = Json.toJson(data)
      json.toString() mustNot include(accountNumber)
      json.toString() mustNot include(caseNumber)

      val decrypted = Json.fromJson[ProtectedData](json).getOrElse(ProtectedData())

      decrypted mustBe data
    }
  }
}

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

import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.amend.AmendClaim

/**
  * Create specified case in the PEGA system.
  * Based on spec "CSG_NIDAC_AutoCreateCase_API_Spec_V0.2.docx"  (NOTE: PEGA spec)
  */
case class EISAmendCaseRequest(
  AcknowledgementReference: String,
  ApplicationType: String,
  OriginatingSystem: String,
  Content: EISAmendCaseRequest.Content
)

object EISAmendCaseRequest {
  implicit val formats: Format[EISAmendCaseRequest] = Json.format[EISAmendCaseRequest]

  case class Content(CaseID: String, Description: String)

  object Content {
    implicit val formats: Format[Content] = Json.format[Content]

    def apply(amendClaim: AmendClaim): Content =
      Content(CaseID = amendClaim.caseReference.number, Description = amendClaim.furtherInformation.info)

  }

}

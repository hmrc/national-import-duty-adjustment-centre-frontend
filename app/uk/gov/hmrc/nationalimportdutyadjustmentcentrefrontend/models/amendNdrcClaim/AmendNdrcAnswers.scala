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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.amendNdrcClaim

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.Answers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.upscan.UploadedFile

final case class AmendNdrcAnswers(
  changePage: Option[String] = None,
  caseReference: Option[String] = None,
  amendCaseResponseType: Set[AmendCaseResponseType] = Set.empty,
  furtherInformation: Option[String] = None,
  uploads: Seq[UploadedFile] = Seq.empty
) extends Answers

object AmendNdrcAnswers {
  implicit val formats: OFormat[AmendNdrcAnswers] = Json.format[AmendNdrcAnswers]
}

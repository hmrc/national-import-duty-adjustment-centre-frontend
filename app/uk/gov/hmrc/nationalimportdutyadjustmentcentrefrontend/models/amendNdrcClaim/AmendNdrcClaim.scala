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

import play.api.Logger
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.exceptions.MissingAnswersException
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.upscan.UploadedFile
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages.{CaseReferencePage, FurtherInformationPage, Page}

final case class AmendNdrcClaim(
  caseReference: String,
  amendCaseResponseType: Set[AmendCaseResponseType],
  furtherInformation: String,
  uploads: Seq[UploadedFile]
)

object AmendNdrcClaim {
  implicit val formats: OFormat[AmendNdrcClaim] = Json.format[AmendNdrcClaim]
  private val logger: Logger                    = Logger(this.getClass)

  def apply(answers: AmendNdrcAnswers): AmendNdrcClaim = new AmendNdrcClaim(
    caseReference = answers.caseReference.getOrElse(missing(CaseReferencePage)),
    amendCaseResponseType = answers.amendCaseResponseType,
    uploads = answers.uploads,
    furtherInformation = answers.furtherInformation.getOrElse(missing(FurtherInformationPage))
  )

  private def missing(answerPage: Page) = {
    logger.warn(s"Missing answer - $answerPage")
    throw new MissingAnswersException(answerPage)
  }

}

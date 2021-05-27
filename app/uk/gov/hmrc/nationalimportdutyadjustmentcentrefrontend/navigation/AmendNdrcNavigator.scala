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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.navigation

import javax.inject.{Inject, Singleton}
import play.api.mvc.Call
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.amendNdrc.routes
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.Answers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.amendNdrcClaim.AmendCaseResponseType.{FurtherInformation, SupportingDocuments}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.amendNdrcClaim.AmendNdrcAnswers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages._

@Singleton
class AmendNdrcNavigator @Inject() ()
    extends Navigator[AmendNdrcAnswers] with AmendNdrcAnswerConditions with AmendNdrcHasAnsweredConditions {

  override protected val pageOrder: Seq[P] = Seq(
    P(FirstPage, routes.CaseReferenceController.onPageLoad, never, always),
    P(CaseReferencePage, routes.CaseReferenceController.onPageLoad, always, caseReferenceAnswered),
    P(AmendCaseResponseTypePage, routes.AmendCaseResponseTypeController.onPageLoad, always, responseTypeAnswered),
    P(UploadPage, routes.UploadFormController.onPageLoad, showUploadDocuments, uploadPageAnswered),
    P(
      FurtherInformationPage,
      routes.FurtherInformationController.onPageLoad,
      showFurtherInformation,
      furtherInformationAnswered
    ),
    P(CheckYourAnswersPage, routes.CheckYourAnswersController.onPageLoad, always, never),
  )

  override protected def checkYourAnswersPage: Call = routes.CheckYourAnswersController.onPageLoad

  override protected def pageFor: String => Option[Page] = (pageName: String) => pageOrder.find(_.page.toString == pageName).map(_.page)

}

protected trait AmendNdrcAnswerConditions {

  protected val always: Answers => Boolean = (_: Answers) => true

  protected val showFurtherInformation: AmendNdrcAnswers => Boolean =
    _.amendCaseResponseType.contains(FurtherInformation)

  protected val showUploadDocuments: AmendNdrcAnswers => Boolean =
    _.amendCaseResponseType.contains(SupportingDocuments)

}

protected trait AmendNdrcHasAnsweredConditions {

  protected val never: Answers => Boolean = (_: Answers) => false

  protected val caseReferenceAnswered: AmendNdrcAnswers => Boolean      = _.caseReference.nonEmpty
  protected val responseTypeAnswered: AmendNdrcAnswers => Boolean       = _.amendCaseResponseType.nonEmpty
  protected val furtherInformationAnswered: AmendNdrcAnswers => Boolean = _.furtherInformation.nonEmpty

  protected val uploadPageAnswered: AmendNdrcAnswers => Boolean = (answers: AmendNdrcAnswers) =>
    answers.amendCaseResponseType.contains(SupportingDocuments) && answers.uploads.nonEmpty

}


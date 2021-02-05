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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.ReclaimDutyType.{Customs, Other, Vat}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.UserAnswers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages._

@Singleton
class Navigator @Inject() () {

  private val normalRoutes: (Page, UserAnswers) => Call = {
    case (FirstPage, _)                      => controllers.makeclaim.routes.ClaimTypeController.onPageLoad()
    case (ClaimTypePage, _)                  => controllers.makeclaim.routes.EntryDetailsController.onPageLoad()
    case (EntryDetailsPage, _)               => controllers.makeclaim.routes.ItemNumbersController.onPageLoad()
    case (ItemNumbersPage, _)                => controllers.makeclaim.routes.ReclaimDutyTypeController.onPageLoad()
    case (ReclaimDutyTypePage, answers)      => reclaimDutyTypeNextPage(answers)
    case (CustomsDutyRepaymentPage, answers) => customsDutyRepaymentNextPage(answers)
    case (ImportVatRepaymentPage, answers)   => importVatRepaymentNextPage(answers)
    case (OtherDutyRepaymentPage, answers)   => otherDutyRepaymentNextPage(answers)
    case (UploadPage, _)                     => controllers.makeclaim.routes.ContactDetailsController.onPageLoad()
    case (ContactDetailsPage, _)             => controllers.makeclaim.routes.AddressController.onPageLoad()
    case (AddressPage, _)                    => controllers.makeclaim.routes.BankDetailsController.onPageLoad()
    case (BankDetailsPage, _)                => controllers.makeclaim.routes.CheckYourAnswersController.onPageLoad()
    case _                                   => controllers.routes.StartController.start()
  }

  private def reclaimDutyTypeNextPage(answers: UserAnswers) = answers.reclaimDutyTypes match {
    case Some(duties) if duties.contains(Customs) =>
      controllers.makeclaim.routes.DutyRepaymentController.onPageLoadCustomsDuty()
    case _ => customsDutyRepaymentNextPage(answers)
  }

  private def customsDutyRepaymentNextPage(answers: UserAnswers) = answers.reclaimDutyTypes match {
    case Some(duties) if duties.contains(Vat) =>
      controllers.makeclaim.routes.DutyRepaymentController.onPageLoadImportVat()
    case _ => importVatRepaymentNextPage(answers)
  }

  private def importVatRepaymentNextPage(answers: UserAnswers) = answers.reclaimDutyTypes match {
    case Some(duties) if duties.contains(Other) =>
      controllers.makeclaim.routes.DutyRepaymentController.onPageLoadOtherDuty()
    case _ => otherDutyRepaymentNextPage(answers)
  }

  private def otherDutyRepaymentNextPage(answers: UserAnswers) =
    controllers.makeclaim.routes.UploadFormController.onPageLoad()

  def nextPage(page: Page, userAnswers: UserAnswers): Call =
    normalRoutes(page, userAnswers)

}

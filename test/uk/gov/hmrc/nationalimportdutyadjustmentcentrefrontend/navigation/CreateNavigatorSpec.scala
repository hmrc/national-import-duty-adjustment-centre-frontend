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

import play.api.mvc.Call
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.{TestData, UnitSpec}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.makeclaim.routes
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.ReclaimDutyType.{Customs, Other, Vat}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.{
  CreateAnswers,
  ReclaimDutyType,
  RepayTo,
  RepresentationType
}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.upscan.UploadedFile
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages.Implicit._
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages._
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.utils.Injector

class CreateNavigatorSpec extends UnitSpec with Injector with TestData {

  private val navigator = instanceOf[CreateNavigator]

  private def answers(reclaim: ReclaimDutyType*): CreateAnswers =
    completeAnswers.copy(reclaimDutyTypes = Set(reclaim: _*))

  private def back(page: Page, userAnswers: CreateAnswers): Call =
    navigator.previousPage(page, userAnswers).maybeCall.getOrElse(Call("GET", "No back page"))

  "Navigator from address page" when {
    val nextPage     = navigator.nextPage(AddressPage, _)
    val previousPage = back(AddressPage, _)

    "going forward" should {
      "go to bank details page if claimant is importer" in {
        nextPage(
          answers().copy(representationType = Some(RepresentationType.Importer))
        ) mustBe routes.BankDetailsController.onPageLoad()
      }
      "go to who to repay page if claimant is representative" in {
        nextPage(
          answers().copy(representationType = Some(RepresentationType.Representative))
        ) mustBe routes.RepayToController.onPageLoad()
      }
    }
    "going back" should {
      "go to business name page" in {
        previousPage(answers()) mustBe routes.BusinessNameController.onPageLoad()
      }
    }
  }

  "Navigator from duty types page" when {
    val nextPage     = navigator.nextPage(ReclaimDutyTypePage, _)
    val previousPage = back(ReclaimDutyTypePage, _)

    "going forward" should {
      "go to customs duty page when Customs duty type selected" in {
        nextPage(answers(Customs)) mustBe routes.DutyRepaymentController.onPageLoadCustomsDuty()
        nextPage(answers(Customs, Vat)) mustBe routes.DutyRepaymentController.onPageLoadCustomsDuty()
        nextPage(answers(Customs, Other)) mustBe routes.DutyRepaymentController.onPageLoadCustomsDuty()
        nextPage(answers(Customs, Vat, Other)) mustBe routes.DutyRepaymentController.onPageLoadCustomsDuty()
      }
      "go to vat duty page when VAT duty type selected (and Customs duty type not selected)" in {
        nextPage(answers(Vat)) mustBe routes.DutyRepaymentController.onPageLoadImportVat()
        nextPage(answers(Vat, Other)) mustBe routes.DutyRepaymentController.onPageLoadImportVat()
      }
      "go to vat duty page when only OTHER duty type selected" in {
        nextPage(answers(Other)) mustBe routes.DutyRepaymentController.onPageLoadOtherDuty()
      }
    }
    "going back" should {
      "go to claim reason page" in {
        previousPage(answers()) mustBe routes.ClaimReasonController.onPageLoad()
      }
    }
  }

  "Navigator from Customs duty page" when {
    val nextPage     = navigator.nextPage(CustomsDutyRepaymentPage, _)
    val previousPage = back(CustomsDutyRepaymentPage, _)

    "going forward" should {
      "go to vat duty page when VAT duty type selected" in {
        nextPage(answers(Customs, Vat)) mustBe routes.DutyRepaymentController.onPageLoadImportVat()
        nextPage(answers(Customs, Vat, Other)) mustBe routes.DutyRepaymentController.onPageLoadImportVat()
      }
      "go to other duty page when Other duty selected and VAT duty not selected" in {
        nextPage(answers(Customs, Other)) mustBe routes.DutyRepaymentController.onPageLoadOtherDuty()
      }
      "go to upload page when neither VAT or Other duty selected" in {
        nextPage(answers(Customs)) mustBe routes.ReturnAmountSummaryController.onPageLoad()
      }
    }
    "going back" should {
      "go to duty types page" in {
        previousPage(answers(Customs)) mustBe routes.ReclaimDutyTypeController.onPageLoad()
        previousPage(answers(Customs, Vat)) mustBe routes.ReclaimDutyTypeController.onPageLoad()
        previousPage(answers(Customs, Vat, Other)) mustBe routes.ReclaimDutyTypeController.onPageLoad()
        previousPage(answers(Customs, Other)) mustBe routes.ReclaimDutyTypeController.onPageLoad()
      }
    }
  }

  "Navigator from VAT duty page" when {
    val nextPage     = navigator.nextPage(ImportVatRepaymentPage, _)
    val previousPage = back(ImportVatRepaymentPage, _)

    "going forward" should {
      "go to other duty page when Other duty type selected" in {
        nextPage(answers(Customs, Vat, Other)) mustBe routes.DutyRepaymentController.onPageLoadOtherDuty()
        nextPage(answers(Vat, Other)) mustBe routes.DutyRepaymentController.onPageLoadOtherDuty()
      }
      "go to upload page when Other duty not selected" in {
        nextPage(answers(Customs, Vat)) mustBe routes.ReturnAmountSummaryController.onPageLoad()
        nextPage(answers(Vat)) mustBe routes.ReturnAmountSummaryController.onPageLoad()
      }
    }
    "going back" should {
      "go to customs duty page when Customs duty type selected" in {
        previousPage(answers(Customs, Vat)) mustBe routes.DutyRepaymentController.onPageLoadCustomsDuty()
        previousPage(answers(Customs, Vat, Other)) mustBe routes.DutyRepaymentController.onPageLoadCustomsDuty()
      }

      "go to duty types page when Customs duty type not selected" in {
        previousPage(answers(Vat)) mustBe routes.ReclaimDutyTypeController.onPageLoad()
        previousPage(answers(Vat, Other)) mustBe routes.ReclaimDutyTypeController.onPageLoad()
      }
    }
  }

  "Navigator from Other duty page" when {
    val nextPage     = navigator.nextPage(OtherDutyRepaymentPage, _)
    val previousPage = back(OtherDutyRepaymentPage, _)

    "going forward" should {
      "go to upload page" in {
        nextPage(answers(Customs, Other)) mustBe routes.ReturnAmountSummaryController.onPageLoad()
        nextPage(answers(Vat, Other)) mustBe routes.ReturnAmountSummaryController.onPageLoad()
        nextPage(answers(Customs, Vat, Other)) mustBe routes.ReturnAmountSummaryController.onPageLoad()
        nextPage(answers(Other)) mustBe routes.ReturnAmountSummaryController.onPageLoad()
      }
    }
    "going back" should {
      "go to vat duty page when Vat duty type selected" in {
        previousPage(answers(Customs, Vat, Other)) mustBe routes.DutyRepaymentController.onPageLoadImportVat()
        previousPage(answers(Vat, Other)) mustBe routes.DutyRepaymentController.onPageLoadImportVat()
      }

      "go to customs duty page when Customs duty type selected and Vat duty type not selected" in {
        previousPage(answers(Customs, Other)) mustBe routes.DutyRepaymentController.onPageLoadCustomsDuty()
      }

      "go to duty types page when neither Customs or Vat duty type not selected" in {
        previousPage(answers(Other)) mustBe routes.ReclaimDutyTypeController.onPageLoad()
      }
    }
  }

  "Navigator from bank details page" when {
    val nextPage     = navigator.nextPage(BankDetailsPage, _)
    val previousPage = back(BankDetailsPage, _)

    "going forward" should {
      "go to enter importer's EORI number page" in {
        nextPage(answers()) mustBe routes.ImporterEoriNumberController.onPageLoad()
      }
    }
    "going back" should {
      "go to address page" in {
        previousPage(
          answers().copy(representationType = Some(RepresentationType.Importer))
        ) mustBe routes.AddressController.onPageLoad()
        previousPage(
          answers().copy(representationType = Some(RepresentationType.Representative))
        ) mustBe routes.RepayToController.onPageLoad()
      }
    }
  }

  "Navigating around file uploads" when {

    def answers(uploads: Seq[UploadedFile]): CreateAnswers =
      completeAnswers.copy(uploads = uploads)

    val nextPage     = navigator.nextPage(RequiredDocumentsPage, _)
    val previousPage = back(ContactDetailsPage, _)

    "going forward (from the page before file uploads)" when {
      "no files have been uploaded" should {
        "goto upload page" in {
          nextPage(answers(Seq.empty)) mustBe routes.UploadFormController.onPageLoad()
        }
      }
      "files have been uploaded" should {
        "goto upload page" in {
          nextPage(answers(Seq(uploadAnswer))) mustBe routes.UploadFormController.onPageLoad()
        }
      }
    }
    "going back (from the question after file uploads)" when {
      "no files have been uploaded" should {
        "goto upload page" in {
          previousPage(answers(Seq.empty)) mustBe routes.UploadFormController.onPageLoad()
        }
      }
      "files have been uploaded" should {
        "goto upload page" in {
          previousPage(answers(Seq(uploadAnswer))) mustBe routes.UploadFormController.onPageLoad()
        }
      }
    }
  }

  "Navigating to page" should {
    "go directly to named page" in {

      navigator.gotoPage(RepresentationTypePage) mustBe routes.RepresentationTypeController.onPageLoad
      navigator.gotoPage(ClaimTypePage) mustBe routes.ClaimTypeController.onPageLoad
      navigator.gotoPage(EntryDetailsPage) mustBe routes.EntryDetailsController.onPageLoad
      navigator.gotoPage(ItemNumbersPage) mustBe routes.ItemNumbersController.onPageLoad
      navigator.gotoPage(ClaimReasonPage) mustBe routes.ClaimReasonController.onPageLoad
      navigator.gotoPage(ReclaimDutyTypePage) mustBe routes.ReclaimDutyTypeController.onPageLoad
      navigator.gotoPage(UploadPage) mustBe routes.UploadFormController.onPageLoad
      navigator.gotoPage(ContactDetailsPage) mustBe routes.ContactDetailsController.onPageLoad
      navigator.gotoPage(AddressPage) mustBe routes.AddressController.onPageLoad
      navigator.gotoPage(ImporterEoriNumberPage) mustBe routes.ImporterEoriNumberController.onPageLoad
      navigator.gotoPage(ImporterContactDetailsPage) mustBe routes.ImporterDetailsController.onPageLoad
      navigator.gotoPage(RepayToPage) mustBe routes.RepayToController.onPageLoad
      navigator.gotoPage(BankDetailsPage) mustBe routes.BankDetailsController.onPageLoad
    }
  }

  "Navigating to next page when changing answers" should {

    "goto change your answers when no further answers required" in {
      val answers = completeAnswers.copy(changePage = Some(ClaimReasonPage))
      navigator.nextPage(ClaimReasonPage, answers) mustBe routes.CheckYourAnswersController.onPageLoad
    }

    "goto importer EORI page when changing from repay agent to repay importer" in {
      val answers =
        completeAnswers.copy(importerEori = None, changePage = Some(RepayToPage), repayTo = Some(RepayTo.Importer))
      navigator.nextPage(RepayToPage, answers) mustBe routes.ImporterEoriNumberController.onPageLoad
    }

    "goto repay to page when changing from Importer to Representative" in {
      val answers = importerAnswers.copy(
        changePage = Some(RepresentationTypePage),
        representationType = Some(RepresentationType.Representative)
      )
      navigator.nextPage(RepresentationTypePage, answers) mustBe routes.RepayToController.onPageLoad
    }

    "goto repayment summary page when changing duty types" in {
      val answers = completeAnswers.copy(changePage = Some(ReclaimDutyTypePage))
      navigator.nextPage(ReclaimDutyTypePage, answers) mustBe routes.ReturnAmountSummaryController.onPageLoad
    }

    "goto repayment summary page when changing VAT duty amounts" in {
      val answers = completeAnswers.copy(changePage = Some(ImportVatRepaymentPage))
      navigator.nextPage(ImportVatRepaymentPage, answers) mustBe routes.ReturnAmountSummaryController.onPageLoad
    }

    "skip repayment summary page when changing earlier question" in {
      val answers = completeAnswers.copy(changePage = Some(ClaimReasonPage))
      navigator.nextPage(ClaimReasonPage, answers) mustBe routes.CheckYourAnswersController.onPageLoad
    }
  }

  "Previous back (back link) when changing answers" should {

    "use javascript history function" in {
      val answers = completeAnswers.copy(changePage = Some(ClaimReasonPage))
      navigator.previousPage(ClaimReasonPage, answers).maybeCall.map(_.url) mustBe Some("javascript:history.back()")

    }
  }

  "Navigating to first missing answer" should {

    "goto check your answers when no further answers required" in {
      val answers = completeAnswers
      navigator.firstMissingAnswer(answers) mustBe routes.CheckYourAnswersController.onPageLoad
    }

    "find first missing answers" in {
      val answers = completeAnswers.copy(claimReason = None)
      navigator.firstMissingAnswer(answers) mustBe routes.ClaimReasonController.onPageLoad
    }
  }
}

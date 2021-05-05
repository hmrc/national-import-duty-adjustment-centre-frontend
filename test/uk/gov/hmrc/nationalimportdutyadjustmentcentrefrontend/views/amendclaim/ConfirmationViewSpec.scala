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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.amendclaim

import play.twirl.api.Html
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.{TestData, UnitViewSpec}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.amend.AmendClaimReceipt
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.amendclaim.ConfirmationView

class ConfirmationViewSpec extends UnitViewSpec with TestData {

  private val page = instanceOf[ConfirmationView]

  private val receipt    = AmendClaimReceipt(validAmendClaimResponse, completeAmendAnswers)
  private val view: Html = page(receipt)

  "ConfirmationPage" should {

    "have correct title" in {
      view.title() must startWith(messages("amend.confirmation.title"))
    }

    "have correct heading" in {
      view.getElementsByTag("h1") must containMessage("amend.confirmation.title")
    }

    "have correct reference" in {
      view.getElementsByClass("govuk-panel__body").text() must include(receipt.caseReference)
    }

    "have link to print summary" in {
      view.getElementsByClass("nidac-print-link") must containMessage("amend.confirmation.summary.print")

      view.getElementsByClass("nidac-print-link").first().attr("href") must include("window.print")
    }
  }
}

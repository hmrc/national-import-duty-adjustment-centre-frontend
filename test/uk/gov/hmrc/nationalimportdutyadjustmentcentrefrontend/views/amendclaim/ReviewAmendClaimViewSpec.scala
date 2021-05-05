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

import org.jsoup.nodes.Document
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.{TestData, UnitViewSpec}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.amend.{
  AmendClaimReceipt,
  AmendClaimResponse,
  AmendClaimResult
}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.amendclaim.ReviewAmendClaimView

import scala.util.Random

class ReviewAmendClaimViewSpec extends UnitViewSpec with TestData {

  private val page = instanceOf[ReviewAmendClaimView]

  private val claimReference = Random.alphanumeric.take(16).mkString

  private val receipt = AmendClaimReceipt(
    AmendClaimResponse("id", result = Some(AmendClaimResult(claimReference, Seq.empty))),
    completeAmendAnswers
  )

  private val view: Document = page(receipt)

  "ReviewAmendClaimView" should {

    "have correct title" in {
      view.title() must startWith(messages("amend.claim.summary.title"))
    }

    "have correct heading" in {
      view.getElementsByTag("h1") must containMessage("amend.claim.summary.title")
    }

    "not have back link" in {
      view.getElementsByClass("govuk-back-link") must beEmpty
    }
  }
}

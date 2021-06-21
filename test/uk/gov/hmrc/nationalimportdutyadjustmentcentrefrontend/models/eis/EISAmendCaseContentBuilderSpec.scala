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

import org.mockito.Mockito.verify
import org.scalatestplus.mockito.MockitoSugar
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.UnitSpec
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.amend.{
  AmendClaim,
  CaseReference,
  FurtherInformation
}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.utils.Injector

class EISAmendCaseContentBuilderSpec extends UnitSpec with Injector with MockitoSugar {

  "EISAmendCaseContentBuilder" should {

    "create Content for Representative Claim" in {

      val quoteFormatter = instanceOf[QuoteFormatter]
      val builder        = new EISAmendCaseContentBuilder(quoteFormatter)

      builder.build(amendClaim) must be(contentForAmendClaim)
    }

    "use QuoteFormatter to format the claim description" in {

      val quoteFormatter = mock[QuoteFormatter]
      val builder        = new EISAmendCaseContentBuilder(quoteFormatter)

      builder.build(amendClaim)

      verify(quoteFormatter).format(amendClaim.furtherInformation.info)
    }

  }

  val amendClaim: AmendClaim = AmendClaim(
    caseReference = CaseReference("NID12345678753"),
    hasMoreDocuments = false,
    uploads = Seq.empty,
    furtherInformation = FurtherInformation("I need more info")
  )

  val contentForAmendClaim: EISAmendCaseRequest.Content =
    EISAmendCaseRequest.Content(CaseID = "NID12345678753", Description = "I need more info")

}

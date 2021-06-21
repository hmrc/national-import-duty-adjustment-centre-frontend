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

import java.time.format.DateTimeFormatter

import javax.inject.Inject
import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.Claim
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.RepayTo.Importer
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.eis.EISCreateCaseRequest.Content
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.eis.EISCreateCaseRequest.Content.eisDateFormatter

/**
  * Create specified case in the PEGA system.
  * Based on spec for "CSG_NIDAC_AutoCreateCase_API_Spec" (NOTE: PEGA spec)
  * * see tests/pega-create-case-spec for latest implemented
  */
case class EISCreateCaseRequest(
  AcknowledgementReference: String,
  ApplicationType: String,
  OriginatingSystem: String,
  Content: EISCreateCaseRequest.Content
)

object EISCreateCaseRequest {
  implicit val formats: Format[EISCreateCaseRequest] = Json.format[EISCreateCaseRequest]

  case class Content(
    RepresentationType: String,
    ClaimType: String,
    ImporterDetails: ImporterDetails,
    AgentDetails: Option[AgentDetails],
    EntryProcessingUnit: String,
    EntryNumber: String,
    EntryDate: String,
    DutyDetails: Seq[DutyDetail],
    PayTo: String,
    PaymentDetails: Option[PaymentDetails],
    ItemNumber: String,
    ClaimReason: String,
    FirstName: String,
    LastName: String,
    SubmissionDate: String
  )

  object Content {
    implicit val formats: Format[Content] = Json.format[Content]

    val eisDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")

  }

}

class EISCreateCaseContentBuilder @Inject() (quoteFormatter: QuoteFormatter) {

  def build(claim: Claim): Content =
    Content(
      RepresentationType = claim.representationType.toString,
      ClaimType = claim.claimType.toString,
      ImporterDetails = ImporterDetails.forClaim(claim),
      AgentDetails = AgentDetails.forClaim(claim),
      EntryProcessingUnit = claim.entryDetails.entryProcessingUnit,
      EntryNumber = claim.entryDetails.entryNumber,
      EntryDate = eisDateFormatter.format(claim.entryDetails.entryDate),
      DutyDetails = claim.reclaimDutyPayments.map(entry => DutyDetail(entry._1, entry._2)).toSeq,
      PayTo = claim.importerBeingRepresentedDetails.map(_.repayTo).getOrElse(Importer).toString,
      PaymentDetails = Some(PaymentDetails(claim.bankDetails)),
      ItemNumber = claim.itemNumbers.numbers,
      ClaimReason = quoteFormatter.format(claim.claimReason.reason),
      FirstName = claim.contactDetails.firstName,
      LastName = claim.contactDetails.lastName,
      SubmissionDate = eisDateFormatter.format(claim.submissionDate)
    )

}

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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create

import java.time.LocalDate

import play.api.Logger
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.exceptions.MissingAnswersException
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.upscan.UploadedFile
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.navigation.CreatePageNames

import scala.util.Try

case class Claim(
  contactDetails: ContactDetails,
  claimantAddress: Address,
  representationType: RepresentationType,
  claimType: ClaimType,
  claimReason: ClaimReason,
  uploads: Seq[UploadedFile],
  reclaimDutyPayments: Map[ReclaimDutyType, DutyPaid],
  importerBeingRepresentedDetails: Option[ImporterBeingRepresentedDetails],
  bankDetails: BankDetails,
  entryDetails: EntryDetails,
  itemNumbers: ItemNumbers,
  submissionDate: LocalDate
) {

  def repaymentTotal: BigDecimal = reclaimDutyPayments.values.map(_.dueAmount).sum

}

object Claim {

  def apply(userAnswers: CreateAnswers): Claim = {
    if (userAnswers.uploads.isEmpty) missing(CreatePageNames.uploadSummary)
    if (userAnswers.reclaimDutyTypes.isEmpty) missing(CreatePageNames.dutyTypes)
    new Claim(
      contactDetails = userAnswers.contactDetails.getOrElse(missing(CreatePageNames.contactDetails)),
      claimantAddress = userAnswers.claimantAddress.getOrElse(missing(CreatePageNames.contactAddress)),
      representationType = userAnswers.representationType.getOrElse(missing(CreatePageNames.representationType)),
      claimType = userAnswers.claimType.getOrElse(missing(CreatePageNames.claimType)),
      claimReason = userAnswers.claimReason.getOrElse(missing(CreatePageNames.claimReason)),
      uploads = userAnswers.uploads,
      reclaimDutyPayments = userAnswers.reclaimDutyTypes.map(
        dutyType =>
          dutyType -> Try(userAnswers.reclaimDutyPayments(dutyType)).getOrElse(missing(dutyTypePage(dutyType)))
      ).toMap,
      importerBeingRepresentedDetails = importerBeingRepresentedDetails(userAnswers),
      bankDetails = userAnswers.bankDetails.getOrElse(missing(CreatePageNames.bankDetails)),
      entryDetails = userAnswers.entryDetails.getOrElse(missing(CreatePageNames.entryDetails)),
      itemNumbers = userAnswers.itemNumbers.getOrElse(missing(CreatePageNames.itemNumbers)),
      submissionDate = LocalDate.now()
    )
  }

  private def dutyTypePage(dutyType: ReclaimDutyType) = dutyType match {
    case ReclaimDutyType.Customs => CreatePageNames.dutyCustoms
    case ReclaimDutyType.Vat     => CreatePageNames.dutyVAT
    case ReclaimDutyType.Other   => CreatePageNames.dutyOther
  }

  private def importerBeingRepresentedDetails(userAnswers: CreateAnswers): Option[ImporterBeingRepresentedDetails] =
    userAnswers.representationType match {
      case Some(RepresentationType.Importer) => None
      case Some(RepresentationType.Representative) =>
        Some(
          create.ImporterBeingRepresentedDetails(
            repayTo = userAnswers.repayTo.getOrElse(missing(CreatePageNames.repayTo)),
            eoriNumber =
              if (userAnswers.repayTo.contains(RepayTo.Importer))
                Some(userAnswers.importerEori.getOrElse(missing(CreatePageNames.importerEori)))
              else None,
            contactDetails = userAnswers.importerContactDetails.getOrElse(missing(CreatePageNames.importerDetails))
          )
        )
    }

  private def missing(pageName: String) = {
    val missing = new MissingAnswersException(pageName)
    Logger(this.getClass).warn(missing.getMessage)
    throw missing
  }

}

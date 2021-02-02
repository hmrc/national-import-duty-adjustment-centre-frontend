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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base

import java.time.{LocalDate, LocalDateTime, ZonedDateTime}

import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.connectors.Reference
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.ClaimType.AntiDumping
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.ReclaimDutyType.{Customs, Vat}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.RepaymentType.Bacs
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.upscan.UpscanNotification.Quarantine
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.upscan._
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models._
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.repositories.UploadDetails

trait TestData {

  val fixedDate: LocalDate         = LocalDate.now()
  val fixedDateTime: LocalDateTime = LocalDateTime.now()

  // UserAnswers
  val emptyAnswers: UserAnswers = UserAnswers()

  val claimTypeAnswer: ClaimType = AntiDumping

  val uploadAnswer: UploadedFile =
    UploadedFile("reference", "/url", ZonedDateTime.now(), "checksum", "filename", "mime/type")

  val reclaimDutyTypesAnswer: Set[ReclaimDutyType] = Set(Customs, Vat)

  val repaymentTypeAnswer: RepaymentType = Bacs

  val bankDetailsAnswer: BankDetails = BankDetails("account name", "001100", "12345678")

  val contactDetailsAnswer: ContactDetails = ContactDetails("Jane", "Doe", "jane@example.com", "01234567890")

  val entryDetailsAnswer: EntryDetails = EntryDetails("010", "123456Q", fixedDate)

  val completeAnswers: UserAnswers = UserAnswers(
    claimType = Some(claimTypeAnswer),
    contactDetails = Some(contactDetailsAnswer),
    uploads = Some(Seq(uploadAnswer)),
    reclaimDutyTypes = Some(reclaimDutyTypesAnswer),
    repaymentType = Some(repaymentTypeAnswer),
    bankDetails = Some(bankDetailsAnswer),
    entryDetails = Some(entryDetailsAnswer)
  )

  // Upscan
  val uploadId: UploadId   = UploadId.generate
  val journeyId: JourneyId = JourneyId.generate

  val upscanInitiateResponse: UpscanInitiateResponse =
    UpscanInitiateResponse(UpscanFileReference("file-ref"), "post-target", Map("field-hidden" -> "value-hidden"))

  def uploadResult(status: UploadStatus): UploadDetails =
    UploadDetails(BSONObjectID.generate(), uploadId, journeyId, Reference("reference"), status, fixedDateTime)

  val uploadInProgress: UploadStatus = InProgress
  val uploadFailed: UploadStatus     = Failed(Quarantine, "bad file")

  val uploadFileSuccess: UploadStatus =
    UploadedFile("reference", "downloadUrl", ZonedDateTime.now(), "checksum", "fileName", "fileMimeType")

}

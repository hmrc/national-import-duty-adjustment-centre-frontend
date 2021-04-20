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

import java.time.{Instant, LocalDate}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.connectors.Reference
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.addresslookup.{
  AddressLookupAddress,
  AddressLookupConfirmation,
  AddressLookupCountry
}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.amend._
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.ClaimType.AntiDumping
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.ReclaimDutyType.{Customs, Other, Vat}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create._
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.upscan.UpscanNotification.Quarantine
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.upscan._
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.{
  EoriNumber,
  FileTransferResult,
  JourneyId,
  UploadId
}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.repositories.UploadDetails

trait TestData {

  val fixedDate: LocalDate  = LocalDate.now()
  val fixedInstant: Instant = Instant.now()

  val claimantEori = EoriNumber("GB434586395327")

  // CreateAnswers
  val emptyAnswers: CreateAnswers = CreateAnswers()

  val representationTypeAnswer: RepresentationType = RepresentationType.Representative

  val claimTypeAnswer: ClaimType = AntiDumping

  val uploadAnswer: UploadedFile =
    UploadedFile("upscanRef1", "/url", fixedInstant, "checksum", "filename", "mime/type")

  val uploadAnswer2: UploadedFile =
    UploadedFile("upscanRef2", "/url2", fixedInstant, "checksum2", "filename2", "mime/type2")

  val reclaimDutyTypesAnswer: Set[ReclaimDutyType] = Set(Customs, Vat, Other)

  val customsDutyRepaymentAnswer: DutyPaid = DutyPaid("100", "9.99")
  val importVatRepaymentAnswer: DutyPaid   = DutyPaid("100", "9.99")
  val otherDutyRepaymentAnswer: DutyPaid   = DutyPaid("100", "9.99")

  val reclaimDutyPayments = Map(
    Customs.toString -> customsDutyRepaymentAnswer,
    Vat.toString     -> importVatRepaymentAnswer,
    Other.toString   -> otherDutyRepaymentAnswer
  )

  val importerBankDetailsAnswer: BankDetails       = BankDetails("importer account name", "001100", "12345678")
  val representativeBankDetailsAnswer: BankDetails = BankDetails("representative account name", "001100", "87654321")

  val repayToAnswer = RepayTo.Importer

  val claimReasonAnswer: ClaimReason = ClaimReason("some valid reason")

  val contactDetailsAnswer: ContactDetails = ContactDetails("Jane", "Doe", "jane@example.com", Some("01234567890"))

  val businessNameAnswer: BusinessName = BusinessName("Mind API Inc");

  val addressAnswer: Address = Address("Line 1", Some("Line 2"), Some("Line 3"), "City", Some("WO0 1KE"))

  val addressLookupRetrieveId = "id123456"

  val addressLookupConfirmation = AddressLookupConfirmation(
    "auditRef",
    Some("id123456"),
    AddressLookupAddress(
      List(
        addressAnswer.addressLine1,
        addressAnswer.addressLine2.getOrElse(""),
        addressAnswer.addressLine3.getOrElse(""),
        addressAnswer.city
      ),
      addressAnswer.postCode,
      AddressLookupCountry("UK", "United Kingdom")
    )
  )



  val entryDetailsAnswer: EntryDetails = EntryDetails("010", "123456Q", fixedDate)

  val itemNumbersAnswer: ItemNumbers = ItemNumbers("1,2,3,4")

  val importerEoriNumberAnswer = EoriNumber("GB232454456746")

  val importerBusinessNameAnswer: ImporterBusinessName = ImporterBusinessName("Unused Imports UK");

  val importerContactDetailsAnswer =
    ImporterContactDetails(
      "Importer Line 1",
      Some("Importer Line 2"),
      Some("Address Line 3"),
      "Importer City",
      Some("BR0 0KL")
    )

  val importerAddressLookupConfirmation =
    AddressLookupConfirmation(
      "auditRef",
      Some("id123456"),
      AddressLookupAddress(
        List(
          importerContactDetailsAnswer.addressLine1,
          importerContactDetailsAnswer.addressLine2.getOrElse(""),
          importerContactDetailsAnswer.addressLine3.getOrElse(""),
          importerContactDetailsAnswer.city
        ),
        importerContactDetailsAnswer.postCode,
        AddressLookupCountry("UK", "United Kingdom")
      )
    )

  val completeAnswers: CreateAnswers = CreateAnswers(
    representationType = Some(representationTypeAnswer),
    claimType = Some(claimTypeAnswer),
    claimReason = Some(claimReasonAnswer),
    contactDetails = Some(contactDetailsAnswer),
    businessName = Some(businessNameAnswer),
    claimantAddress = Some(addressAnswer),
    uploads = Seq(uploadAnswer),
    uploadAnotherFile = Some(uploadAnotherFileAnswer),
    reclaimDutyTypes = reclaimDutyTypesAnswer,
    reclaimDutyPayments = reclaimDutyPayments,
    repayTo = Some(repayToAnswer),
    importerBankDetails = Some(importerBankDetailsAnswer),
    representativeBankDetails = Some(representativeBankDetailsAnswer),
    entryDetails = Some(entryDetailsAnswer),
    itemNumbers = Some(itemNumbersAnswer),
    importerEori = Some(importerEoriNumberAnswer),
    importerBusinessName = Some(importerBusinessNameAnswer),
    importerContactDetails = Some(importerContactDetailsAnswer)
  )

  val importerAnswers: CreateAnswers = CreateAnswers(
    representationType = Some(RepresentationType.Importer),
    claimType = Some(claimTypeAnswer),
    claimReason = Some(claimReasonAnswer),
    contactDetails = Some(contactDetailsAnswer),
    businessName = Some(businessNameAnswer),
    claimantAddress = Some(addressAnswer),
    uploads = Seq(uploadAnswer),
    uploadAnotherFile = Some(uploadAnotherFileAnswer),
    reclaimDutyTypes = reclaimDutyTypesAnswer,
    reclaimDutyPayments = reclaimDutyPayments,
    importerBankDetails = Some(importerBankDetailsAnswer),
    entryDetails = Some(entryDetailsAnswer),
    itemNumbers = Some(itemNumbersAnswer)
  )

  // Upscan
  val uploadId: UploadId   = UploadId.generate
  val journeyId: JourneyId = JourneyId.generate

  val upscanInitiateResponse: UpscanInitiateResponse =
    UpscanInitiateResponse(UpscanFileReference("file-ref"), "post-target", Map("field-hidden" -> "value-hidden"))

  def uploadResult(status: UploadStatus): UploadDetails =
    UploadDetails(uploadId, journeyId, Reference("reference"), status, fixedInstant)

  val uploadInProgress: UploadStatus = InProgress
  val uploadFailed: UploadStatus     = Failed(Quarantine, "bad file")

  val uploadFileSuccess: UploadStatus =
    UploadedFile("upscanRef1", "downloadUrl", Instant.now(), "checksum", "fileName", "fileMimeType")

  // AmendAnswers
  val caseReferenceAnswer      = CaseReference("NID21134557697RM8WIB13")
  val furtherInformationAnswer = FurtherInformation("I also have this to tell which is additional information")
  val hasMoreDocumentsAnswer   = true
  val uploadAnotherFileAnswer  = false

  val emptyAmendAnswers: AmendAnswers = AmendAnswers()

  val completeAmendAnswers: AmendAnswers =
    AmendAnswers(
      caseReference = Some(caseReferenceAnswer),
      furtherInformation = Some(furtherInformationAnswer),
      hasMoreDocuments = Some(hasMoreDocumentsAnswer),
      uploads = Seq(uploadAnswer, uploadAnswer2),
      uploadAnotherFile = Some(uploadAnotherFileAnswer)
    )

  val claim: Claim           = Claim(claimantEori, completeAnswers)
  val amendClaim: AmendClaim = AmendClaim(completeAmendAnswers)

  val validCreateClaimResponse: CreateClaimResponse =
    CreateClaimResponse(
      correlationId = "123456",
      error = None,
      result = Some(
        CreateClaimResult(
          caseReference = "NID21134557697RM8WIB14",
          fileTransferResults = Seq(
            new FileTransferResult("up-ref-1", true, 201, fixedInstant, None),
            new FileTransferResult("up-ref-2", true, 201, fixedInstant, None),
            new FileTransferResult("up-ref-3", true, 201, fixedInstant, None)
          )
        )
      )
    )

  val validAmendClaimResponse: AmendClaimResponse =
    AmendClaimResponse(
      correlationId = "123456",
      error = None,
      result = Some(
        AmendClaimResult(
          caseReference = "NID21134557697RM8WIB13",
          fileTransferResults = Seq(
            new FileTransferResult("up-ref-1", true, 201, fixedInstant, None),
            new FileTransferResult("up-ref-2", true, 201, fixedInstant, None)
          )
        )
      )
    )

  val createClaimAudit: CreateClaimAudit = CreateClaimAudit(
    success = true,
    Some("NID21134557697RM8WIB14"),
    contactDetailsAnswer,
    addressAnswer,
    representationTypeAnswer,
    claimTypeAnswer,
    claimReasonAnswer,
    Map("Customs" -> DutyPaid("100", "9.99"), "Vat" -> DutyPaid("100", "9.99"), "Other" -> DutyPaid("100", "9.99")),
    importerBankDetailsAnswer,
    Some(importerContactDetailsAnswer),
    Some(repayToAnswer),
    entryDetailsAnswer,
    itemNumbersAnswer,
    Seq(uploadAnswer),
    Seq(
      new FileTransferResult("up-ref-1", true, 201, fixedInstant, None),
      new FileTransferResult("up-ref-2", true, 201, fixedInstant, None),
      new FileTransferResult("up-ref-3", true, 201, fixedInstant, None)
    ),
    claimantEori.number,
    Some(importerEoriNumberAnswer)
  )

  val amendClaimAudit: AmendClaimAudit = AmendClaimAudit(
    success = true,
    claimantEori.number,
    "NID21134557697RM8WIB13",
    Seq(uploadAnswer, uploadAnswer2),
    Seq(
      new FileTransferResult("up-ref-1", true, 201, fixedInstant, None),
      new FileTransferResult("up-ref-2", true, 201, fixedInstant, None)
    ),
    furtherInformationAnswer.info
  )

}

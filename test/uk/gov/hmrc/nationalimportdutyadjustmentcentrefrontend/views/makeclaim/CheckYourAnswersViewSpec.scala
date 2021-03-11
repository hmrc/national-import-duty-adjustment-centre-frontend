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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.makeclaim

import org.jsoup.nodes.Document
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.{TestData, UnitViewSpec}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.makeclaim.routes
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.Claim
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.navigation.CreatePageNames
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.viewmodels.{DateFormatter, MessageKey}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.CheckYourAnswersView

class CheckYourAnswersViewSpec extends UnitViewSpec with TestData {

  private val page = instanceOf[CheckYourAnswersView]

  private val completeClaim                                = Claim(completeAnswers)
  private def view(claim: Claim = completeClaim): Document = page(claim, navigatorBack)

  "CheckYourAnswersPage" should {

    "have correct title" in {
      view().title() must startWith(messages("check_answers.title"))
    }

    "have correct heading" in {
      view().getElementsByTag("h1") must containMessage("check_answers.title")
    }

    "have back link" in {
      view() must haveNavigatorBackLink(navigatorBackUrl)
    }

    "have important information section" which {

      val informationSection = view().getElementById("important_information_section")

      "contains representative type" in {

        val representativeTypeRow = informationSection.getElementsByClass("representation_type_row")

        representativeTypeRow must haveSummaryKey(messages("check_answers.information.representation.type"))
        representativeTypeRow must haveSummaryValue(
          MessageKey.apply("check_answers.information.representation", representationTypeAnswer.toString)
        )

        representativeTypeRow must haveSummaryChangeLinkText(
          s"${messages("site.change")} ${messages("check_answers.information.representation.type.accessible")}"
        )

        representativeTypeRow must haveSummaryActionsHref(
          routes.CheckYourAnswersController.onChange(CreatePageNames.representationType)
        )
      }

    }

    "have claim section" which {

      val claimSection = view().getElementById("claim_section")

      "contains valid claim type" in {

        val claimTypeRow = claimSection.getElementsByClass("claim_type_row")

        claimTypeRow must haveSummaryKey(messages("check_answers.claim.claimType"))
        claimTypeRow must haveSummaryValue(MessageKey.apply("claim_type", claimTypeAnswer.toString))

        claimTypeRow must haveSummaryChangeLinkText(
          s"${messages("site.change")} ${messages("check_answers.claim.claimType.accessible")}"
        )

        claimTypeRow must haveSummaryActionsHref(routes.CheckYourAnswersController.onChange(CreatePageNames.claimType))
      }

      "contains valid entry details" in {

        val entryDetailsRow = claimSection.getElementsByClass("entry_details_row")

        entryDetailsRow must haveSummaryKey(messages("check_answers.claim.entryDetails"))
        entryDetailsRow must haveSummaryValue(
          s"${messages("check_answers.claim.entryDetails.epu", entryDetailsAnswer.entryProcessingUnit)} ${messages("check_answers.claim.entryDetails.entryNumber", entryDetailsAnswer.entryNumber)} ${messages("check_answers.claim.entryDetails.entryDate", DateFormatter.format(entryDetailsAnswer.entryDate))}"
        )

        entryDetailsRow must haveSummaryChangeLinkText(
          s"${messages("site.change")} ${messages("check_answers.claim.entryDetails.accessible")}"
        )

        entryDetailsRow must haveSummaryActionsHref(
          routes.CheckYourAnswersController.onChange(CreatePageNames.entryDetails)
        )
      }

      "contains valid item number" in {

        val itemNumberRow = claimSection.getElementsByClass("item_numbers_row")

        itemNumberRow must haveSummaryKey(messages("check_answers.claim.itemNumbers"))
        itemNumberRow must haveSummaryValue(itemNumbersAnswer.numbers)

        itemNumberRow must haveSummaryChangeLinkText(
          s"${messages("site.change")} ${messages("check_answers.claim.itemNumbers.accessible")}"
        )

        itemNumberRow must haveSummaryActionsHref(
          routes.CheckYourAnswersController.onChange(CreatePageNames.itemNumbers)
        )
      }

      "contains valid claim reason" in {

        val claimReasonRow = claimSection.getElementsByClass("claim_reason_row")

        claimReasonRow must haveSummaryKey(messages("check_answers.claim.reason"))
        claimReasonRow must haveSummaryValue(claimReasonAnswer.reason)

        claimReasonRow must haveSummaryChangeLinkText(
          s"${messages("site.change")} ${messages("check_answers.claim.reason.accessible")}"
        )

        claimReasonRow must haveSummaryActionsHref(
          routes.CheckYourAnswersController.onChange(CreatePageNames.claimReason)
        )
      }

      "contains valid reclaim duty type" in {

        val reclaimDutyTypeRow = claimSection.getElementsByClass("reclaim_duty_type_row")

        reclaimDutyTypeRow must haveSummaryKey(messages("check_answers.repayment.reclaimDutyType"))
        reclaimDutyTypeRow must haveSummaryValue(
          reclaimDutyTypesAnswer.map(
            value => MessageKey.apply("check_answers.repayment.reclaimDutyType", value.toString)
          ).mkString(", ")
        )

        reclaimDutyTypeRow must haveSummaryChangeLinkText(
          s"${messages("site.change")} ${messages("check_answers.repayment.reclaimDutyType.accessible")}"
        )

        reclaimDutyTypeRow must haveSummaryActionsHref(
          routes.CheckYourAnswersController.onChange(CreatePageNames.dutyTypes)
        )
      }

      "contains valid reclaim duty total" in {

        val reclaimDutyTypeRow = claimSection.getElementsByClass("reclaim_duty_total_row")

        reclaimDutyTypeRow must haveSummaryKey(messages("check_answers.repayment.total"))
        reclaimDutyTypeRow must haveSummaryValue(s"£${completeClaim.repaymentTotal}")
      }

      "contains valid upload" in {

        val uploadRow = claimSection.getElementsByClass("upload_row")

        uploadRow must haveSummaryKey(messages("check_answers.claim.uploaded"))
        uploadRow must haveSummaryValue(uploadAnswer.fileName)

        uploadRow must haveSummaryChangeLinkText(
          s"${messages("site.change")} ${messages("check_answers.claim.uploaded.accessible")}"
        )

        uploadRow must haveSummaryActionsHref(routes.CheckYourAnswersController.onChange(CreatePageNames.uploadSummary))
      }
    }

    "have your details section" which {

      val yourDetailsSection = view().getElementById("your_details_section")

      "contains valid contact details" in {

        val contactDetailsRow = yourDetailsSection.getElementsByClass("contact_details_row")

        contactDetailsRow must haveSummaryKey(messages("check_answers.yourDetails.contactDetails"))
        contactDetailsRow must haveSummaryValue(
          s"${contactDetailsAnswer.firstName} ${contactDetailsAnswer.lastName} ${contactDetailsAnswer.emailAddress} ${contactDetailsAnswer.telephoneNumber.get}"
        )

        contactDetailsRow must haveSummaryChangeLinkText(
          s"${messages("site.change")} ${messages("check_answers.yourDetails.contactDetails.accessible")}"
        )

        contactDetailsRow must haveSummaryActionsHref(
          routes.CheckYourAnswersController.onChange(CreatePageNames.contactDetails)
        )
      }

      "contains valid address details" in {

        val addressDetailsRow = yourDetailsSection.getElementsByClass("your_address_row")

        addressDetailsRow must haveSummaryKey(messages("check_answers.yourDetails.yourAddress"))
        addressDetailsRow must haveSummaryValue(
          s"${addressAnswer.name} ${addressAnswer.addressLine1} ${addressAnswer.addressLine2.get} ${addressAnswer.city} ${addressAnswer.postCode}"
        )

        addressDetailsRow must haveSummaryChangeLinkText(
          s"${messages("site.change")} ${messages("check_answers.yourDetails.yourAddress.accessible")}"
        )

        addressDetailsRow must haveSummaryActionsHref(
          routes.CheckYourAnswersController.onChange(CreatePageNames.contactAddress)
        )
      }

    }

    "not have importer details when claimant is importer" in {
      view(completeClaim.copy(importerBeingRepresentedDetails = None)).getElementById(
        "importer_section"
      ) must notBePresent
    }

    "have importer details section" which {
      val importerSection = view().getElementById("importer_section")

      "contains does import have Eori" in {
        val eoriRow = importerSection.getElementsByClass("importer_has_eori_row")
        eoriRow must haveSummaryKey(messages("check_answers.importer.hasEori"))
        eoriRow must haveSummaryValue(MessageKey.apply("check_answers.importer.hasEori", true.toString))
        eoriRow must haveSummaryChangeLinkText(
          s"${messages("site.change")} ${messages("check_answers.importer.hasEori.accessible")}"
        )
        eoriRow must haveSummaryActionsHref(routes.CheckYourAnswersController.onChange(CreatePageNames.importerHasEori))
      }

      "contains Eori number when importer has EORI" in {
        val eoriRow = importerSection.getElementsByClass("importer_eori_row")

        eoriRow must haveSummaryKey(messages("check_answers.importer.eori"))
        eoriRow must haveSummaryValue(importerEoriNumberAnswer.number)
        eoriRow must haveSummaryChangeLinkText(
          s"${messages("site.change")} ${messages("check_answers.importer.eori.accessible")}"
        )
        eoriRow must haveSummaryActionsHref(routes.CheckYourAnswersController.onChange(CreatePageNames.importerEori))
      }

      "does not contains Eori number when importer does not have EORI" in {
        val importerSection =
          view(Claim(completeAnswers.copy(importerHasEori = Some(false)))).getElementById("importer_section")

        val eoriRow = importerSection.getElementsByClass("importer_eori_row")
        eoriRow must beEmpty
      }

      "contains importer contact details" in {
        val importerDetailsRow = importerSection.getElementsByClass("importer_contact_details_row")

        importerDetailsRow must haveSummaryKey(messages("check_answers.importer.contactDetails"))
        importerDetailsRow must haveSummaryValue(
          Seq(
            importerContactDetailsAnswer.name,
            importerContactDetailsAnswer.addressLine1,
            importerContactDetailsAnswer.addressLine2.getOrElse(""),
            importerContactDetailsAnswer.city,
            importerContactDetailsAnswer.postCode
          ).mkString(" ")
        )
        importerDetailsRow must haveSummaryChangeLinkText(
          s"${messages("site.change")} ${messages("check_answers.importer.contactDetails.accessible")}"
        )
        importerDetailsRow must haveSummaryActionsHref(
          routes.CheckYourAnswersController.onChange(CreatePageNames.importerDetails)
        )
      }
    }

    "have payment section" which {

      val paymentSection = view().getElementById("payment_section")

      "contains who to pay" in {

        val payToRow = paymentSection.getElementsByClass("pay_to_row")

        payToRow must haveSummaryKey(messages("check_answers.payment.payTo"))
        payToRow must haveSummaryValue(MessageKey.apply("check_answers.payment.payTo", repayToAnswer.toString))

        payToRow must haveSummaryChangeLinkText(
          s"${messages("site.change")} ${messages("check_answers.payment.payTo.accessible")}"
        )

        payToRow must haveSummaryActionsHref(routes.CheckYourAnswersController.onChange(CreatePageNames.repayTo))
      }

      "have payment section" which {
        val paymentSection = view().getElementById("payment_section")

        "contains payTo" in {
          val payToRow = paymentSection.getElementsByClass("pay_to_row")
          payToRow must haveSummaryKey(messages("check_answers.payment.payTo"))
          payToRow must haveSummaryValue(MessageKey.apply("check_answers.payment.payTo", repayToAnswer.toString))
        }

        "contains account name" in {
          val accountNameRow = paymentSection.getElementsByClass("bank_details_account_name_row")
          accountNameRow must haveSummaryKey(messages("check_answers.payment.accountName"))
          accountNameRow must haveSummaryValue(bankDetailsAnswer.accountName)
        }

        "does account sort code" in {
          val accountSortCodeRow = paymentSection.getElementsByClass("bank_details_sortCode_row")
          accountSortCodeRow must haveSummaryKey(messages("check_answers.payment.sortCode"))
          accountSortCodeRow must haveSummaryValue(bankDetailsAnswer.sortCode)
        }

        "contains account number" in {
          val accountNumberRow = paymentSection.getElementsByClass("bank_details_accountNumber_row")
          accountNumberRow must haveSummaryKey(messages("check_answers.payment.accountNumber"))
          accountNumberRow must haveSummaryValue(bankDetailsAnswer.accountNumber)
        }
      }

      "have 'Submit' button" in {
        view().getElementById("submit") must includeMessage("check_answers.submit")
      }

    }
  }
}

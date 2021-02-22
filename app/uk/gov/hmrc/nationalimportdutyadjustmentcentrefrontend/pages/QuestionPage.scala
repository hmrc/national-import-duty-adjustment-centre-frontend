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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages

import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models._

trait QuestionPage[A] extends Page {
  def hasAnswer: UserAnswers => Boolean
}

case object RepresentationTypePage     extends QuestionPage[RepresentationType] {
  override def hasAnswer: UserAnswers => Boolean = _.representationType.nonEmpty
}
case object ClaimTypePage              extends QuestionPage[ClaimType] {
  override def hasAnswer: UserAnswers => Boolean = _.claimType.nonEmpty
}
case object ContactDetailsPage         extends QuestionPage[ContactDetails] {
  override def hasAnswer: UserAnswers => Boolean = _.contactDetails.nonEmpty
}
case object AddressPage                extends QuestionPage[Address] {
  override def hasAnswer: UserAnswers => Boolean = _.claimantAddress.nonEmpty
}
case object ImporterHasEoriNumberPage  extends QuestionPage[Boolean] {
  override def hasAnswer: UserAnswers => Boolean= _.importerHasEori.nonEmpty
}
case object ImporterEoriNumberPage     extends QuestionPage[EoriNumber] {
  override def hasAnswer: UserAnswers => Boolean = _.importerEori.nonEmpty
}
case object ImporterContactDetailsPage extends QuestionPage[ImporterContactDetails] {
  override def hasAnswer: UserAnswers => Boolean = _.importerContactDetails.nonEmpty
}
case object ReclaimDutyTypePage        extends QuestionPage[Set[ReclaimDutyType]] {
  override def hasAnswer: UserAnswers => Boolean = _.reclaimDutyTypes.nonEmpty
}
case object CustomsDutyRepaymentPage   extends QuestionPage[DutyPaid] {
  override def hasAnswer: UserAnswers => Boolean = _.reclaimDutyPayments.contains(ReclaimDutyType.Customs.toString)
}
case object ImportVatRepaymentPage     extends QuestionPage[DutyPaid] {
  override def hasAnswer: UserAnswers => Boolean = _.reclaimDutyPayments.contains(ReclaimDutyType.Vat.toString)
}
case object OtherDutyRepaymentPage     extends QuestionPage[DutyPaid] {
  override def hasAnswer: UserAnswers => Boolean = _.reclaimDutyPayments.contains(ReclaimDutyType.Other.toString)
}
case object RepayToPage                extends QuestionPage[RepayTo] {
  override def hasAnswer: UserAnswers => Boolean = _.repayTo.nonEmpty
}
case object BankDetailsPage            extends QuestionPage[BankDetails] {
  override def hasAnswer: UserAnswers => Boolean = _.bankDetails.nonEmpty
}
case object ClaimReasonPage            extends QuestionPage[ClaimReason] {
  override def hasAnswer: UserAnswers => Boolean = _.claimReason.nonEmpty
}
case object UploadSummaryPage          extends QuestionPage[Boolean] {
  override def hasAnswer: UserAnswers => Boolean = _.uploads.nonEmpty
}
case object EntryDetailsPage           extends QuestionPage[EntryDetails] {
  override def hasAnswer: UserAnswers => Boolean = _.entryDetails.nonEmpty
}
case object ItemNumbersPage            extends QuestionPage[ItemNumbers] {
  override def hasAnswer: UserAnswers => Boolean = _.itemNumbers.nonEmpty
}

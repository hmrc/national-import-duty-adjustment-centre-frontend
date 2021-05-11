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

import play.api.libs.json.{Json, OFormat}

case class DutyPaid(actuallyPaid: String, shouldHavePaid: String) {
  val dueAmount: BigDecimal = BigDecimal.apply(actuallyPaid) - BigDecimal.apply(shouldHavePaid)
}

object DutyPaid {
  private val decimalPlaces              = 2
  implicit val format: OFormat[DutyPaid] = Json.format[DutyPaid]

  private def setScale(amount: String) = BigDecimal.apply(amount).setScale(decimalPlaces).toString()

  def apply(actuallyPaid: String, shouldHavePaid: String): DutyPaid =
    new DutyPaid(setScale(actuallyPaid), setScale(shouldHavePaid))

}

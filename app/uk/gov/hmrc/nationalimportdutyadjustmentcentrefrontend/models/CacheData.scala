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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models

import java.time.Instant

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.amend.{AmendAnswers, AmendClaimReceipt}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.{CreateAnswers, CreateClaimReceipt}

final case class CacheData(
  id: String,
  journeyId: JourneyId = JourneyId.generate,
  createAnswers: Option[CreateAnswers] = None,
  amendAnswers: Option[AmendAnswers] = None,
  createClaimReceipt: Option[CreateClaimReceipt] = None,
  amendClaimReceipt: Option[AmendClaimReceipt] = None,
  lastUpdated: Instant = Instant.now()
) {

  def getCreateAnswers: CreateAnswers = createAnswers.getOrElse(CreateAnswers())
  def getAmendAnswers: AmendAnswers   = amendAnswers.getOrElse(AmendAnswers())
}

object CacheData {

  implicit val formatInstant               = MongoJavatimeFormats.instantFormat
  implicit val formats: OFormat[CacheData] = Json.format[CacheData]

}

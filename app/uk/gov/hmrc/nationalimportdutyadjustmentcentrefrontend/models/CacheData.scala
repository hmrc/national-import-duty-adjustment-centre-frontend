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

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.amend.{AmendAnswers, AmendClaimResponse}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.{CreateAnswers, CreateClaimResponse}

import java.time.LocalDateTime

final case class CacheData(
  id: String,
  journeyId: JourneyId = JourneyId.generate,
  createAnswers: Option[CreateAnswers] = None,
  amendAnswers: Option[AmendAnswers] = None,
  createClaimResponse: Option[CreateClaimResponse] = None,
  amendClaimResponse: Option[AmendClaimResponse] = None,
  lastUpdated: LocalDateTime = LocalDateTime.now
) {

  def claimReference: Option[String] = createClaimResponse.flatMap(_.result).map(_.caseReference)
  def amendReference: Option[String] = amendClaimResponse.flatMap(_.result).map(_.caseReference)
  def getCreateAnswers               = createAnswers.getOrElse(CreateAnswers())
  def getAmendAnswers                = amendAnswers.getOrElse(AmendAnswers())
}

object CacheData {

  implicit private val formatLastUpdated: OFormat[LocalDateTime] = JsonFormats.formatLocalDateTime

  implicit val formats: OFormat[CacheData] = Json.format[CacheData]
}

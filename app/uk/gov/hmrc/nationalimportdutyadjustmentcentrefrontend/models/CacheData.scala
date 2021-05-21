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

import play.api.libs.json._
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.config.CryptoFactory
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.amend.{AmendAnswers, AmendClaimReceipt}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.{CreateAnswers, CreateClaimReceipt}

final case class CacheData(
  id: String,
  journeyId: JourneyId = JourneyId.generate,
  data: ProtectedData = ProtectedData(),
  lastUpdated: Instant = Instant.now()
) {
  val createAnswers: CreateAnswers = data.createAnswers.getOrElse(CreateAnswers())
  val amendAnswers: AmendAnswers   = data.amendAnswers.getOrElse(AmendAnswers())

  val maybeCreateClaimReceipt: Option[CreateClaimReceipt] = data.createClaimReceipt
  val maybeAmendClaimReceipt: Option[AmendClaimReceipt]   = data.amendClaimReceipt

  def update(answers: CreateAnswers): CacheData = this.copy(data = data.copy(createAnswers = Some(answers)))
  def update(answers: AmendAnswers): CacheData  = this.copy(data = data.copy(amendAnswers = Some(answers)))

  def store(receipt: CreateClaimReceipt): CacheData =
    this.copy(data = data.copy(createClaimReceipt = Some(receipt), createAnswers = None))

  def store(receipt: AmendClaimReceipt): CacheData =
    this.copy(data = data.copy(amendClaimReceipt = Some(receipt), amendAnswers = None))

}

object CacheData {

  implicit val formatInstant               = MongoJavatimeFormats.instantFormat
  implicit val formats: OFormat[CacheData] = Json.format[CacheData]

  def apply(id: String, createAnswers: CreateAnswers): CacheData =
    new CacheData(id, data = ProtectedData(createAnswers = Some(createAnswers)))

  def apply(id: String, amendAnswers: AmendAnswers): CacheData =
    new CacheData(id, data = ProtectedData(amendAnswers = Some(amendAnswers)))

  def apply(id: String, createClaimReceipt: CreateClaimReceipt): CacheData =
    new CacheData(id, data = ProtectedData(createClaimReceipt = Some(createClaimReceipt)))

  def apply(id: String, amendClaimReceipt: AmendClaimReceipt): CacheData =
    new CacheData(id, data = ProtectedData(amendClaimReceipt = Some(amendClaimReceipt)))

}

case class ProtectedData(
  createAnswers: Option[CreateAnswers] = None,
  amendAnswers: Option[AmendAnswers] = None,
  createClaimReceipt: Option[CreateClaimReceipt] = None,
  amendClaimReceipt: Option[AmendClaimReceipt] = None
)

object ProtectedData {
  private implicit val formats: OFormat[ProtectedData] = Json.format[ProtectedData]

  private val crypto = CryptoFactory.instance

  implicit val reads: Reads[ProtectedData] = Reads {
    json: JsValue => Json.fromJson(crypto.decrypt(json))
  }

  implicit val writes: Writes[ProtectedData] = Writes {
    data => crypto.encrypt(Json.toJson(data))
  }

}

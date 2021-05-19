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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.config

import com.typesafe.config.ConfigFactory
import play.api.libs.json._
import uk.gov.hmrc.crypto.{ApplicationCrypto, Crypted, PlainText}

object CryptoProvider {

  private val enabledKey = "json.encryption.enabled"
  private val config     = ConfigFactory.load()
  private val crypto     = new ApplicationCrypto(config).JsonCrypto

  private val enabled = if (config.hasPath(enabledKey)) config.getBoolean(enabledKey) else true

  def decrypt(json: JsValue): JsValue = if (enabled)
    Json.parse(crypto.decrypt(Crypted(json.toString())).value)
  else
    json

  def encrypt(json: JsValue): JsValue = if (enabled)
    JsString(crypto.encrypt(PlainText(json.toString())).value)
  else
    json

}

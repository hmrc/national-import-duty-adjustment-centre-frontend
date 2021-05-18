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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.upscan

import java.time.Instant

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.upscan.UpscanNotification.FailureReason

sealed trait UploadStatus

case class InProgress() extends UploadStatus

case class Failed(failureReason: FailureReason, message: String) extends UploadStatus {
  def errorCode: String = failureReason.toString
}

case class UploadedFile(
  upscanReference: String,
  downloadUrl: String,
  uploadTimestamp: Instant,
  checksum: String,
  fileName: String,
  fileMimeType: String
) extends UploadStatus

object UploadedFile {
  implicit val formats: OFormat[UploadedFile] = Json.format[UploadedFile]
}

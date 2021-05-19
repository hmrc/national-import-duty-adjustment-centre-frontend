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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers

import play.api.data.FormError
import play.api.mvc.Call
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.config.AppConfig
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.connectors.{Reference, UpscanInitiateConnector}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.upscan.UpscanInitiateResponse
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.{JourneyId, UploadId}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.services.UploadProgressTracker

import scala.concurrent.{ExecutionContext, Future}

trait FileUploading {

  protected val ERROR_DUPLICATE      = "DUPLICATE"
  protected val UNKNOWN              = "UNKNOWN"
  protected val TIMEOUT              = "TIMEOUT"
  protected val ERROR_MISSING_FILE   = "InvalidArgument"
  protected val ERROR_FILE_TOO_SMALL = "EntityTooSmall"

  private val missingFileCodes = Set(ERROR_MISSING_FILE, ERROR_FILE_TOO_SMALL)

  val upscanInitiateConnector: UpscanInitiateConnector
  val uploadProgressTracker: UploadProgressTracker
  val appConfig: AppConfig

  protected def successRedirectUrl(uploadId: UploadId): Call
  protected def errorRedirectUrl(errorCode: String): Call

  private val errorQuery        = "?errorCode="
  private lazy val errorBaseUrl = errorRedirectUrl("").url.dropRight(errorQuery.length)

  protected def initiateForm(
    journeyId: JourneyId
  )(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[UpscanInitiateResponse] = {
    val uploadId = UploadId.generate
    for {
      upscanInitiateResponse <- upscanInitiateConnector.initiateV2(
        journeyId,
        Some(appConfig.selfUrl(successRedirectUrl(uploadId).url)),
        Some(appConfig.selfUrl(errorBaseUrl))
      )
      _ <- uploadProgressTracker.requestUpload(
        uploadId,
        journeyId,
        Reference(upscanInitiateResponse.fileReference.reference)
      )
    } yield upscanInitiateResponse
  }

  protected def isFileMissingError(code: String): Boolean = missingFileCodes.contains(code)

  protected def mapError(code: String): FormError = {
    def error(message: String) = FormError("upload-file", message)
    code match {
      case "400" | ERROR_MISSING_FILE => error("error.file-upload.required")
      case "InternalError" | TIMEOUT  => error("error.file-upload.try-again")
      case "EntityTooLarge"           => error("error.file-upload.invalid-size-large")
      case ERROR_FILE_TOO_SMALL       => error("error.file-upload.invalid-size-small")
      case "QUARANTINE"               => error("error.file-upload.quarantine")
      case "REJECTED"                 => error("error.file-upload.invalid-type")
      case ERROR_DUPLICATE            => error("error.file-upload.duplicate")
      case _                          => error("error.file-upload.unknown")
    }
  }

}

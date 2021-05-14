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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.amend

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.http.UpstreamErrorResponse
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.ApiError

import java.time.Instant
import scala.util.{Failure, Success, Try}

case class AmendClaimResponse(
  correlationId: String,
  processingDate: Option[Instant] = None,
  error: Option[ApiError] = None,
  result: Option[AmendClaimResult] = None
)

object AmendClaimResponse {
  implicit val format: OFormat[AmendClaimResponse] = Json.format[AmendClaimResponse]

  final def shouldRetry(response: Try[AmendClaimResponse]): Boolean =
    response match {
      case Success(result) if result.error.flatMap(error => error.errorMessage).contains("Busy") =>
        println("Succesfully informed downstream was busy")
        true
      case Failure(e) if e.asInstanceOf[UpstreamErrorResponse].statusCode == 429 =>
        println("Quota reached")
        true
      case Failure(e) if e.asInstanceOf[UpstreamErrorResponse].statusCode == 503 =>
        println("Server was busy")
        true
    }

  final def errorMessage(response: AmendClaimResponse): String =
    s"${response.error.map(_.errorCode).getOrElse("")} ${response.error.flatMap(_.errorMessage).getOrElse("")}"

}

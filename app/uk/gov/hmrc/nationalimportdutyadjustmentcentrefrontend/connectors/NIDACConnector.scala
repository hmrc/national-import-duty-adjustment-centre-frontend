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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.connectors

import akka.actor.ActorSystem

import javax.inject.Inject
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpException}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.config.AppConfig
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.ApiError
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.amend.AmendClaimResponse
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.CreateClaimResponse
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.services.requests.{AmendEISClaimRequest, CreateEISClaimRequest}

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ExecutionContext, Future}

class NIDACConnector @Inject() (httpClient: HttpClient, appConfig: AppConfig, val actorSystem: ActorSystem)(implicit ec: ExecutionContext) extends Retry {

  private val baseUrl = appConfig.nidacServiceBaseUrl

  def submitClaim(request: CreateEISClaimRequest, correlationId: String)(implicit hc: HeaderCarrier): Future[CreateClaimResponse] =

    retry(FiniteDuration(5, TimeUnit.SECONDS), FiniteDuration(10, TimeUnit.SECONDS))(CreateClaimResponse.shouldRetry, CreateClaimResponse.errorMessage) {
    httpClient.POST[CreateEISClaimRequest, CreateClaimResponse](
      s"$baseUrl/create-claim",
      request,
      Seq("X-Correlation-Id" -> correlationId)
    ) recover {
      case httpException: HttpException =>
        failResponse(correlationId, httpException.responseCode, httpException.message)
    }
  }

  def amendClaim(request: AmendEISClaimRequest, correlationId: String)(implicit hc: HeaderCarrier): Future[AmendClaimResponse] =
    retry(FiniteDuration(5, TimeUnit.SECONDS), FiniteDuration(10, TimeUnit.SECONDS))(AmendClaimResponse.shouldRetry, AmendClaimResponse.errorMessage) {
      httpClient.POST[AmendEISClaimRequest, AmendClaimResponse](
        s"$baseUrl/update-claim",
        request,
        Seq("X-Correlation-Id" -> correlationId)
      )
  }

  private def failResponse(correlationId: String, errorCode: Int, errorMessage: String) = CreateClaimResponse(
    correlationId = correlationId,
    error = Some(new ApiError(errorCode.toString, Some(errorMessage))),
    result = None
  )

}

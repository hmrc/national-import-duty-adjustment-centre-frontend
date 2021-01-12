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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.actions

import javax.inject.Inject
import play.api.mvc.ActionTransformer
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.UserAnswers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.requests.{DataRequest, IdentifierRequest}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.repositories.SessionRepository
import uk.gov.hmrc.play.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

class DataRetrievalActionImpl @Inject() (val sessionRepository: SessionRepository)(implicit
  val executionContext: ExecutionContext
) extends DataRetrievalAction {

  override protected def transform[A](request: IdentifierRequest[A]): Future[DataRequest[A]] = {

    implicit val hc: HeaderCarrier =
      HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))

    sessionRepository.get(request.identifier).map {
      case None =>
        DataRequest(request.request, request.identifier, UserAnswers(request.identifier))
      case Some(userAnswers) =>
        DataRequest(request.request, request.identifier, userAnswers)
    }
  }

}

trait DataRetrievalAction extends ActionTransformer[IdentifierRequest, DataRequest]

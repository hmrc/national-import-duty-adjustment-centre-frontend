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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.services

import javax.inject.Inject
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.requests.IdentifierRequest
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.{CacheData, UserAnswers}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.repositories.CacheDataRepository

import scala.concurrent.{ExecutionContext, Future}

class CacheDataService @Inject() (repository: CacheDataRepository)(implicit ec: ExecutionContext) {

  def getOrCreateAnswers(implicit request: IdentifierRequest[_]): Future[UserAnswers] =
    repository.get(request.identifier) map { data =>
      data.flatMap(_.answers).getOrElse(new UserAnswers())
    }

  def updateAnswers(update: UserAnswers => UserAnswers)(implicit request: IdentifierRequest[_]): Future[UserAnswers] =
    repository.get(request.identifier) flatMap { maybeData =>
      val data: CacheData             = maybeData.getOrElse(CacheData(request.identifier))
      val updatedAnswers: UserAnswers = update(data.answers.getOrElse(UserAnswers()))
      repository.set(data.copy(answers = Some(updatedAnswers))) map { _ => updatedAnswers }
    }

}

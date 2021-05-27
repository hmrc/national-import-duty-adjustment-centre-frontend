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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.amend.{AmendAnswers, AmendClaimReceipt}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.amendNdrcClaim.AmendNdrcAnswers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.{CreateAnswers, CreateClaimReceipt}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.requests.IdentifierRequest
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.{CacheData, JourneyId}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.repositories.CacheDataRepository

import scala.concurrent.{ExecutionContext, Future}

class CacheDataService @Inject() (repository: CacheDataRepository)(implicit ec: ExecutionContext) {

  private def getCacheData(implicit request: IdentifierRequest[_]): Future[CacheData] =
    repository.get(request.identifier) flatMap {
      case Some(data) => Future(data)
      case None =>
        val data = CacheData(request.identifier)
        repository.insert(data) map { _ => data }
    }

  def getCreateAnswersWithJourneyId(implicit request: IdentifierRequest[_]): Future[(CreateAnswers, JourneyId)] =
    getCacheData map (cache => (cache.createAnswers, cache.journeyId))

  def getAmendAnswersWithJourneyId(implicit request: IdentifierRequest[_]): Future[(AmendAnswers, JourneyId)] =
    getCacheData map (cache => (cache.amendAnswers, cache.journeyId))

  def getAmendNdrcAnswersWithJourneyId(implicit request: IdentifierRequest[_]): Future[(AmendNdrcAnswers, JourneyId)] =
    getCacheData map (cache => (cache.amendNdrcAnswers, cache.journeyId))

  def getCreateAnswers(implicit request: IdentifierRequest[_]): Future[CreateAnswers] =
    getCacheData map (_.createAnswers)

  def getAmendAnswers(implicit request: IdentifierRequest[_]): Future[AmendAnswers] =
    getCacheData map (_.amendAnswers)


  def getAmendNdrcAnswers(implicit request: IdentifierRequest[_]): Future[AmendNdrcAnswers] =
    getCacheData map (_.amendNdrcAnswers)

  def updateCreateAnswers(
    update: CreateAnswers => CreateAnswers
  )(implicit request: IdentifierRequest[_]): Future[CreateAnswers] =
    getCacheData flatMap { cacheData =>
      val updatedAnswers: CreateAnswers = update(cacheData.createAnswers)
      repository.update(cacheData.update(updatedAnswers)) map { _ =>
        updatedAnswers
      }
    }

  def updateAmendAnswers(
    update: AmendAnswers => AmendAnswers
  )(implicit request: IdentifierRequest[_]): Future[AmendAnswers] =
    getCacheData flatMap { cacheData =>
      val updatedAnswers: AmendAnswers = update(cacheData.amendAnswers)
      repository.update(cacheData.update(updatedAnswers)) map { _ =>
        updatedAnswers
      }
    }

  def updateAmendNdrcAnswers(
                          update: AmendNdrcAnswers => AmendNdrcAnswers
                        )(implicit request: IdentifierRequest[_]): Future[AmendNdrcAnswers] =
    getCacheData flatMap { cacheData =>
      val updatedAnswers: AmendNdrcAnswers = update(cacheData.amendNdrcAnswers)
      repository.update(cacheData.update(updatedAnswers)) map { _ =>
        updatedAnswers
      }
    }

  def storeCreateReceipt(
    claimReceipt: CreateClaimReceipt
  )(implicit request: IdentifierRequest[_]): Future[Option[CacheData]] =
    getCacheData flatMap { cacheData =>
      repository.update(cacheData.store(claimReceipt))
    }

  def storeAmendReceipt(
    amendClaimReceipt: AmendClaimReceipt
  )(implicit request: IdentifierRequest[_]): Future[Option[CacheData]] =
    getCacheData flatMap { cacheData =>
      repository.update(cacheData.store(amendClaimReceipt))
    }

}

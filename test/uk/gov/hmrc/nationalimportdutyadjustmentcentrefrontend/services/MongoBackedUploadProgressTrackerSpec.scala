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

import akka.actor.ActorSystem
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.{reset, verify, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import org.scalatest.time.{Second, Span}
import org.scalatestplus.mockito.MockitoSugar.mock
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.{TestData, UnitSpec}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.config.AppConfig
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.upscan.UpscanNotification.Quarantine
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.upscan.{Failed, InProgress}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.repositories.UploadRepository
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.utils.Injector

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}

class MongoBackedUploadProgressTrackerSpec extends UnitSpec with Injector with BeforeAndAfterEach with TestData {

  implicit val ec: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global

  private val mockAppConfig        = mock[AppConfig]
  private val mockUploadRepository = mock[UploadRepository]
  private val actorSystem          = instanceOf[ActorSystem]

  private def tracker = new MongoBackedUploadProgressTracker(mockUploadRepository, mockAppConfig, actorSystem)

  override protected def beforeEach(): Unit = {
    super.beforeEach()

    when(mockAppConfig.uploadTimeout).thenReturn(500.milliseconds)
    when(mockAppConfig.uploadPollDelay).thenReturn(300.milliseconds)
  }

  override protected def afterEach(): Unit = {
    reset(mockAppConfig, mockUploadRepository)
    super.afterEach()
  }

  "MongoBackedUploadProgressTracker" should {

    "call repository" when {

      "adding upload details" in {
        when(mockUploadRepository.add(any())).thenReturn(Future.successful(true))

        tracker.requestUpload(uploadId, journeyId, fileReference).futureValue mustBe true

        verify(mockUploadRepository).add(any())
      }

      "updating upload upload details" in {
        when(mockUploadRepository.updateStatus(any(), any(), any())).thenReturn(Future.successful(true))

        tracker.registerUploadResult(
          fileReference,
          journeyId,
          Failed(Quarantine, "Virus detected")
        ).futureValue mustBe true

        verify(mockUploadRepository).updateStatus(any(), any(), any())
      }

      "getting upload result status" in {
        val details = uploadResult(Failed(Quarantine, "Virus detected"))
        when(mockUploadRepository.findUploadDetails(any(), any())).thenReturn(Future.successful(Some(details)))

        tracker.getUploadResult(uploadId, journeyId).futureValue mustBe Some(details.status)

        verify(mockUploadRepository).findUploadDetails(any(), any())
      }
    }

    "call repository multiple times when status 'InProgress'" in {
      val details = uploadResult(InProgress())
      when(mockUploadRepository.findUploadDetails(any(), any())).thenReturn(Future.successful(Some(details)))

      // None returned when "uploadTimeout" happens
      tracker.getUploadResult(uploadId, journeyId).futureValue(Timeout(Span(1, Second))) mustBe Some(InProgress())

      /*
       Called once at beginning (InProgress), once after 300ms polling delay (InProgress) and one final time after a
        further 300ms delay after which it will "give up" as the 500ms upload timeout will have expired
       */
      verify(mockUploadRepository, Mockito.times(3)).findUploadDetails(any(), any())
    }

  }
}

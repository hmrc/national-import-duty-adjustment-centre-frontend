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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.makeclaim

import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{never, reset, verify, when}
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.{ControllerSpec, TestData}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.config.AppConfig
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.connectors.UpscanInitiateConnector
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.JourneyId
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.CreateAnswers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.upscan.UploadStatus
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.upscan.UpscanNotification.Quarantine
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.services.UploadProgressTracker
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.viewmodels.NavigatorBack
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.UploadFormView
import uk.gov.hmrc.play.bootstrap.tools.Stubs.stubMessagesControllerComponents

import scala.concurrent.Future

class UploadFormControllerSpec extends ControllerSpec with TestData {

  private val formView = mock[UploadFormView]

  private val mockInitiateConnector = mock[UpscanInitiateConnector]
  private val appConfig             = instanceOf[AppConfig]
  private val mockProgressTracker   = mock[UploadProgressTracker]

  private def controller =
    new UploadFormController(
      stubMessagesControllerComponents(),
      fakeAuthorisedIdentifierAction,
      mockProgressTracker,
      mockInitiateConnector,
      cacheDataService,
      appConfig,
      navigator,
      formView
    )(executionContext)

  override protected def beforeEach(): Unit = {
    super.beforeEach()

    withCacheCreateAnswers(emptyAnswers)

    when(mockInitiateConnector.initiateV2(any[JourneyId], any(), any())(any())).thenReturn(
      Future.successful(upscanInitiateResponse)
    )

    when(mockProgressTracker.requestUpload(any(), any(), any())).thenReturn(Future.successful(true))

    when(formView.apply(any(), any(), any(), any(), any())(any(), any())).thenReturn(HtmlFormat.empty)
  }

  override protected def afterEach(): Unit = {
    reset(formView, mockInitiateConnector, mockProgressTracker)
    super.afterEach()
  }

  private def givenUploadStatus(status: UploadStatus): Unit =
    when(mockProgressTracker.getUploadResult(any(), any())).thenReturn(Future.successful(Some(status)))

  def theFormViewBackLink: NavigatorBack = {
    val captor = ArgumentCaptor.forClass(classOf[NavigatorBack])
    verify(formView).apply(any(), any(), any(), any(), captor.capture())(any(), any())
    captor.getValue
  }

  "onPageLoad" should {

    "initiate upscan and persist the result" in {
      val result = controller.onPageLoad()(fakeGetRequest)
      status(result) mustBe OK

      verify(mockInitiateConnector).initiateV2(any(), any(), any())(any())
      verify(mockProgressTracker).requestUpload(any(), any(), any())
    }

    "produce back link" when {

      "user has not uploaded any files" in {
        withCacheCreateAnswers(completeAnswers.copy(uploads = Seq.empty))
        val result = controller.onPageLoad()(fakeGetRequest)
        status(result) mustBe OK

        theFormViewBackLink mustBe NavigatorBack(Some(routes.RequiredDocumentsController.onPageLoad()))
      }

      "user has uploaded some files" in {
        withCacheCreateAnswers(completeAnswers)
        val result = controller.onPageLoad()(fakeGetRequest)
        status(result) mustBe OK

        theFormViewBackLink mustBe NavigatorBack(Some(routes.RequiredDocumentsController.onPageLoad()))
      }
    }

  }

  "onProgress" should {

    "redirect when upload in progress" in {

      givenUploadStatus(uploadInProgress)
      val result = controller.onProgress(uploadId)(fakeGetRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.UploadFormController.onError("TIMEOUT").url)
    }

    "redirect when upload failed" in {

      givenUploadStatus(uploadFailed)
      val result = controller.onProgress(uploadId)(fakeGetRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.UploadFormController.onError(Quarantine.toString).url)
    }

    "redirect when uploading a duplicate file" in {

      withCacheCreateAnswers(completeAnswers.copy(uploads = Seq(uploadAnswer)))
      givenUploadStatus(uploadFileSuccess)
      val result = controller.onProgress(uploadId)(fakeGetRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.UploadFormController.onError("DUPLICATE").url)

      verify(dataRepository, never()).update(any())
    }

    "update UserAnswers and redirect to summary when upload succeeds" in {

      givenUploadStatus(uploadFileSuccess)
      val result = controller.onProgress(uploadId)(fakeGetRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.UploadFormController.onPageLoad().url)

      theUpdatedCreateAnswers.uploads mustBe Seq(uploadFileSuccess)
    }

  }

  "onError" should {

    "initiate upscan and persist the result" in {
      val result = controller.onError("code")(fakeGetRequest)
      status(result) mustBe OK

      verify(mockInitiateConnector).initiateV2(any(), any(), any())(any())
      verify(mockProgressTracker).requestUpload(any(), any(), any())
    }

    "redirect to 'continue' if error is InvalidArgumemt and a file has been uploaded" in {
      withCacheCreateAnswers(completeAnswers)
      val result = controller.onError("InvalidArgument")(fakeGetRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.UploadFormController.onContinue().url)
    }

    "redirect to 'continue' if error is FileTooSmall and a file has been uploaded" in {
      withCacheCreateAnswers(completeAnswers)
      val result = controller.onError("EntityTooSmall")(fakeGetRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.UploadFormController.onContinue().url)
    }

  }

  "onRemove" should {

    "remove uploaded document" in {
      withCacheCreateAnswers(CreateAnswers(uploads = Seq(uploadAnswer, uploadAnswer2)))
      val result = controller.onRemove(uploadAnswer.upscanReference)(postRequest())
      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.makeclaim.routes.UploadFormController.onPageLoad().url)

      theUpdatedCreateAnswers.uploads mustBe Seq(uploadAnswer2)
    }
  }

}

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

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, verify, when}
import org.mockito.{ArgumentCaptor, ArgumentMatchers}
import play.api.data.Form
import play.api.http.Status
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.{ControllerSpec, TestData}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.config.AppConfig
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.create.ImporterDetailsFormProvider
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.addresslookup.AddressLookupOnRamp
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.{CreateAnswers, ImporterContactDetails}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages.{AddressPage, ImporterContactDetailsPage}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.services.AddressLookupService
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.ImporterDetailsView
import uk.gov.hmrc.play.bootstrap.tools.Stubs.stubMessagesControllerComponents

import scala.concurrent.Future

class ImporterDetailsControllerSpec extends ControllerSpec with TestData {

  private val page                 = mock[ImporterDetailsView]
  private val formProvider         = new ImporterDetailsFormProvider
  private val addressLookupService = mock[AddressLookupService]
  val appConfig: AppConfig         = instanceOf[AppConfig]

  private def controller =
    new ImporterDetailsController(
      fakeAuthorisedIdentifierAction,
      cacheDataService,
      formProvider,
      addressLookupService,
      stubMessagesControllerComponents(),
      navigator,
      page
    )(executionContext, appConfig)

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    withEmptyCache()
    when(page.apply(any(), any())(any(), any())).thenReturn(HtmlFormat.empty)
  }

  override protected def afterEach(): Unit = {
    reset(page)
    super.afterEach()
  }

  def theResponseForm: Form[ImporterContactDetails] = {
    val captor = ArgumentCaptor.forClass(classOf[Form[ImporterContactDetails]])
    verify(page).apply(captor.capture(), any())(any(), any())
    captor.getValue
  }

  "GET" should {

    "redirect to change when cache is empty" in {
      val result = controller.onPageLoad()(fakeGetRequest)
      status(result) mustBe Status.SEE_OTHER

      redirectLocation(result) mustBe Some(routes.ImporterDetailsController.onChange().url)
    }

    "redirect to address lookup page from change, when cache is empty" in {
      when(addressLookupService.initialiseJourney(any(), any(), any(), any(), any(), any())(any(), any())).thenReturn(
        Future.successful(AddressLookupOnRamp("http://localhost/AddressLookupReturnedRedirect"))
      )
      val result = controller.onChange()(fakeGetRequest)
      status(result) mustBe Status.SEE_OTHER

      redirectLocation(result) mustBe Some("http://localhost/AddressLookupReturnedRedirect")
    }

    "update cache and redirect when address lookup calls update" in {
      withCacheCreateAnswers(emptyAnswers)
      when(addressLookupService.retrieveAddress(ArgumentMatchers.eq(addressLookupRetrieveId))(any(), any())).thenReturn(
        Future.successful(importerAddressLookupConfirmation)
      )
      val result = controller.onUpdate(addressLookupRetrieveId)(fakeGetRequest)
      status(result) mustBe Status.SEE_OTHER
      theUpdatedCreateAnswers.importerContactDetails mustBe Some(auditableImporterContactDetails)
      redirectLocation(result) mustBe Some(navigator.nextPage(ImporterContactDetailsPage, theUpdatedCreateAnswers).url)
    }

    "return 400 (BAD REQUEST) when address lookup returns an invalid address" in {
      withCacheCreateAnswers(emptyAnswers)
      when(addressLookupService.retrieveAddress(ArgumentMatchers.eq(addressLookupRetrieveId))(any(), any())).thenReturn(
        Future.successful(addressLookupConfirmationInvalid)
      )
      val result = controller.onUpdate(addressLookupRetrieveId)(fakeGetRequest)
      status(result) mustEqual BAD_REQUEST
    }

    "display page when cache has answer" in {
      withCacheCreateAnswers(CreateAnswers(importerContactDetails = Some(auditableImporterContactDetails)))
      val result = controller.onPageLoad()(fakeGetRequest)
      status(result) mustBe Status.OK

      theResponseForm.value mustBe Some(auditableImporterContactDetails)
    }
  }

  "POST" should {

    val validRequest = postRequest(
      "addressLine1" -> auditableImporterContactDetails.addressLine1,
      "addressLine2" -> auditableImporterContactDetails.addressLine2.getOrElse(""),
      "addressLine3" -> auditableImporterContactDetails.addressLine3.getOrElse(""),
      "city"         -> auditableImporterContactDetails.city,
      "postcode"     -> auditableImporterContactDetails.postCode,
      "auditRef"     -> auditableImporterContactDetails.auditRef.get
    )

    "update cache, clear audit ref and redirect when valid answer is submitted" in {

      val previouslyLookedUpAddress =
        ImporterContactDetails(
          "The Old Windmill",
          None,
          None,
          "Trumpton",
          "TR1 1WM",
          auditableImporterContactDetails.auditRef
        )

      withCacheCreateAnswers(emptyAnswers.copy(importerContactDetails = Some(previouslyLookedUpAddress)))

      val result = controller.onSubmit()(validRequest)
      status(result) mustEqual SEE_OTHER
      theUpdatedCreateAnswers.importerContactDetails mustBe Some(importerContactDetailsAnswer)
      theUpdatedCreateAnswers.importerContactDetails.flatMap(cd => cd.auditRef) mustBe None
      redirectLocation(result) mustBe Some(navigator.nextPage(ImporterContactDetailsPage, theUpdatedCreateAnswers).url)
    }

    "preserve audit ref and redirect when same answer is submitted" in {

      val previouslyLookedUpAddress = auditableImporterContactDetails

      withCacheCreateAnswers(emptyAnswers.copy(importerContactDetails = Some(previouslyLookedUpAddress)))

      val result = controller.onSubmit()(validRequest)
      status(result) mustEqual SEE_OTHER
      theUpdatedCreateAnswers.importerContactDetails mustBe Some(auditableImporterContactDetails)
      theUpdatedCreateAnswers.importerContactDetails.flatMap(
        ca => ca.auditRef
      ) mustBe auditableImporterContactDetails.auditRef
      redirectLocation(result) mustBe Some(navigator.nextPage(ImporterContactDetailsPage, theUpdatedCreateAnswers).url)
    }

    "return 400 (BAD REQUEST) when invalid data posted" in {

      val result = controller.onSubmit()(postRequest())
      status(result) mustEqual BAD_REQUEST
    }

  }
}

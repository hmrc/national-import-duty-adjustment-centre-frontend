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
import org.mockito.Mockito.{reset, verify, when}
import play.api.data.Form
import play.api.http.Status
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.{ControllerSpec, TestData}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.RepaymentTypeFormProvider
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.RepaymentType.{Bacs, TaxAccount}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.{RepaymentType, UserAnswers}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages.RepaymentTypePage
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.RepaymentTypePage
import uk.gov.hmrc.play.bootstrap.tools.Stubs.stubMessagesControllerComponents

class RepaymentTypeControllerSpec extends ControllerSpec with TestData {

  private val page         = mock[RepaymentTypePage]
  private val formProvider = new RepaymentTypeFormProvider

  private def controller =
    new RepaymentTypeController(
      fakeAuthorisedIdentifierAction,
      cacheDataService,
      formProvider,
      stubMessagesControllerComponents(),
      navigator,
      page
    )(executionContext)

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    withEmptyCache
    when(page.apply(any())(any(), any())).thenReturn(HtmlFormat.empty)
  }

  override protected def afterEach(): Unit = {
    reset(page)
    super.afterEach()
  }

  def theResponseForm: Form[RepaymentType] = {
    val captor = ArgumentCaptor.forClass(classOf[Form[RepaymentType]])
    verify(page).apply(captor.capture())(any(), any())
    captor.getValue
  }

  "GET" should {

    "display page when cache is empty" in {
      val result = controller.onPageLoad()(fakeGetRequest)
      status(result) mustBe Status.OK

      theResponseForm.value mustBe None
    }

    "display page when cache has answer" in {
      withCacheUserAnswers(UserAnswers(repaymentType = Some(Bacs)))
      val result = controller.onPageLoad()(fakeGetRequest)
      status(result) mustBe Status.OK

      theResponseForm.value mustBe Some(Bacs)
    }
  }

  "POST" should {

    val validRequest = postRequest(("repayment_type", TaxAccount.toString))

    "update cache and redirect when valid answer is submitted" in {

      withCacheUserAnswers(emptyAnswers)

      val result = controller.onSubmit()(validRequest)
      status(result) mustEqual SEE_OTHER
      val updatedAnswers = theUpdatedUserAnswers
      updatedAnswers.repaymentType mustBe Some(TaxAccount)
      redirectLocation(result) mustBe Some(navigator.nextPage(RepaymentTypePage, updatedAnswers).url)
    }

    "return 400 (BAD REQUEST) when invalid data posted" in {

      val result = controller.onSubmit()(postRequest())
      status(result) mustEqual BAD_REQUEST
    }

  }
}

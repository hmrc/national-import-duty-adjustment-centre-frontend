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

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, verify, when}
import play.api.http.Status
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.ControllerSpec
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.actions.IdentifierAction
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.SignedOutPage
import uk.gov.hmrc.play.bootstrap.tools.Stubs.stubMessagesControllerComponents

import scala.concurrent.Future

class SignOutControllerSpec extends ControllerSpec {

  val page: SignedOutPage = mock[SignedOutPage]

  private def controller(identifyAction: IdentifierAction) =
    new SignOutController(stubMessagesControllerComponents(), identifyAction, userAnswersRepository, page)

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    when(page.apply()(any(), any())).thenReturn(HtmlFormat.empty)
  }

  override protected def afterEach(): Unit = {
    reset(page)
    super.afterEach()
  }

  "GET /sign-out" should {

    "sign out user when user is authorised" in {
      when(userAnswersRepository.delete(any())).thenReturn(Future.successful(()))
      val result = controller(fakeAuthorisedIdentifierAction).signOut(fakeGetRequest)

      status(result) mustBe Status.SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SignOutController.signedOut().url)
      verify(userAnswersRepository).delete(any())
    }

    "redirect when user is unauthorised" in {
      val result = controller(fakeUnauthorisedIdentifierAction).signOut(fakeGetRequest)
      status(result) mustBe Status.SEE_OTHER
      redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad().url)
    }
  }

  "GET /signed-out" should {

    "display page" in {
      val result = controller(fakeUnauthorisedIdentifierAction).signedOut(fakeGetRequest)
      status(result) mustBe Status.OK
    }
  }
}

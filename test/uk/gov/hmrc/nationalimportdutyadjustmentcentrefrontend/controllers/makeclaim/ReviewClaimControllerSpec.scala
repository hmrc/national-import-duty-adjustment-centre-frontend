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
import org.mockito.Mockito.{reset, when}
import play.api.http.Status
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.ControllerSpec
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.{CreateClaimResponse, CreateClaimResult}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.ReviewClaimView
import uk.gov.hmrc.play.bootstrap.tools.Stubs.stubMessagesControllerComponents

class ReviewClaimControllerSpec extends ControllerSpec {

  val page: ReviewClaimView = mock[ReviewClaimView]

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    when(page.apply(any())(any(), any())).thenReturn(HtmlFormat.empty)
  }

  override protected def afterEach(): Unit = {
    reset(page)
    super.afterEach()
  }

  private val controller =
    new ReviewClaimController(stubMessagesControllerComponents(), fakeAuthorisedIdentifierAction, dataRepository, page)

  "GET" should {
    "return OK when cache contains claim response" in {
      withCachedClaimResponse(CreateClaimResponse("id", result = Some(CreateClaimResult("reference", Seq.empty))))
      val result = controller.onPageLoad()(fakeGetRequest)

      status(result) mustBe Status.OK
    }

    "redirect to 'session expired' when cache does not contain claim response" in {
      withEmptyCache
      val result = controller.onPageLoad()(fakeGetRequest)

      status(result) mustBe Status.SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.SessionExpiredController.onPageLoad().url)
    }

  }
}

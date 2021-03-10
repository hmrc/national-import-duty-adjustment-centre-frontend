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

import play.api.http.Status
import play.api.test.Helpers._
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.ControllerSpec
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.actions.IdentifierAction
import uk.gov.hmrc.play.bootstrap.tools.Stubs.stubMessagesControllerComponents

class StartControllerSpec extends ControllerSpec {

  private def controller(identifyAction: IdentifierAction) =
    new StartController(stubMessagesControllerComponents(), identifyAction)

  "GET" should {

    "redirect to what do you want to do when user is authorised" in {
      val result = controller(fakeAuthorisedIdentifierAction).start(fakeGetRequest)
      status(result) mustBe Status.SEE_OTHER
      redirectLocation(result) mustBe Some(routes.WhatDoYouWantToDoController.onPageLoad().url)
    }

    "redirect when user is unauthorised" in {
      val result = controller(fakeUnauthorisedIdentifierAction).start(fakeGetRequest)
      status(result) mustBe Status.SEE_OTHER
      redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad().url)
    }
  }
}

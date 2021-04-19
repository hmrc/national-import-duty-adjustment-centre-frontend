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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views

import play.twirl.api.Html
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.UnitViewSpec
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.config.AppConfig
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.routes
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.ServiceUnavailablePage

class ServiceUnavailablePageViewSpec extends UnitViewSpec {

  private val page      = instanceOf[ServiceUnavailablePage]
  private val appConfig = instanceOf[AppConfig]

  private val view: Html = page()

  "ServiceUnavailablePage" should {

    "have correct title" in {
      view.title() must startWith(messages("service.unavailable.title"))
    }

    "have correct heading" in {
      view.getElementsByTag("h1").text() mustBe messages("service.unavailable.title")
    }

    "have sign out link" in {
      view.getElementsByClass("hmrc-sign-out-nav__link").attr("href") mustBe routes.SignOutController.feedBack().url
    }

  }
}

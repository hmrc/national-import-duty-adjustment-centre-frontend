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
import org.mockito.Mockito.verify
import play.api.http.Status
import play.api.test.Helpers._
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.ControllerSpec
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.config.AppConfig
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.actions.IdentifierAction
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.CacheData
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.CreateAnswers
import uk.gov.hmrc.play.bootstrap.tools.Stubs.stubMessagesControllerComponents

class KeepAliveControllerSpec extends ControllerSpec {

  val appConfig: AppConfig = instanceOf[AppConfig]

  private def controller(identifyAction: IdentifierAction) =
    new KeepAliveController(stubMessagesControllerComponents(), identifyAction, cacheDataService)

  "GET /keep-alive" should {

    "update users session and data cache" in {
      withCacheCreateAnswers(CreateAnswers())
      val result = controller(fakeAuthorisedIdentifierAction).keepAlive(fakeGetRequest)
      status(result) mustBe Status.OK

      verify(dataRepository).update(any[CacheData])

    }

  }

}

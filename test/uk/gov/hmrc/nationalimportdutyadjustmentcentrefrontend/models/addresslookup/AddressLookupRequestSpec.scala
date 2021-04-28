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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.addresslookup

import org.scalatestplus.mockito.MockitoSugar
import play.api.i18n.MessagesApi
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.UnitSpec
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.config.AppConfig
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.addresslookup.AddressLookupRequest.Labels.Language.{
  AppLevelLabels,
  ConfirmPageLabels,
  EditPageLabels,
  LookupPageLabels,
  SelectPageLabels
}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.addresslookup.AddressLookupRequest.Options.TimeoutConfig
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.addresslookup.AddressLookupRequest.{
  Labels,
  Options
}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.utils.Injector

class AddressLookupRequestSpec extends UnitSpec with MockitoSugar with Injector {

  val realMessagesApi: MessagesApi = instanceOf[MessagesApi]
  val appConfig: AppConfig         = instanceOf[AppConfig]

  "AddressLookupRequest" should {
    "create properly for Address Lookup initialisation " in {

      val request = AddressLookupRequest(
        continueUrl = "http://continue",
        homeUrl = "http://start",
        signOutUrl = "http://leave",
        keepAliveUrl = "http://keepalive",
        lookupPageHeadingKey = "address.title",
        hintKey = "address.hint"
      )(realMessagesApi, appConfig)

      request mustBe expectedRequest
    }
  }

  val expectedRequest = new AddressLookupRequest(
    2,
    Options(
      continueUrl = "http://continue",
      serviceHref = "http://start",
      signOutHref = "http://leave",
      showPhaseBanner = true,
      ukMode = true,
      includeHMRCBranding = false,
      timeoutConfig = TimeoutConfig(
        timeoutAmount = Some(appConfig.timeoutDialogTimeout),
        timeoutUrl = Some("http://leave"),
        timeoutKeepAliveUrl = Some("http://keepalive")
      )
    ),
    Labels(
      en =
        AddressLookupRequest.Labels.Language(
          AppLevelLabels(navTitle = Some("Apply for return of import duty secured by deposit or guarantee")),
          SelectPageLabels(title = Some("Select an address"), heading = Some("Select an address")),
          LookupPageLabels(
            title = Some("What is your UK correspondence address?"),
            heading = Some("What is your UK correspondence address?"),
            afterHeadingText = Some("We will use this to send letters about this claim.")
          ),
          ConfirmPageLabels(title = Some("Confirm your address"), heading = Some("Confirm your address")),
          EditPageLabels(
            title = Some("What is your UK correspondence address?"),
            heading = Some("What is your UK correspondence address?"),
            postcodeLabel = Some("UK postcode")
          )
        ),
      cy =
        AddressLookupRequest.Labels.Language(
          AppLevelLabels(navTitle = Some("Apply for return of import duty secured by deposit or guarantee")),
          SelectPageLabels(),
          LookupPageLabels(heading = Some("What is your UK correspondence address?")),
          ConfirmPageLabels(),
          EditPageLabels()
        )
    )
  )

}

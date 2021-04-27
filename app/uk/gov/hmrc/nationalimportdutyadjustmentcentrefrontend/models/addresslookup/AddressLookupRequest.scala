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

import play.api.i18n.{Lang, Messages, MessagesApi, MessagesImpl}
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.config.AppConfig
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.addresslookup.AddressLookupRequest.Labels.Language.{
  AppLevelLabels,
  ConfirmPageLabels,
  EditPageLabels,
  LookupPageLabels,
  SelectPageLabels
}

case class AddressLookupRequest(
  version: Int = 2,
  options: AddressLookupRequest.Options,
  labels: AddressLookupRequest.Labels
)

object AddressLookupRequest {
  implicit val format: OFormat[AddressLookupRequest] = Json.format[AddressLookupRequest]

  def apply(continueUrl: String, homeUrl: String, lookupPageHeadingKey: String)(implicit
    messagesApi: MessagesApi,
    config: AppConfig
  ): AddressLookupRequest = {
    val eng: Messages = MessagesImpl(Lang("en"), messagesApi)
    val cy: Messages  = MessagesImpl(Lang("cy"), messagesApi)
    new AddressLookupRequest(
      2,
      Options(continueUrl = continueUrl, homeNavHref = Some(homeUrl), showPhaseBanner = true, ukMode = true, includeHMRCBranding = false),
      Labels(
        en =
          AddressLookupRequest.Labels.Language(
            AppLevelLabels(navTitle = Some(eng("service.name"))),
            SelectPageLabels(title = Some(eng("alf.label.select.title"))),
            LookupPageLabels(title = Some(eng("alf.label.lookup.title")), heading = Some(eng("alf.label.lookup.title"))),
            ConfirmPageLabels(title = Some(eng("alf.label.confirm.title")), heading = Some(eng("alf.label.confirm.title"))),
            EditPageLabels(postcodeLabel = Some(eng("alf.label.edit.postcode")))
          ),
        cy =
          AddressLookupRequest.Labels.Language(
            AppLevelLabels(navTitle = Some(cy("service.name"))),
            SelectPageLabels(),
            LookupPageLabels(heading = Some(eng(lookupPageHeadingKey))),
            ConfirmPageLabels(),
            EditPageLabels(postcodeLabel = Some("Can be changed"))
          )
      )
    )
  }

  case class Options(continueUrl: String, homeNavHref: Option[String], showPhaseBanner: Boolean, ukMode: Boolean, includeHMRCBranding: Boolean)

  object Options {
    implicit val format: OFormat[Options] = Json.format[Options]
  }

  case class Labels(en: AddressLookupRequest.Labels.Language, cy: AddressLookupRequest.Labels.Language)

  object Labels {
    implicit val format: OFormat[Labels] = Json.format[Labels]

    case class Language(
      appLevelLabels: AddressLookupRequest.Labels.Language.AppLevelLabels,
      selectPageLabels: AddressLookupRequest.Labels.Language.SelectPageLabels,
      lookupPageLabels: AddressLookupRequest.Labels.Language.LookupPageLabels,
      confirmPageLabels: AddressLookupRequest.Labels.Language.ConfirmPageLabels,
      editPageLabels: AddressLookupRequest.Labels.Language.EditPageLabels
    )

    object Language {
      implicit val format: OFormat[Language] = Json.format[Language]

      case class AppLevelLabels(navTitle: Option[String] = None, phaseBannerHtml: Option[String] = None)

      object AppLevelLabels {
        implicit val format: OFormat[AppLevelLabels] = Json.format[AppLevelLabels]
      }

      case class SelectPageLabels(
        title: Option[String] = None,
        heading: Option[String] = Some("Select an address"),
        submitLabel: Option[String] = None,
        editAddressLinkText: Option[String] = None
      )

      object SelectPageLabels {
        implicit val format: OFormat[SelectPageLabels] = Json.format[SelectPageLabels]
      }

      case class LookupPageLabels(
        title: Option[String] = None,
        heading: Option[String] = Some("Select an address"),
        filterLabel: Option[String] = None,
        postcodeLabel: Option[String] = None,
        submitLabel: Option[String] = None
      )

      object LookupPageLabels {
        implicit val format: OFormat[LookupPageLabels] = Json.format[LookupPageLabels]
      }

      case class ConfirmPageLabels(
        title: Option[String] = None,
        heading: Option[String] = None,
        showConfirmChangeText: Option[Boolean] = None
      )

      object ConfirmPageLabels {
        implicit val format: OFormat[ConfirmPageLabels] = Json.format[ConfirmPageLabels]
      }

      case class EditPageLabels(
                                 submitLabel: Option[String] = None,
                                   postcodeLabel: Option[String] = None
                               )

      object EditPageLabels {
        implicit val format: OFormat[EditPageLabels] = Json.format[EditPageLabels]
      }

    }

  }

}

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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.makeclaim

import org.jsoup.nodes.Document
import play.api.data.Form
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.{TestData, UnitViewSpec}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.create.AddressFormProvider
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.Address
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.AddressView

class AddressViewSpec extends UnitViewSpec with TestData {

  private val page = instanceOf[AddressView]
  private val form = new AddressFormProvider().apply()

  private def view(form: Form[Address] = form): Document = page(form, navigatorBack)

  "AddressPage on empty form" should {

    "have correct title" in {
      view().title() must startWith(messages("address.title"))
    }

    "have correct heading" in {
      view().getElementsByTag("h1") must containMessage("address.title")
    }

    "have back link" in {
      view() must haveNavigatorBackLink(navigatorBackUrl)
    }

    "have label for addresss line 1" in {
      view().getElementsByAttributeValue("for", "addressLine1").text() must include(
        s"${messages("address.line1.heading")} ${messages("address.line1.hidden")}"
      )
    }

    "have label for addresss line 2" in {
      view().getElementsByAttributeValue("for", "addressLine2").text() must include(messages("address.line2.hidden"))
    }

    "have label for addresss line 3" in {
      view().getElementsByAttributeValue("for", "addressLine3").text() must include(messages("address.line3.hidden"))
    }

    "have label for town or city" in {
      view().getElementsByAttributeValue("for", "city") must containMessage("address.city.heading")
    }

    "have label for postcode" in {
      view().getElementsByAttributeValue("for", "postcode") must containMessage("address.postcode.heading")
    }

    "have 'Continue' button" in {
      view().getElementById("nidac-continue") must includeMessage("site.continue")
    }

  }

  "AddressPage on filled form" should {

    "have populated fields" in {
      val filledView = view(form.fill(addressAnswer))

      filledView.getElementById("addressLine1") must haveValue(addressAnswer.addressLine1)
      filledView.getElementById("addressLine2") must haveValue(addressAnswer.addressLine2.getOrElse(""))
      filledView.getElementById("addressLine3") must haveValue(addressAnswer.addressLine3.getOrElse(""))
      filledView.getElementById("city") must haveValue(addressAnswer.city)
      filledView.getElementById("postcode") must haveValue(addressAnswer.postCode.getOrElse(""))
    }

    "display error when " when {

      val answers = Map(
        "name"         -> "Joe Soap Ltd",
        "addressLine1" -> "123 High Street",
        "addressLine2" -> "Near Bradford",
        "city"         -> "Bradford",
        "postcode"     -> "BD12CD"
      )

      "line 1 missing" in {
        val errorView = view(form.bind(answers - "addressLine1"))
        errorView must haveFieldError("addressLine1", "address.line1.error.required")
        errorView must havePageError("address.line1.error.required")
      }

      "city missing" in {
        val errorView = view(form.bind(answers - "city"))
        errorView must haveFieldError("city", "address.city.error.required")
        errorView must havePageError("address.city.error.required")
      }

      "postcode" in {
        val errorView = view(form.bind(answers + ("postcode" -> "PO1!FG")))
        errorView must haveFieldError("postcode", "address.postcode.error.invalid")
        errorView must havePageError("address.postcode.error.invalid")
      }

    }

  }
}

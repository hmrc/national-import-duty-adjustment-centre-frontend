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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.amend

import play.api.data.Form
import play.api.data.Forms._
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.mappings.Mappings
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.amend.FurtherInformation

import javax.inject.Inject

class FurtherInformationFormProvider @Inject() extends Mappings {

  def apply(): Form[FurtherInformation] = Form(
    mapping(
      "furtherInformation" -> text("further_information.error.required")
        .verifying(firstError(maxLength(1024, "further_information.error.length")))
    )(FurtherInformation.apply)(FurtherInformation.unapply)
  )

}

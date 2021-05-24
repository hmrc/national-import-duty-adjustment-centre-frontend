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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.create

import javax.inject.Inject
import play.api.data.Form
import play.api.data.Forms._
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.mappings.{Mappings, Validation}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.EoriNumber
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.mappings.Implicits.SanitizedString

class EoriNumberFormProvider @Inject() extends Mappings {

  def apply(): Form[EoriNumber] = {

    val formToModel = (eoriNumber: String) => EoriNumber(eoriNumber.stripSpaces.toUpperCase)

    Form(
      mapping(
        "eoriNumber" -> text("importer.eori.error.required")
          .verifying(
            firstError(regexp(Validation.eoriNumber, "importer.eori.error.invalid", _.stripSpaces.toUpperCase))
          )
      )(formToModel)(EoriNumber.unapply)
    )
  }

}

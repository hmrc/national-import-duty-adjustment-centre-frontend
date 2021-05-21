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

import play.api.data.FormError
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.behaviours.StringFieldBehaviours
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.mappings.Validation
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.EoriNumber

class EoriNumberFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "importer.eori.error.required"
  val invalidKey  = "importer.eori.error.invalid"

  ".EoriNumber" must {

    val form      = new EoriNumberFormProvider()()
    val fieldName = "eoriNumber"

    behave like fieldThatBindsValidData(form, fieldName, eoriNumber)

    behave like fieldThatPreventsUnsafeInput(
      form,
      fieldName,
      unsafeInputsWithMaxLength(17),
      FormError(fieldName, invalidKey, Seq(Validation.eoriNumber))
    )

    behave like mandatoryField(form, fieldName, requiredError = FormError(fieldName, requiredKey))
  }

  "Form" must {
    "Accept valid form data" in {
      val form = new EoriNumberFormProvider().apply().bind(buildFormData("GB123456789012"))

      form.hasErrors mustBe false
      form.value mustBe Some(EoriNumber("GB123456789012"))
    }

    "Accept lower case EORI with spaces and convert to correct format" in {
      val form = new EoriNumberFormProvider().apply().bind(buildFormData(" gb 1234 5678 9012 "))

      form.hasErrors mustBe false
      form.value mustBe Some(EoriNumber("GB123456789012"))
    }

    def buildFormData(eoriNumber: String) =
      Map("eoriNumber" -> eoriNumber)
  }

}

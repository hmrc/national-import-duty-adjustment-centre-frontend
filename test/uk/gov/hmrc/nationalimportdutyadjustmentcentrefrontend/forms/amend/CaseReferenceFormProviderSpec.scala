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

import play.api.data.FormError
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.behaviours.StringFieldBehaviours
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.mappings.Validation
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.amend.CaseReference

class CaseReferenceFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "amend.case.reference.error.required"
  val invalidKey  = "amend.case.reference.error.invalid"

  ".CaseReference" must {
    val form = new CaseReferenceFormProvider()()

    val fieldName = "caseReference"

    behave like fieldThatBindsValidData(form, fieldName, caseReference)

    behave like fieldThatPreventsUnsafeInput(
      form,
      fieldName,
      unsafeInputsWithMaxLength(22),
      FormError(fieldName, invalidKey, Seq(Validation.caseReference))
    )

    behave like mandatoryField(form, fieldName, requiredError = FormError(fieldName, requiredKey))
  }

  "Form" must {
    "Accept valid form data" in {
      val form = new CaseReferenceFormProvider().apply().bind(buildFormData("NID21134557697RM8WIB13"))

      form.hasErrors mustBe false
      form.value mustBe Some(CaseReference("NID21134557697RM8WIB13"))
    }

    "Accept lower case reference with spaces and convert to correct format" in {
      val form = new CaseReferenceFormProvider().apply().bind(buildFormData(" nid 2113 4557697 RM8 wiB13 "))

      form.hasErrors mustBe false
      form.value mustBe Some(CaseReference("NID21134557697RM8WIB13"))
    }

    def buildFormData(caseReference: String) =
      Map("caseReference" -> caseReference)
  }

}

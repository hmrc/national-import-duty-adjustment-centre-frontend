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

class DutyPaidFormProviderSpec extends StringFieldBehaviours {

  val form = new DutyPaidFormProvider()()

  "ActuallyPaid" must {

    val fieldName   = "actuallyPaid"
    val requiredKey = "dutyPaid.actual.error.required"
    val invalidKey  = "dutyPaid.error.invalid"
    val zeroError   = "dutyPaid.actual.error.zero"

    behave like fieldThatBindsValidData(form, fieldName, decimals)

    behave like mandatoryField(form, fieldName, requiredError = FormError(fieldName, requiredKey))

    behave like fieldThatPreventsUnsafeInput(
      form,
      fieldName,
      nonNumerics,
      FormError(fieldName, invalidKey, Seq(Validation.dutyPattern))
    )

    "not error with valid value" in {
      assertErrorsForValue(fieldName, "0.1", Seq.empty)
    }

    "error when input is zero" in {
      assertErrorsForValue(fieldName, "0", Seq(FormError(fieldName, zeroError, Seq.empty)))
    }

    "error when format is invalid" in {
      assertErrorsForValue(fieldName, "10.120", Seq(FormError(fieldName, invalidKey, Seq(Validation.dutyPattern))))
    }
  }

  "ShouldPaid" must {

    val fieldName     = "shouldPaid"
    val requiredKey   = "dutyPaid.should.error.required"
    val invalidKey    = "dutyPaid.error.invalid"
    val negativeError = "dutyPaid.should.error.negative"

    behave like fieldThatBindsValidData(form, fieldName, decimals)

    behave like mandatoryField(form, fieldName, requiredError = FormError(fieldName, requiredKey))

    behave like fieldThatPreventsUnsafeInput(
      form,
      fieldName,
      nonNumerics,
      FormError(fieldName, invalidKey, Seq(Validation.dutyPattern))
    )

    "not error with valid value" in {
      assertErrorsForValue(fieldName, "0.00", Seq.empty)
    }

    "error when input is negative" in {
      assertErrorsForValue(fieldName, "-10", Seq(FormError(fieldName, negativeError, Seq.empty)))
    }

    "error when format is invalid" in {
      assertErrorsForValue(fieldName, "0.000", Seq(FormError(fieldName, invalidKey, Seq(Validation.dutyPattern))))
    }
  }

  def assertErrorsForValue(field: String, value: String, expectedErrors: Seq[FormError]): Unit = {
    val result = form.bind(Map(field -> value)).apply(field)
    result.errors mustEqual expectedErrors
  }

}

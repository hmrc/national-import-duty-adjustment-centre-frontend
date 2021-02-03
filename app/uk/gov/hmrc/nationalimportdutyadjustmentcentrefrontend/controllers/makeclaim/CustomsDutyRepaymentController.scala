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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.makeclaim

import javax.inject.{Inject, Singleton}
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc._
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.actions.IdentifierAction
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.DutyPaidFormProvider
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.DutyPaid
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.requests.IdentifierRequest
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.navigation.Navigator
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages.CustomsDutyRepaymentPage
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.services.CacheDataService
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.DutyRepaymentPage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CustomsDutyRepaymentController @Inject() (
  identify: IdentifierAction,
  data: CacheDataService,
  formProvider: DutyPaidFormProvider,
  val controllerComponents: MessagesControllerComponents,
  navigator: Navigator,
  repaymentPage: DutyRepaymentPage
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  private val form = formProvider()

  def onPageLoad(): Action[AnyContent] = identify.async { implicit request =>
    data.getAnswers map { answers =>
      val preparedForm = answers.customsDutyRepayment.fold(form)(form.fill)
      Ok(page(preparedForm))
    }
  }

  def onSubmit(): Action[AnyContent] = identify.async { implicit request =>
    form.bindFromRequest().fold(
      formWithErrors => Future(BadRequest(page(formWithErrors))),
      value =>
        data.updateAnswers(answers => answers.copy(customsDutyRepayment = Some(value))) map {
          updatedAnswers => Redirect(navigator.nextPage(CustomsDutyRepaymentPage, updatedAnswers))
        }
    )
  }

  private def page(form: Form[DutyPaid])(implicit request: IdentifierRequest[_]) =
    repaymentPage(form, routes.CustomsDutyRepaymentController.onSubmit(), "customsDutyPaid")

}

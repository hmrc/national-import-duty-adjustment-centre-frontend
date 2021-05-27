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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.amendNdrc

import javax.inject.{Inject, Singleton}
import play.api.i18n.I18nSupport
import play.api.mvc._
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.Navigation
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.actions.IdentifierAction
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.amendNdrcClaim.FurtherInformationFormProvider
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.amendNdrcClaim.AmendNdrcAnswers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.navigation.AmendNdrcNavigator
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages.{FurtherInformationPage, Page}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.services.CacheDataService
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.amendNdrcClaim.FurtherInformationView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import scala.concurrent.ExecutionContext

@Singleton
class FurtherInformationController @Inject()(
  identify: IdentifierAction,
  data: CacheDataService,
  formProvider: FurtherInformationFormProvider,
  val controllerComponents: MessagesControllerComponents,
  val navigator: AmendNdrcNavigator,
  furtherInformationView: FurtherInformationView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport with Navigation[AmendNdrcAnswers] {

  override val page: Page = FurtherInformationPage

  private val form = formProvider()

  def onPageLoad(): Action[AnyContent] = identify.async { implicit request =>
    data.getAmendNdrcAnswers map { answers =>
      val preparedForm = answers.furtherInformation.fold(form)(form.fill)
      Ok(furtherInformationView(preparedForm, backLink(answers)))
    }
  }

  def onSubmit(): Action[AnyContent] = identify.async { implicit request =>
    form.bindFromRequest().fold(
      formWithErrors =>
        data.getAmendNdrcAnswers map { answers =>
          BadRequest(furtherInformationView(formWithErrors, backLink(answers)))
        },
      value =>
        data.updateAmendNdrcAnswers(answers => answers.copy(furtherInformation = Some(value))) map {
          updatedAnswers => Redirect(nextPage(updatedAnswers))
        }
    )
  }

}

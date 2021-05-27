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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.amendNdrcClaim.AmendCaseResponseTypeFormProvider
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.amendNdrcClaim.{AmendCaseResponseType, AmendNdrcAnswers}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.navigation.AmendNdrcNavigator
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages.{AmendCaseResponseTypePage, Page, ReclaimDutyTypePage}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.services.CacheDataService
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.amendNdrcClaim.AmendCaseResponseTypeView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import scala.concurrent.ExecutionContext

@Singleton
class AmendCaseResponseTypeController @Inject()(
                                           identify: IdentifierAction,
                                           data: CacheDataService,
                                           formProvider: AmendCaseResponseTypeFormProvider,
                                           val controllerComponents: MessagesControllerComponents,
                                           val navigator: AmendNdrcNavigator,
                                           view: AmendCaseResponseTypeView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport with Navigation[AmendNdrcAnswers] {

  override val page: Page = AmendCaseResponseTypePage

  private val form = formProvider()

  def onPageLoad(): Action[AnyContent] = identify.async { implicit request =>
    data.getAmendNdrcAnswers map { answers =>
      val preparedForm = form.fill(answers.amendCaseResponseType)
      Ok(view(preparedForm, backLink(answers)))
    }
  }

  def onSubmit(): Action[AnyContent] = identify.async { implicit request =>
    form.bindFromRequest().fold(
      formWithErrors =>
        data.getAmendNdrcAnswers map { answers => BadRequest(view(formWithErrors, backLink(answers))) },
      value =>
        data.updateAmendNdrcAnswers(answers => updateAnswers(value, answers)) map {
          updatedAnswers => Redirect(nextPage(updatedAnswers))
        }
    )
  }

  private def updateAnswers(dutyTypes: Set[AmendCaseResponseType], answers: AmendNdrcAnswers) =
    answers.copy(
      amendCaseResponseType = dutyTypes
    )

}

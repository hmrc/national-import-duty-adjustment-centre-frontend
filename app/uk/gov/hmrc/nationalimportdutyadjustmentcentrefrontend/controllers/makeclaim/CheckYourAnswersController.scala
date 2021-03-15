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
import play.api.i18n.I18nSupport
import play.api.mvc._
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.Navigation
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.actions.IdentifierAction
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.{Claim, CreateAnswers}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.exceptions.MissingAnswersException
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.navigation.CreateNavigator
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages.{CheckYourAnswersPage, Page}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.services.{CacheDataService, ClaimService}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.CheckYourAnswersView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.ExecutionContext

@Singleton
class CheckYourAnswersController @Inject() (
  mcc: MessagesControllerComponents,
  identify: IdentifierAction,
  data: CacheDataService,
  service: ClaimService,
  val navigator: CreateNavigator,
  checkYourAnswersView: CheckYourAnswersView
)(implicit ec: ExecutionContext)
    extends FrontendController(mcc) with I18nSupport with Navigation[CreateAnswers] {

  override val page: Page = CheckYourAnswersPage

  def onPageLoad(): Action[AnyContent] = identify.async { implicit request =>
    data.getCreateAnswers flatMap { answers =>
      val claim = Claim(answers)
      data.updateCreateAnswers(answers => answers.copy(changePage = None)) map { ans =>
        Ok(checkYourAnswersView(claim, backLink(ans)))
      }
    } recover {
      case missing: MissingAnswersException =>
        Redirect(navigator.gotoPage(missing.getMissingPage))
    }
  }

  def onChange(page: String): Action[AnyContent] = identify.async { implicit request =>
    data.updateCreateAnswers(answers => answers.copy(changePage = Some(page))) map { _ =>
      Redirect(navigator.gotoPage(page))
    }
  }

  def onSubmit(): Action[AnyContent] = identify.async { implicit request =>
    data.getCreateAnswers flatMap { answers =>
      val claim = Claim(answers)
      service.submitClaim(claim) flatMap {
        case response if response.error.isDefined => throw new Exception(s"Error - ${response.error}")
        case response =>
          data.storeCreateResponse(response) map {
            _ => Redirect(nextPage(answers))
          }
      }
    } recover {
      case missing: MissingAnswersException =>
        Redirect(navigator.gotoPage(missing.getMissingPage))
    }

  }

}

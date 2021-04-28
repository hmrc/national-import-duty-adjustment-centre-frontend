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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.config.AppConfig
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.Navigation
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.actions.IdentifierAction
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.create.AddressFormProvider
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.{Address, CreateAnswers}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.navigation.CreateNavigator
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages.{AddressPage, Page}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.services.{AddressLookupService, CacheDataService}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.AddressView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AddressController @Inject() (
  identify: IdentifierAction,
  data: CacheDataService,
  formProvider: AddressFormProvider,
  addressLookupService: AddressLookupService,
  val controllerComponents: MessagesControllerComponents,
  val navigator: CreateNavigator,
  addressView: AddressView
)(implicit ec: ExecutionContext, implicit val appConfig: AppConfig)
    extends FrontendBaseController with I18nSupport with Navigation[CreateAnswers] {

  override val page: Page = AddressPage

  private val form = formProvider()

  def onPageLoad(): Action[AnyContent] = identify.async { implicit request =>
    data.getCreateAnswers map { answers =>
      answers.claimantAddress match {
        case Some(address) =>
          val preparedForm = answers.claimantAddress.fold(form)(form.fill)
          Ok(addressView(preparedForm, backLink(answers)))
        case _ =>
          Redirect(controllers.makeclaim.routes.AddressController.onChange())
      }
    }
  }

  def onSubmit(): Action[AnyContent] = identify.async { implicit request =>
    form.bindFromRequest().fold(
      formWithErrors =>
        data.getCreateAnswers map { answers => BadRequest(addressView(formWithErrors, backLink(answers))) },
      value =>
        data.getCreateAnswers flatMap { existingAnswers =>
          val updatedAddress =
            if (existingAnswers.claimantAddress.contains(value)) value
            else value.copy(auditRef = None)
          data.updateCreateAnswers(answers => answers.copy(claimantAddress = Some(updatedAddress))) map {
            updatedAnswers => Redirect(nextPage(updatedAnswers))
          }
        }
    )
  }

  def onChange(): Action[AnyContent] = identify.async { implicit request =>
    addressLookupService.initialiseJourney(
      appConfig.yourAddressLookupCallbackUrl,
      appConfig.loginContinueUrl,
      appConfig.signOutUrl,
      "address.title",
      "address.hint"
    ) map {
      response => Redirect(response.redirectUrl)
    }
  }

  def onUpdate(id: String): Action[AnyContent] = identify.async { implicit request =>
    data.getCreateAnswers flatMap { answers =>
      addressLookupService.retrieveAddress(id) flatMap { confirmedAddress =>
        val el = confirmedAddress.extractAddressLines()
        val updatedAddress = Address(
          el._1,
          el._2,
          el._3,
          el._4,
          confirmedAddress.address.postcode.getOrElse(""),
          Some(confirmedAddress.auditRef)
        )
        // Address Lookup Service may return an address that is incompatible with NIDAC, so validate it again
        val formWithAddress = form.fillAndValidate(updatedAddress)
        if (formWithAddress.hasErrors)
          Future.successful(BadRequest(addressView(formWithAddress, backLink(answers))))
        else
          data.updateCreateAnswers(answers => answers.copy(claimantAddress = Some(updatedAddress))) map {
            _ => Redirect(nextPage(answers))
          }
      }
    }
  }

}

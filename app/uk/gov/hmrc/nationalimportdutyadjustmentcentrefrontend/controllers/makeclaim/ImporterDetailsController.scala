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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.create.ImporterDetailsFormProvider
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.{
  AuditableImporterContactDetails,
  CreateAnswers,
  ImporterContactDetails
}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.exceptions.MissingAddressException
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.navigation.CreateNavigator
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages.{ImporterContactDetailsPage, Page}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.services.{AddressLookupService, CacheDataService}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.ImporterDetailsView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import scala.concurrent.ExecutionContext

@Singleton
class ImporterDetailsController @Inject() (
  identify: IdentifierAction,
  data: CacheDataService,
  formProvider: ImporterDetailsFormProvider,
  addressLookupService: AddressLookupService,
  val controllerComponents: MessagesControllerComponents,
  val navigator: CreateNavigator,
  detailsView: ImporterDetailsView
)(implicit ec: ExecutionContext, implicit val appConfig: AppConfig)
    extends FrontendBaseController with I18nSupport with Navigation[CreateAnswers] {

  override val page: Page = ImporterContactDetailsPage

  private val form = formProvider()

  def onPageLoad(): Action[AnyContent] = identify.async { implicit request =>
    data.getCreateAnswers map { answers =>
      answers.importerContactDetails match {
        case Some(address) =>
          val preparedForm = answers.importerContactDetails.map(cd => cd.importerContactdetails).fold(form)(form.fill)
          Ok(detailsView(preparedForm, backLink(answers)))
        case _ =>
          Redirect(controllers.makeclaim.routes.ImporterDetailsController.onChange())
      }
    }
  }

  def onSubmit(): Action[AnyContent] = identify.async { implicit request =>
    form.bindFromRequest().fold(
      formWithErrors =>
        data.getCreateAnswers map { answers => BadRequest(detailsView(formWithErrors, backLink(answers))) },
      value =>
        data.updateCreateAnswers { answers =>
          answers.importerContactDetails match {
            case Some(existing) =>
              answers.copy(importerContactDetails = Some(AuditableImporterContactDetails(value, existing.auditRef)))
            case None => throw new MissingAddressException
          }
        } map {
          updatedAnswers => Redirect(nextPage(updatedAnswers))
        }
    )
  }

  def onChange(): Action[AnyContent] = identify.async { implicit request =>
    addressLookupService.initialiseJourney(
      appConfig.importerAddressLookupCallbackUrl,
      appConfig.loginContinueUrl,
      appConfig.signOutUrl,
      "importer-details.title",
      "importer-details.hint"
    ) map {
      response => Redirect(response.redirectUrl)
    }
  }

  def onUpdate(id: String): Action[AnyContent] = identify.async { implicit request =>
    data.getCreateAnswers flatMap { answers =>
      addressLookupService.retrieveAddress(id) flatMap { confirmedAddress =>
        val el = confirmedAddress.extractAddressLines()
        val updatedAddress =
          ImporterContactDetails(el._1, el._2, el._3, el._4, confirmedAddress.address.postcode.getOrElse(""))
        val auditableImporterContactDetails = AuditableImporterContactDetails(updatedAddress, confirmedAddress.auditRef)
        data.updateCreateAnswers(
          answers => answers.copy(importerContactDetails = Some(auditableImporterContactDetails))
        ) map {
          _ => Redirect(nextPage(answers))
        }
      }
    }
  }

}

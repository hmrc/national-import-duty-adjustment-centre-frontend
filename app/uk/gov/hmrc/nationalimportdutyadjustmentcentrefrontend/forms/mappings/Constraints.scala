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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.mappings

import play.api.data.validation.{Constraint, Invalid, Valid}
import Implicits.SanitizedString
import java.time.LocalDate

import play.api.data.{FieldMapping, FormError}
import play.api.data.Forms.of
import play.api.data.format.Formatter

import scala.util.{Success, Try}

trait Constraints {

  protected def firstError[A](constraints: Constraint[A]*): Constraint[A] =
    Constraint {
      input =>
        constraints
          .map(_.apply(input))
          .find(_ != Valid)
          .getOrElse(Valid)
    }

  protected def postcodeLength(missingKey: String, errorKey: String): Constraint[String] =
    Constraint {
      case str if str.stripExternalAndReduceInternalSpaces.length == 0 =>
        Invalid(missingKey)
      case str if str.stripExternalAndReduceInternalSpaces.length > 8 =>
        Invalid(errorKey)
      case str if str.stripExternalAndReduceInternalSpaces.length < 5 =>
        Invalid(errorKey)
      case _ =>
        Valid
    }

  protected def regexp(regex: String, errorKey: String, transform: String => String = x => x): Constraint[String] =
    Constraint {
      case str if transform(str).matches(regex) =>
        Valid
      case _ =>
        Invalid(errorKey, regex)
    }

  protected def maxLength(maximum: Int, errorKey: String, transform: String => String = x => x): Constraint[String] =
    Constraint {
      case str if transform(str).length <= maximum =>
        Valid
      case _ =>
        Invalid(errorKey, maximum)
    }

  protected def minLength(minimum: Int, errorKey: String, transform: String => String = x => x): Constraint[String] =
    Constraint {
      case str if transform(str).length >= minimum =>
        Valid
      case _ =>
        Invalid(errorKey, minimum)
    }

  protected def nonEmptySet(errorKey: String): Constraint[Set[_]] =
    Constraint {
      case set if set.nonEmpty =>
        Valid
      case _ =>
        Invalid(errorKey)
    }

  protected def maxDate(maximum: LocalDate, errorKey: String, args: Any*): Constraint[LocalDate] =
    Constraint {
      case date if date.isAfter(maximum) =>
        Invalid(errorKey, args: _*)
      case _ =>
        Valid
    }

  protected def minDate(minimum: LocalDate, errorKey: String, args: Any*): Constraint[LocalDate] =
    Constraint {
      case date if date.isBefore(minimum) =>
        Invalid(errorKey, args: _*)
      case _ =>
        Valid
    }

  protected def greaterThanZero(errorKey: String): Constraint[String] =
    Constraint {
      input =>
        Try(BigDecimal(input)) match {
          case Success(value) if value > 0 => Valid
          case _                           => Invalid(errorKey)
        }
    }

  protected def greaterThanOrEqualZero(errorKey: String): Constraint[String] =
    Constraint {
      input =>
        Try(BigDecimal(input)) match {
          case Success(value) if value >= 0 => Valid
          case _                            => Invalid(errorKey)
        }
    }

  protected def startsWith(errorKey: String): Constraint[String] =
    Constraint {
      case str if str.trim.toUpperCase.startsWith("NDRC")  =>
        Valid
      case _ =>
        Invalid(errorKey)
    }

  protected def textNoSpaces(errorKey: String = "error.required"): FieldMapping[String] =
    of(stringFormatterNoSpaces(errorKey))

  private[mappings] def stringFormatterNoSpaces(errorKey: String): Formatter[String] = new Formatter[String] {

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], String] =
      data.get(key) match {
        case None | Some("") => Left(Seq(FormError(key, errorKey)))
        case Some(s) => Right(trimWhitespace(s))
      }

    override def unbind(key: String, value: String): Map[String, String] =
      Map(key -> trimWhitespace(value))
  }

  def trimWhitespace(string: String): String = string.split("\\s+").mkString
}

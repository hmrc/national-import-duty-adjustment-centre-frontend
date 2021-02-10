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

import java.time.LocalDate

import play.api.data.FormError
import play.api.data.format.Formatter
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.mappings.DateFields.{dayKey, monthKey, yearKey}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.Enumerable

import scala.util.control.Exception.nonFatalCatch
import scala.util.{Failure, Success, Try}

trait Formatters {

  private[mappings] def stringFormatter(errorKey: String): Formatter[String] = new Formatter[String] {

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], String] =
      data.get(key) match {
        case None | Some("") => Left(Seq(FormError(key, errorKey)))
        case Some(s)         => Right(s)
      }

    override def unbind(key: String, value: String): Map[String, String] =
      Map(key -> value)

  }

  private[mappings] def booleanFormatter(requiredKey: String, invalidKey: String): Formatter[Boolean] =
    new Formatter[Boolean] {

      private val baseFormatter = stringFormatter(requiredKey)

      override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Boolean] =
        baseFormatter
          .bind(key, data)
          .right.flatMap {
            case "true" | "yes" => Right(true)
            case "false" | "no" => Right(false)
            case _              => Left(Seq(FormError(key, invalidKey)))
          }

      def unbind(key: String, value: Boolean) = Map(key -> value.toString)
    }

  private[mappings] def intFormatter(
    requiredKey: String,
    wholeNumberKey: String = "error.number.wholeNumber",
    nonNumericKey: String = "error.number.nonNumeric",
    invalidValueKey: String = "error.number.invalidValue",
    args: Seq[String] = Seq.empty,
    valueRange: (Int, Int) = (Int.MinValue, Int.MaxValue)
  ): Formatter[Int] =
    new Formatter[Int] {

      val decimalRegexp = """^-?(\d*\.\d*)$"""

      private val baseFormatter = stringFormatter(requiredKey)

      override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Int] = {
        def toInt(s: String): Either[Seq[FormError], Int] = {
          val intValue: Either[Seq[FormError], Int] = nonFatalCatch
            .either(s.toInt)
            .left.map(_ => Seq(FormError(key, nonNumericKey, args)))

          intValue.right.flatMap {
            case i if i < valueRange._1 || i > valueRange._2 => Left(Seq(FormError(key, invalidValueKey, args)))
            case i                                           => Right(i)
          }
        }

        baseFormatter
          .bind(key, data)
          .right.map(_.replace(",", ""))
          .right.flatMap {
            case s if s.matches(decimalRegexp) =>
              Left(Seq(FormError(key, wholeNumberKey, args)))
            case s => toInt(s)
          }
      }

      override def unbind(key: String, value: Int): Map[String, String] =
        baseFormatter.unbind(key, value.toString)

    }

  private[mappings] def enumerableFormatter[A](requiredKey: String, invalidKey: String)(implicit
    ev: Enumerable[A]
  ): Formatter[A] =
    new Formatter[A] {

      private val baseFormatter = stringFormatter(requiredKey)

      override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], A] =
        baseFormatter.bind(key, data).right.flatMap {
          str =>
            ev.withName(str).map(Right.apply).getOrElse(Left(Seq(FormError(key, invalidKey))))
        }

      override def unbind(key: String, value: A): Map[String, String] =
        baseFormatter.unbind(key, value.toString)

    }

  private[mappings] def localDateFormatter(
    invalidKey: String,
    requiredKey: String,
    args: Seq[String] = Seq.empty
  ): Formatter[LocalDate] =
    new Formatter[LocalDate] {

      private val dayFormatter =
        intFormatter(
          requiredKey = s"$requiredKey.$dayKey",
          args = args,
          valueRange = (1, 31),
          invalidValueKey = "date.error.day"
        )

      private val monthFormatter =
        intFormatter(
          requiredKey = s"$requiredKey.$monthKey",
          args = args,
          valueRange = (1, 12),
          invalidValueKey = "date.error.month"
        )

      private val yearFormatter =
        intFormatter(requiredKey = s"$requiredKey.$yearKey", args = args)

      private def toDate(key: String, day: Int, month: Int, year: Int): Either[Seq[FormError], LocalDate] =
        Try(LocalDate.of(year, month, day)) match {
          case Success(date) =>
            Right(date)
          case Failure(_) =>
            Left(Seq(FormError(key, invalidKey, args)))
        }

      private def formatDate(key: String, data: Map[String, String]): Either[Seq[FormError], LocalDate] =
        for {
          day   <- dayFormatter.bind(key, data).right
          month <- monthFormatter.bind(s"$key.$monthKey", data).right
          year  <- yearFormatter.bind(s"$key.$yearKey", data).right
          date  <- toDate(key, day, month, year).right
        } yield date

      override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], LocalDate] = {

        val fields = List(key, s"$key.$monthKey", s"$key.$yearKey").map {
          field =>
            field -> data.get(field).filter(_.nonEmpty)
        }.toMap

        val missingFields: List[String] = fields
          .withFilter(_._2.isEmpty)
          .map(_._1)
          .toList

        def multipleMissing =
          s"$requiredKey.${missingFields.map { field =>
            if (field == key) dayKey else field.replace(s"$key.", "")
          }.mkString(".")}"

        missingFields.size match {
          case 3 => Left(List(FormError(key, requiredKey, args)))
          case 2 =>
            Left(List(FormError(missingFields.head, multipleMissing, args)))
          case _ => formatDate(key, data)
        }
      }

      override def unbind(key: String, value: LocalDate): Map[String, String] =
        Map(
          s"$key"           -> value.getDayOfMonth.toString,
          s"$key.$monthKey" -> value.getMonthValue.toString,
          s"$key.$yearKey"  -> value.getYear.toString
        )

    }

}

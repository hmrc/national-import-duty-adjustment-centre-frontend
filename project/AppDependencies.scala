import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-28" % "4.2.0",
    "uk.gov.hmrc"       %% "play-frontend-hmrc"         % "0.59.0-play-28",
    "uk.gov.hmrc"       %% "play-frontend-govuk"        % "0.71.0-play-28",
    "uk.gov.hmrc"       %% "play-language"              % "4.12.0-play-28",
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28"         % "0.49.0"
  )

  val test = Seq(
    "uk.gov.hmrc"            %% "bootstrap-test-play-28" % "4.2.0"   % Test,
    "org.scalatest"          %% "scalatest"              % "3.2.3"   % Test,
    "org.scalatestplus"      %% "mockito-3-4"            % "3.2.3.0" % Test,
    "org.jsoup"               % "jsoup"                  % "1.13.1"  % Test,
    "com.typesafe.play"      %% "play-test"              % current   % Test,
    "com.eclipsesource"      %% "play-json-schema-validator" % "0.9.5" % Test,
    "com.vladsch.flexmark"    % "flexmark-all"           % "0.36.8"  % "test, it",
    "org.scalatestplus.play" %% "scalatestplus-play"     % "5.1.0"   % "test, it",
    "org.scalatestplus"      %% "scalacheck-1-15"        % "3.2.3.0" % "test"
  )

}

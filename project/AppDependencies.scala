import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc" %% "bootstrap-frontend-play-27" % "3.2.0",
    "uk.gov.hmrc" %% "play-frontend-hmrc"         % "0.34.0-play-27",
    "uk.gov.hmrc" %% "play-frontend-govuk"        % "0.56.0-play-27",
    "uk.gov.hmrc" %% "play-language"              % "4.5.0-play-27"
  )

  val test = Seq(
    "uk.gov.hmrc"            %% "bootstrap-test-play-27" % "3.2.0"   % Test,
    "org.scalatest"          %% "scalatest"              % "3.2.3"   % Test,
    "org.scalatestplus.play" %% "scalatestplus-play"     % "3.1.2"   % Test,
    "org.scalatestplus"      %% "mockito-3-4"            % "3.2.3.0" % Test,
    "org.jsoup"               % "jsoup"                  % "1.13.1"  % Test,
    "com.typesafe.play"      %% "play-test"              % current   % Test,
    "com.vladsch.flexmark"    % "flexmark-all"           % "0.36.8"  % "test, it",
    "org.scalatestplus.play" %% "scalatestplus-play"     % "4.0.3"   % "test, it"
  )

}

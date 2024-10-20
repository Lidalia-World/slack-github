package slackgithub.awslambdaruntime.model

import kotlin.String
import kotlin.collections.List

data class ErrorRequest(
  val errorMessage: String?,
  val errorType: String?,
  val stackTrace: List<String>?,
)

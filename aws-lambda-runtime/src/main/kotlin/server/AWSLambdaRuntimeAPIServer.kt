package slackgithub.awslambdaruntime.server

import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status.Companion.INTERNAL_SERVER_ERROR
import org.http4k.filter.ServerFilters
import org.http4k.routing.routes
import slackgithub.awslambdaruntime.server.endpoints.runtime.initd.error.postRuntimeInitError
import slackgithub.awslambdaruntime.server.endpoints.runtime.invocation.awsrequestid.error.postRuntimeInvocationAwsRequestIdError
import slackgithub.awslambdaruntime.server.endpoints.runtime.invocation.awsrequestid.response.postRuntimeInvocationAwsRequestIdResponse
import slackgithub.awslambdaruntime.server.endpoints.runtime.invocation.next.getRuntimeInvocationNext
import java.io.InterruptedIOException

object AWSLambdaRuntimeAPIServer {
  operator fun invoke(): HttpHandler = ServerFilters.CatchAll(
    onError = {
      when (it) {
        is InterruptedException, is InterruptedIOException -> throw it
        is Exception, is NotImplementedError, is StackOverflowError -> handleGracefully(it)
        else -> throw it
      }
    },
  )(
    routes(
      postRuntimeInitError(),
      getRuntimeInvocationNext(),
      postRuntimeInvocationAwsRequestIdResponse(),
      postRuntimeInvocationAwsRequestIdError(),
    ),
  )

  private fun handleGracefully(e: Throwable): Response {
    e.printStackTrace()
    return Response(INTERNAL_SERVER_ERROR).body("Request failed\n")
  }
}

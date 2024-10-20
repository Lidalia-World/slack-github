package slackgithub.awslambdaruntime.server

import org.http4k.core.HttpHandler
import org.http4k.routing.routes
import slackgithub.awslambdaruntime.server.endpoints.runtime.initd.error.postRuntimeInitError
import slackgithub.awslambdaruntime.server.endpoints.runtime.invocation.awsrequestid.error.postRuntimeInvocationAwsRequestIdError
import slackgithub.awslambdaruntime.server.endpoints.runtime.invocation.awsrequestid.response.postRuntimeInvocationAwsRequestIdResponse
import slackgithub.awslambdaruntime.server.endpoints.runtime.invocation.next.getRuntimeInvocationNext

object AWSLambdaRuntimeAPIServer {
  operator fun invoke(): HttpHandler = routes(
    postRuntimeInitError(),
    getRuntimeInvocationNext(),
    postRuntimeInvocationAwsRequestIdResponse(),
    postRuntimeInvocationAwsRequestIdError(),
  )
}

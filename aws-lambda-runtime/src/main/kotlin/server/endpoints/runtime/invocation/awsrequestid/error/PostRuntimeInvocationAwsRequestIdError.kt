package slackgithub.awslambdaruntime.server.endpoints.runtime.invocation.awsrequestid.error

import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.format.Moshi.auto
import org.http4k.lens.Header
import org.http4k.lens.Path
import org.http4k.lens.string
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import slackgithub.awslambdaruntime.model.ErrorResponse
import slackgithub.awslambdaruntime.model.PostRuntimeInvocationAwsRequestIdErrorRequest
import slackgithub.awslambdaruntime.model.StatusResponse

/**
 * Runtime makes this request in order to submit an error response. It can be either a function
 * error, or a runtime error. Error will be served in response to the invoke.
 *
 * Request:
 * 	* / *
 * Response:
 * 	application/json
 * 		202 Accepted
 * 		400 Bad Request
 * 		403 Forbidden
 * 		500 Container error. Non-recoverable state. Runtime should exit promptly.
 */
fun postRuntimeInvocationAwsRequestIdError(): RoutingHttpHandler {
  val postRuntimeInvocationAwsRequestIdErrorRequestLens =
    Body.auto<PostRuntimeInvocationAwsRequestIdErrorRequest>().toLens()
  val statusResponseLens = Body.auto<StatusResponse>().toLens()
  val errorResponseLens = Body.auto<ErrorResponse>().toLens()
  val awsRequestIdLens = Path.string().of("AwsRequestId")
  val lambdaRuntimeFunctionErrorTypeLens = Header.string().optional(
    "LambdaRuntimeFunctionErrorType",
  )

  return "/runtime/invocation/{AwsRequestId}/error" bind Method.POST to { req: Request ->
    val awsRequestId = awsRequestIdLens(req)
    val lambdaRuntimeFunctionErrorType = lambdaRuntimeFunctionErrorTypeLens(req)
    val postRuntimeInvocationAwsRequestIdErrorRequest =
      postRuntimeInvocationAwsRequestIdErrorRequestLens(req)
    Response(Status.OK)
      .with(statusResponseLens of TODO())
  }
}

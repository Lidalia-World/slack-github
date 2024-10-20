package slackgithub.awslambdaruntime.server.endpoints.runtime.invocation.awsrequestid.response

import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.format.Moshi.auto
import org.http4k.lens.Path
import org.http4k.lens.string
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import slackgithub.awslambdaruntime.model.ErrorResponse
import slackgithub.awslambdaruntime.model.PostRuntimeInvocationAwsRequestIdResponseRequest
import slackgithub.awslambdaruntime.model.StatusResponse

/**
 * Runtime makes this request in order to submit a response.
 * Request:
 * 	* / *
 * Response:
 * 	application/json
 * 		202 Accepted
 * 		400 Bad Request
 * 		403 Forbidden
 * 		413 Payload Too Large
 * 		500 Container error. Non-recoverable state. Runtime should exit promptly.
 */
fun postRuntimeInvocationAwsRequestIdResponse(): RoutingHttpHandler {
  val postRuntimeInvocationAwsRequestIdResponseRequestLens =
    Body.auto<PostRuntimeInvocationAwsRequestIdResponseRequest>().toLens()
  val statusResponseLens = Body.auto<StatusResponse>().toLens()
  val errorResponseLens = Body.auto<ErrorResponse>().toLens()
  val awsRequestIdLens = Path.string().of("AwsRequestId")

  return "/runtime/invocation/{AwsRequestId}/response" bind Method.POST to { req: Request ->
    val awsRequestId = awsRequestIdLens(req)
    val postRuntimeInvocationAwsRequestIdResponseRequest =
      postRuntimeInvocationAwsRequestIdResponseRequestLens(req)
    Response(Status.OK)
      .with(statusResponseLens of TODO())
  }
}

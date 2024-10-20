package slackgithub.awslambdaruntime.server.endpoints.runtime.initd.error

import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.format.Moshi.auto
import org.http4k.lens.Header
import org.http4k.lens.string
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import slackgithub.awslambdaruntime.model.ErrorResponse
import slackgithub.awslambdaruntime.model.PostRuntimeInitErrorRequest
import slackgithub.awslambdaruntime.model.StatusResponse

/**
 * Non-recoverable initialization error. Runtime should exit after reporting the error. Error will
 * be served in response to the first invoke.
 *
 * Request:
 * 	* / *
 * Response:
 * 	application/json
 * 		202 Accepted
 * 		403 Forbidden
 * 		500 Container error. Non-recoverable state. Runtime should exit promptly.
 */
fun postRuntimeInitError(): RoutingHttpHandler {
  val postRuntimeInitErrorRequestLens = Body.auto<PostRuntimeInitErrorRequest>().toLens()
  val statusResponseLens = Body.auto<StatusResponse>().toLens()
  val errorResponseLens = Body.auto<ErrorResponse>().toLens()
  val lambdaRuntimeFunctionErrorTypeLens = Header.string().optional(
    "LambdaRuntimeFunctionErrorType",
  )

  return "/runtime/init/error" bind Method.POST to { req: Request ->
    val lambdaRuntimeFunctionErrorType = lambdaRuntimeFunctionErrorTypeLens(req)
    val postRuntimeInitErrorRequest = postRuntimeInitErrorRequestLens(req)
    Response(Status.OK)
      .with(statusResponseLens of TODO())
  }
}

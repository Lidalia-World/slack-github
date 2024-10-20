package slackgithub.awslambdaruntime.server.endpoints.runtime.invocation.next

import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.format.Moshi.auto
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import slackgithub.awslambdaruntime.model.ErrorResponse
import slackgithub.awslambdaruntime.model.EventResponse

/**
 * Runtime makes this HTTP request when it is ready to receive and process a new invoke.
 *
 * Response:
 * 	application/json
 * 		200 This is an iterator-style blocking API call. Response contains event JSON document,
 * specific to the invoking service.
 *
 * 		403 Forbidden
 * 		500 Container error. Non-recoverable state. Runtime should exit promptly.
 */
fun getRuntimeInvocationNext(): RoutingHttpHandler {
  val eventResponseLens = Body.auto<EventResponse>().toLens()
  val errorResponseLens = Body.auto<ErrorResponse>().toLens()

  return "/runtime/invocation/next" bind Method.GET to { req: Request ->
    Response(Status.OK)
      .with(eventResponseLens of TODO())
  }
}

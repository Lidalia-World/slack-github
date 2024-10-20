package slackgithub.app

import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status.Companion.I_M_A_TEAPOT
import org.http4k.core.Status.Companion.OK
import org.http4k.core.then
import org.http4k.filter.ServerFilters
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.http4k.serverless.ApiGatewayV2FnLoader
import org.http4k.serverless.AwsLambdaRuntime
import org.http4k.serverless.asServer

val http4kApp = ServerFilters.CatchAll().then(
  routes(
    "/echo/{message:.*}" bind GET to {
      Response(OK).body(
        it.path("message") ?: "(nothing to echo, use /echo/<message>)",
      )
    },
    "/tea" bind GET to { Response(I_M_A_TEAPOT) },
    "/" bind GET to { Response(OK).body("ok") },
  ),
)

fun main() {
  ApiGatewayV2FnLoader(http4kApp).asServer(AwsLambdaRuntime()).start()
}

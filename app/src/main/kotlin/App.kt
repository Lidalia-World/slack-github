package slackgithub.app

import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.then
import org.http4k.filter.ServerFilters
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.http4k.serverless.ApiGatewayV2FnLoader
import org.http4k.serverless.AwsLambdaRuntime
import org.http4k.serverless.asServer

fun main(args: Array<String>) {
  if (args.contains("--test")) {
    testServer()
  } else {
    runServer()
  }
}

private fun runServer() {
  http4kServer().start()
  println("Lambda runtime running")
}

private fun testServer() {
  http4kServer()
    .start()
    .stop()
  println("Test successful - server was able to start up and shutdown")
}

private fun http4kServer() = ApiGatewayV2FnLoader(http4kApp).asServer(AwsLambdaRuntime()).start()

val http4kApp = ServerFilters.CatchAll().then(
  routes(
    "/echo/{message:.*}" bind GET to {
      Response(OK).body(
        it.path("message") ?: "(nothing to echo, use /echo/<message>)",
      )
    },
    "/" bind GET to { Response(OK).body("ok") },
  ),
)

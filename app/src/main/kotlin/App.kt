package slackgithub.app

import org.http4k.server.SunHttpLoom
import org.http4k.server.asServer
import slackgithub.awslambdaruntime.server.AWSLambdaRuntimeAPIServer

fun main(args: Array<String>) {
  if (args.contains("--test")) {
    testServer()
  } else {
    runServer()
  }
}

private fun runServer() {
  val server = AWSLambdaRuntimeAPIServer().asServer(SunHttpLoom(8000))
    .start()
  println("Server running at http://localhost:${server.port()}")
}

private fun testServer() {
  AWSLambdaRuntimeAPIServer().asServer(SunHttpLoom(8000))
    .start()
    .stop()
  println("Test successful - server was able to start up and shutdown")
}

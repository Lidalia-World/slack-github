package slackgithub.app

import org.http4k.server.SunHttp
import org.http4k.server.asServer
import slackgithub.awslambdaruntime.server.AWSLambdaRuntimeAPIServer

fun main(args: Array<String>) {
  val server = AWSLambdaRuntimeAPIServer().asServer(SunHttp(8000))
  server.start()
  println("Server running at http://localhost:${server.port()}")
  if (args.contains("--test")) {
    server.stop()
    println("Server stopped")
  }
}

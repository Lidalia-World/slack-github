package slackgithub.app

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class MessageUtilsTest {
  @Test
  fun testGetMessage() {
    Assertions.assertEquals("Hello      World!", MessageUtils.getMessage())
  }
}

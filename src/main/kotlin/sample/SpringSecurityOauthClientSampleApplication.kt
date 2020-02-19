package sample

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringSecurityOauthClientSampleApplication

fun main(args: Array<String>) {
	runApplication<SpringSecurityOauthClientSampleApplication>(*args)
}

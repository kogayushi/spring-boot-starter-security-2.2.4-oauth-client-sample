package sample

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping


@Controller
class SampleController {

    @GetMapping("/")
    fun hello(
        @AuthenticationPrincipal oidcUser: OidcUser,
        model: Model
    ): String {
        model.addAttribute("name", oidcUser.subject)
        return "index"
    }
}

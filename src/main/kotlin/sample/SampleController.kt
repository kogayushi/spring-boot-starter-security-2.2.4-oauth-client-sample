package sample

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping


@Controller
class SampleController {

    @GetMapping("/")
    fun hello(
        @AuthenticationPrincipal oAuth2User: OAuth2User,
        model: Model
    ): String {
        model.addAttribute("name", oAuth2User.name)
        return "index"
    }
}

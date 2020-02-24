package sample

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping


@Controller
class SampleController {

    @GetMapping("/")
    fun hello(
        @AuthenticationPrincipal oAuth2User: CustomOAuth2User,
        model: Model
    ): String {
        val myUserPrincipal = oAuth2User.toMyUserPrincipal()
        model.addAttribute("provider", myUserPrincipal.provider)
        model.addAttribute("name", myUserPrincipal.name)
        return "index"
    }
}

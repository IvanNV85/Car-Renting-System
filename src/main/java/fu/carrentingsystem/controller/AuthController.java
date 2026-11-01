package fu.carrentingsystem.controller;

import fu.carrentingsystem.dto.request.RegisterRequest;
import fu.carrentingsystem.service.AccountService;
import fu.carrentingsystem.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AccountService accountService;
    private final CustomerService customerService;

    @GetMapping("/login")
    public String showLoginForm(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            return isAdmin ? "redirect:/admin/cars" : "redirect:/";
        }

        if (error != null) {
            model.addAttribute("errorMessage", "Email/Tên đăng nhập hoặc mật khẩu không chính xác.");
        }
        if (logout != null) {
            model.addAttribute("infoMessage", "Bạn đã đăng xuất thành công khỏi hệ thống.");
        }

        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            return isAdmin ? "redirect:/admin/cars" : "redirect:/";
        }

        if (!model.containsAttribute("registerRequest")) {
            model.addAttribute("registerRequest", new RegisterRequest());
        }
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(
            @Valid @ModelAttribute("registerRequest") RegisterRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        // Validate password confirmation
        if (request.getPassword() != null && !request.getPassword().equals(request.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.registerRequest", "Mật khẩu xác nhận không trùng khớp.");
        }

        // Validate uniqueness of Account fields
        if (request.getEmail() != null && accountService.existsByEmail(request.getEmail().trim())) {
            bindingResult.rejectValue("email", "error.registerRequest", "Email này đã được đăng ký trong hệ thống.");
        }

        if (request.getAccountName() != null && accountService.existsByAccountName(request.getAccountName().trim())) {
            bindingResult.rejectValue("accountName", "error.registerRequest", "Tên tài khoản này đã tồn tại.");
        }

        // Validate uniqueness of Customer fields
        if (request.getMobile() != null && customerService.existsByMobile(request.getMobile().trim())) {
            bindingResult.rejectValue("mobile", "error.registerRequest", "Số điện thoại đã được đăng ký.");
        }

        if (request.getIdentityCard() != null && customerService.existsByIdentityCard(request.getIdentityCard().trim())) {
            bindingResult.rejectValue("identityCard", "error.registerRequest", "Số CCCD/CMND này đã được sử dụng.");
        }

        if (request.getLicenceNumber() != null && customerService.existsByLicenceNumber(request.getLicenceNumber().trim())) {
            bindingResult.rejectValue("licenceNumber", "error.registerRequest", "Số GPLX này đã được sử dụng.");
        }

        if (bindingResult.hasErrors()) {
            return "register";
        }

        accountService.registerCustomer(request);
        redirectAttributes.addFlashAttribute("successMessage", "Đăng ký tài khoản thành công! Bạn có thể đăng nhập ngay bây giờ.");
        return "redirect:/login";
    }
}

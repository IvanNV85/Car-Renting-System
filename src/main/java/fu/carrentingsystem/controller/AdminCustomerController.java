package fu.carrentingsystem.controller;

import fu.carrentingsystem.dto.request.CustomerUpdateRequest;
import fu.carrentingsystem.dto.response.CustomerResponse;
import fu.carrentingsystem.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/customers")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminCustomerController {

    private final CustomerService customerService;

    @GetMapping
    public String listCustomers(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model) {

        Page<CustomerResponse> customerPage = customerService.getAllCustomers(page, size, keyword);

        model.addAttribute("customers", customerPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", customerPage.getTotalPages());
        model.addAttribute("totalElements", customerPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("activeTab", "customers");

        return "admin/customers/list";
    }

    @GetMapping("/{id}")
    public String viewCustomerDetail(@PathVariable("id") int id, Model model, RedirectAttributes redirectAttributes) {
        try {
            CustomerResponse customer = customerService.getCustomerDetail(id);
            model.addAttribute("customer", customer);
            model.addAttribute("activeTab", "customers");
            return "admin/customers/detail";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/customers";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") int id, Model model, RedirectAttributes redirectAttributes) {
        try {
            CustomerResponse customer = customerService.getCustomerDetail(id);
            if (!model.containsAttribute("customerUpdateRequest")) {
                CustomerUpdateRequest updateRequest = customerService.getCustomerUpdateForm(id);
                model.addAttribute("customerUpdateRequest", updateRequest);
            }
            model.addAttribute("customer", customer);
            model.addAttribute("customerId", id);
            model.addAttribute("activeTab", "customers");
            return "admin/customers/edit";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/customers";
        }
    }

    @PostMapping("/{id}/edit")
    public String processUpdate(
            @PathVariable("id") int id,
            @Valid @ModelAttribute("customerUpdateRequest") CustomerUpdateRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            CustomerResponse customer = customerService.getCustomerDetail(id);
            model.addAttribute("customer", customer);
            model.addAttribute("customerId", id);
            model.addAttribute("activeTab", "customers");
            return "admin/customers/edit";
        }

        try {
            customerService.updateCustomer(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin khách hàng thành công!");
            return "redirect:/admin/customers/" + id;
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            CustomerResponse customer = customerService.getCustomerDetail(id);
            model.addAttribute("customer", customer);
            model.addAttribute("customerId", id);
            model.addAttribute("activeTab", "customers");
            return "admin/customers/edit";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteCustomer(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        try {
            customerService.deleteCustomer(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa khách hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/customers";
    }
}


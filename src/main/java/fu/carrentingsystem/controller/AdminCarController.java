package fu.carrentingsystem.controller;

import fu.carrentingsystem.constant.CarStatus;
import fu.carrentingsystem.dto.request.CarRequest;
import fu.carrentingsystem.dto.response.CarResponse;
import fu.carrentingsystem.service.CarService;
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
@RequestMapping("/admin/cars")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminCarController {

    private final CarService carService;

    @GetMapping
    public String listCars(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) CarStatus status,
            Model model) {

        Page<CarResponse> carPage = carService.getAllCars(page, size, keyword, status);

        model.addAttribute("cars", carPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", carPage.getTotalPages());
        model.addAttribute("totalElements", carPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("statuses", CarStatus.values());
        model.addAttribute("activeTab", "cars");

        return "admin/cars/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("carRequest")) {
            CarRequest defaultReq = new CarRequest();
            defaultReq.setStatus(CarStatus.AVAILABLE);
            model.addAttribute("carRequest", defaultReq);
        }
        model.addAttribute("producers", carService.getAllProducers());
        model.addAttribute("statuses", CarStatus.values());
        model.addAttribute("isEdit", false);
        model.addAttribute("activeTab", "cars");
        return "admin/cars/form";
    }

    @PostMapping("/new")
    public String processCreate(
            @Valid @ModelAttribute("carRequest") CarRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("producers", carService.getAllProducers());
            model.addAttribute("statuses", CarStatus.values());
            model.addAttribute("isEdit", false);
            model.addAttribute("activeTab", "cars");
            return "admin/cars/form";
        }

        try {
            carService.createCar(request);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm mới xe thành công!");
            return "redirect:/admin/cars";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("producers", carService.getAllProducers());
            model.addAttribute("statuses", CarStatus.values());
            model.addAttribute("isEdit", false);
            model.addAttribute("activeTab", "cars");
            return "admin/cars/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") int id, Model model, RedirectAttributes redirectAttributes) {
        try {
            if (!model.containsAttribute("carRequest")) {
                CarRequest updateForm = carService.getCarUpdateForm(id);
                model.addAttribute("carRequest", updateForm);
            }
            model.addAttribute("carId", id);
            model.addAttribute("producers", carService.getAllProducers());
            model.addAttribute("statuses", CarStatus.values());
            model.addAttribute("isEdit", true);
            model.addAttribute("activeTab", "cars");
            return "admin/cars/form";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/cars";
        }
    }

    @PostMapping("/{id}/edit")
    public String processUpdate(
            @PathVariable("id") int id,
            @Valid @ModelAttribute("carRequest") CarRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("carId", id);
            model.addAttribute("producers", carService.getAllProducers());
            model.addAttribute("statuses", CarStatus.values());
            model.addAttribute("isEdit", true);
            model.addAttribute("activeTab", "cars");
            return "admin/cars/form";
        }

        try {
            carService.updateCar(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin xe thành công!");
            return "redirect:/admin/cars";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("carId", id);
            model.addAttribute("producers", carService.getAllProducers());
            model.addAttribute("statuses", CarStatus.values());
            model.addAttribute("isEdit", true);
            model.addAttribute("activeTab", "cars");
            return "admin/cars/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteCar(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        try {
            carService.deleteCar(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa xe thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/cars";
    }
}


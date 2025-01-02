package com.project.popupmarket.controller;

import com.project.popupmarket.dto.userDto.UserRegisterDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class WebController {

    @GetMapping("main")
    public String index() {
        return "main";
    }

    @GetMapping("rental/list")
    public String rentalList() {
        return "place_list";
    }

    @GetMapping("popup/list")
    public String popupList() {
        return "popup_list";
    }

    @GetMapping("rental/detail/{rentalPlaceSeq}")
    public String rentalDetail(@PathVariable Long rentalPlaceSeq) {
        return "place_detail";
    }

    @GetMapping("popup/detail/{popupStoreSeq}")
    public String popupDetail(@PathVariable Long popupStoreSeq) {
        return "popup_detail";
    }

    @GetMapping("mypage/rental")
    public String mypageRental() {
        return "my_place_list";
    }

    @GetMapping("mypage/rental/add")
    public String mypageRentalAdd() {
        return "my_place_add";
    }

    @GetMapping("mypage/rental/view/{rentalPlaceSeq}")
    public String mypageRentalView(@PathVariable Long rentalPlaceSeq) {
        return "my_place_view";
    }

    @GetMapping("mypage/rental/reservation/{rentalPlaceSeq}")
    public String mypageRentalReservation(@PathVariable Long rentalPlaceSeq) {
        return "my_place_reservation";
    }

    @GetMapping("mypage/popup")
    public String mypagePopup() {
        return "my_popup_list";
    }

    @GetMapping("mypage/popup/add")
    public String mypagePopupAdd() {
        return "my_popup_add";
    }

    @GetMapping("mypage/popup/view/{popupStoreSeq}")
    public String mypagePopupView(@PathVariable Long popupStoreSeq) {
        return "my_popup_view";
    }

    @GetMapping("mypage/popup/invitation/{popupStoreSeq}")
    public String mypagePopupRequest(@PathVariable Long popupStoreSeq) {
        return "my_popup_request";
    }

    @GetMapping("mypage/receipt")
    public String mypageReceipt() {
        return "my_receipt";
    }

    @GetMapping("payment")
    public String payment() {
        return "payment";
    }

    @GetMapping("payment/success")
    public String paymentSuccess() {
        return "payment_success";
    }

    @GetMapping("payment/fail")
    public String paymentFail() {
        return "payment_fail";
    }

    @GetMapping("register")
    public String showRegisterForm(Model model) {
        model.addAttribute("userRegisterDto", new UserRegisterDto());
        return "register";
    }

    @GetMapping("/mypage")
    public String mypage() {
        return "mypage";
    }

    // 로그인 페이지 보여주기
    @GetMapping("login")
    public String showLoginForm() {
        return "Login";
    }
}

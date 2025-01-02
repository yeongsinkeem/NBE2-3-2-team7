package com.project.popupmarket.controller.invitation;

import com.project.popupmarket.dto.invitation.InvitationInfoTO;
import com.project.popupmarket.dto.invitation.InvitationTO;
import com.project.popupmarket.service.invitation.InvitationService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class InvitationController {

    private final InvitationService invitationService;

    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    @PostMapping("/invitation")
    @Operation(summary = "입점 요청 추가")
    public ResponseEntity<String> addInvitation(@RequestBody InvitationTO invitation) {

        boolean flag = invitationService.addInvitation(invitation);

        if (flag) {
            return ResponseEntity.ok("success");
        } else {
            return ResponseEntity.status(500).body("error");
        }
    }

    @GetMapping("/invitation/{popupStoreSeq}")
    public InvitationInfoTO getInvitation(
            @PathVariable("popupStoreSeq") Long popupStoreSeq
    ) {
        return invitationService.getInvitations(popupStoreSeq);
    }

    @DeleteMapping("/invitation")
    @Operation(summary = "입점 요청 삭제")
    public ResponseEntity<String> removeInvitation(@RequestBody InvitationTO invitation) {

        boolean flag = invitationService.removeInvitation(invitation);

        if (flag) {
            return ResponseEntity.ok("success");
        } else {
            return ResponseEntity.status(500).body("error");
        }
    }
}

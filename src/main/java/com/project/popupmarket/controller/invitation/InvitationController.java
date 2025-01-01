package com.project.popupmarket.controller.invitation;

import com.project.popupmarket.dto.invitation.InvitationTO;
import com.project.popupmarket.service.invitation.InvitationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
public class InvitationController {

    private final InvitationService invitationService;

    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    @PostMapping("/invitation")
    public ResponseEntity<String> addInvitation(@RequestBody InvitationTO invitation) {

        boolean flag = invitationService.addInvitation(invitation);

        if (flag) {
            return ResponseEntity.ok("success");
        } else {
            return ResponseEntity.status(500).body("error");
        }
    }

    // 임대지 기능과 병합 이후 진행
//    @GetMapping("/invitation/{popupStoreSeq}")
//    public ResponseEntity<List<InvitationInfoTO>> getInvitation(
//            @PathVariable("popupStoreSeq") Long popupStoreSeq
//    ) {
//        List<InvitationInfoTO> lists = invitationService.getInvitations(popupStoreSeq);
//
//        if(lists.isEmpty()) {
//            return ResponseEntity.status(404).build();
//        } else {
//            return ResponseEntity.ok(lists);
//        }
//    }

    @DeleteMapping("/invitation")
    public ResponseEntity<String> removeInvitation(@RequestBody InvitationTO invitation) {

        boolean flag = invitationService.removeInvitation(invitation);

        if (flag) {
            return ResponseEntity.ok("success");
        } else {
            return ResponseEntity.status(500).body("error");
        }
    }
}

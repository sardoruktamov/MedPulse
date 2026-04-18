package api.medpulse.uz.controller;

import api.medpulse.uz.dto.comment.CommentDTO;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

@RestController
public class CommentController {

    // 1. Dastlabki 10 ta izohni olish (Oddiy HTTP GET)
    @GetMapping("/api/v1/comments/post/{postId}")
    public ResponseEntity<Page<CommentDTO>> getPostComments(
            @PathVariable String postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Bu yerda CommentService orqali bazadan eng yangi 10 ta izohni olasiz
        // return ResponseEntity.ok(commentService.getCommentsByPostId(postId, page, size));
        return null;
    }

    // 2. REAL-TIME: Jonli xabar kelganda qabul qilib, hammaga tarqatish
    @MessageMapping("/comment.send") // Frontend /app/comment.send ga yuboradi
    @SendTo("/topic/comments")       // Hammaga /topic/comments orqali yetib boradi
    public CommentDTO sendMessage(@Payload CommentDTO commentDTO) {
        // Bu yerda CommentService orqali xabarni bazaga saqlaysiz
        // commentService.saveComment(commentDTO);

        // Saqlangan xabarni barcha ulanganlarga (shu jumladan yozgan odamga ham) qaytaradi
        return commentDTO;
    }
}
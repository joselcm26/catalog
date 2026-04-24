package com.josec.catalog.controller;

import com.josec.catalog.dto.FollowConnectionResponseDTO;
import com.josec.catalog.service.FollowService;
import com.josec.catalog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class FollowConnectionController {

    @Autowired
    private UserService userService;

    @Autowired
    private FollowService followService;

    @GetMapping("/users/my/followers")
    public List<FollowConnectionResponseDTO> getMyFollowers(){
        return followService.getMyFollowers();
    }

    @GetMapping("/users/my/followed")
    public List<FollowConnectionResponseDTO> getMyFollowedUsers(){
        return followService.getMyFollowedUsers();
    }

    @PostMapping("/users/{id}/follow")
    public ResponseEntity<String> followUser(@PathVariable int id){
        followService.followUser(id);
        return ResponseEntity.ok("Following operation completed successfully.");
    }

    @DeleteMapping("/users/{id}/unfollow")
    public ResponseEntity<String> unfollowUser(@PathVariable int id){
        followService.unfollowUser(id);
        return ResponseEntity.ok("Unfollowing operation completed successfully.");
    }

    @PatchMapping("/users/my/requests/{followerId}/accept")
    public ResponseEntity<String> acceptRequest(@PathVariable int followerId){
        followService.acceptFollowRequest(followerId);
        return ResponseEntity.ok("Follow request accepted.");
    }

    @DeleteMapping("/users/my/requests/{followerId}/reject")
    public ResponseEntity<String> rejectRequest(@PathVariable int followerId){
        followService.rejectFollowRequest(followerId);
        return ResponseEntity.ok("Follow request rejected.");
    }
}

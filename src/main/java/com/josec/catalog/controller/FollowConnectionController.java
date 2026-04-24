package com.josec.catalog.controller;

import com.josec.catalog.service.FollowService;
import com.josec.catalog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class FollowConnectionController {

    @Autowired
    private UserService userService;

    @Autowired
    private FollowService followService;

    @PostMapping("/users/followers/{id}/follow")
    public ResponseEntity<String> followUser(@PathVariable int id){
        followService.followUser(id);
        return ResponseEntity.ok("Following operation completed successfully.");
    }

    @DeleteMapping("/users/followers/{id}/follow")
    public ResponseEntity<String> unfollowUser(@PathVariable int id){
        followService.unfollowUser(id);
        return ResponseEntity.ok("Unfollowing operation completed successfully.");
    }

    @PatchMapping("/users/my/requests/{followerId/accept")
    public ResponseEntity<String> acceptRequest(@PathVariable int followerId){
        followService.acceptFollowRequest(followerId);
        return ResponseEntity.ok("Follow request accepted.");
    }
    
    @DeleteMapping("/users/my/requests/{followerId/reject")
    public ResponseEntity<String> rejectRequest(@PathVariable int followerId){
        followService.rejectFollowRequest(followerId);
        return ResponseEntity.ok("Follow request rejected.");
    }
}

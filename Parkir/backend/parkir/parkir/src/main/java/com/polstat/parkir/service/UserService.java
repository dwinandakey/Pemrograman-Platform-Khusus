package com.polstat.parkir.service;

import com.polstat.parkir.dto.ProfileUpdateDto;
import com.polstat.parkir.dto.UserDto;
public interface UserService{
    UserDto createUser(UserDto userDto);
    UserDto getUserByEmail(String email);
    UserDto updateUserProfile(String currentEmail, ProfileUpdateDto updatedProfile);
    void changePassword(String email, String currentPassword, String newPassword);
    void deleteUser(Long userId);
    void updateUserProfileByAdmin(Long userId, UserDto updatedUser);
    void changePasswordByAdmin(Long userId, String newPassword);
    UserDto getUserById(Long userId);
}

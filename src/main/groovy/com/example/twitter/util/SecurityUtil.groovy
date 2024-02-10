package com.example.twitter.util

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class SecurityUtil {
     static String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication()
        return authentication.getName()
    }

}

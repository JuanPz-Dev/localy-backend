package com.localy.backend.application.port.security;

import com.localy.backend.domain.model.User;

public interface CurrentUserPort {
    User getCurrentUser();
}
package com.clinic.dto.response;

import java.time.LocalDate;

/** What the browser sees of an owner (AppUser). No entity graph, no pets list. */
public class OwnerDTO {
    private Long id;
    private String username;
    private String email;
    private LocalDate createdAt;

    public OwnerDTO() {
    }

    public OwnerDTO(Long id, String username, String email, LocalDate createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public LocalDate getCreatedAt() { return createdAt; }
}

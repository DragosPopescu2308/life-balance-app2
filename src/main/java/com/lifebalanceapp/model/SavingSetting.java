package com.lifebalanceapp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_saving_settings")
public class SavingSetting {

    @Id
    @Column(name = "user_id")
    private Integer userId;

    @Column(nullable = false)
    private Double percentage = 10.0;

    @Column(nullable = false)
    private Boolean active = true;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    public SavingSetting() {}

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Double getPercentage() { return percentage; }
    public void setPercentage(Double percentage) { this.percentage = percentage; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}

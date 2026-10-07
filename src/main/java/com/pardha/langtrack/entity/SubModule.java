package com.pardha.langtrack.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class SubModule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long lessonId;

    private String title;

    private int orderNumber;

    public SubModule() {
    }

    public SubModule(Long lessonId, String title, int orderNumber) {
        this.lessonId = lessonId;
        this.title = title;
        this.orderNumber = orderNumber;
    }

    public Long getId() {
        return id;
    }

    public Long getLessonId() {
        return lessonId;
    }

    public String getTitle() {
        return title;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }
}
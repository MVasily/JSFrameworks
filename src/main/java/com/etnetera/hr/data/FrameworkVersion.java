package com.etnetera.hr.data;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
public class FrameworkVersion {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column(nullable = false)
    @NotNull
    @Min(1)
    Float version;

    @Column
    String description;


    @ManyToOne
    @JoinColumn
    @JsonBackReference
    JavaScriptFramework javaScriptFramework;

    public FrameworkVersion() {
    }

    public FrameworkVersion(float version, String description) {
        this.version = version;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getVersion() {
        return version;
    }

    public void setVersion(Float version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public JavaScriptFramework getJavaScriptFramework() {
        return javaScriptFramework;
    }

    public void setJavaScriptFramework(JavaScriptFramework javaScriptFramework) {
        this.javaScriptFramework = javaScriptFramework;
    }
}

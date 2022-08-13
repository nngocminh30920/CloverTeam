package com.example.warehousemanagement.model.request;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "branch")
public class Branch {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String address;
    private String phone;
    private Boolean active;
    private Long accountId;
    private String accountName;
}

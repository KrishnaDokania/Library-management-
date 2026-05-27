package com.library.model;

public class Member {
    private String id;
    private String name;
    private String email;
    private String phone;
    private double unpaidFines;

    public Member(String id, String name, String email, String phone, double unpaidFines) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.unpaidFines = unpaidFines;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getUnpaidFines() {
        return unpaidFines;
    }

    public void setUnpaidFines(double unpaidFines) {
        this.unpaidFines = unpaidFines;
    }

    public void addFine(double amount) {
        this.unpaidFines += amount;
    }

    public void deductFine(double amount) {
        this.unpaidFines = Math.max(0.0, this.unpaidFines - amount);
    }

    @Override
    public String toString() {
        return String.format("Member[ID=%s, Name=%s, Email=%s, Phone=%s, UnpaidFines=$%.2f]",
                id, name, email, phone, unpaidFines);
    }
}

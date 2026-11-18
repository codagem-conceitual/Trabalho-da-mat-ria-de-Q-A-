package com.example.slabiak.appointmentscheduler.model;

import com.example.slabiak.appointmentscheduler.entity.Work;
import com.example.slabiak.appointmentscheduler.entity.user.User;
import com.example.slabiak.appointmentscheduler.entity.user.customer.CorporateCustomer;
import com.example.slabiak.appointmentscheduler.entity.user.customer.RetailCustomer;
import com.example.slabiak.appointmentscheduler.entity.user.provider.Provider;
import com.example.slabiak.appointmentscheduler.validation.FieldsMatches;
import com.example.slabiak.appointmentscheduler.validation.UniqueUsername;
import com.example.slabiak.appointmentscheduler.validation.groups.*;

import javax.validation.constraints.*;
import java.util.List;

@FieldsMatches(field = "password", matchingField = "matchingPassword", groups = {CreateUser.class})
public class UserForm {

    @NotNull(groups = {UpdateUser.class})
    @Min(value = 1, groups = {UpdateUser.class})
    private int id;

    @UniqueUsername(groups = {CreateUser.class})
    @Size(min = 5, max = 15, groups = {CreateUser.class}, message = "O usuário deve ter de 5 a 15 caracteres")
    @NotBlank(groups = {CreateUser.class})
    private String userName;

    @Size(min = 5, max = 15, groups = {CreateUser.class}, message = "A senha deve ter de 5 a 15 caracteres")
    @NotBlank(groups = {CreateUser.class})
    private String password;

    @Size(min = 5, max = 15, groups = {CreateUser.class}, message = "A senha deve ter de 5 a 15 caracteres")
    @NotBlank(groups = {CreateUser.class})
    private String matchingPassword;

    @NotBlank(groups = {CreateUser.class, UpdateUser.class}, message = "Informe o nome")
    private String firstName;

    @NotBlank(groups = {CreateUser.class, UpdateUser.class}, message = "Informe o sobrenome")
    private String lastName;

    @Email(groups = {CreateUser.class, UpdateUser.class}, message = "E-mail inválido")
    @NotBlank(groups = {CreateUser.class, UpdateUser.class}, message = "Informe o e-mail")
    private String email;

    @Pattern(groups = {CreateUser.class, UpdateUser.class}, regexp = "[0-9]{10,11}", message = "Informe o celular com DDD, só números (ex.: 84999990000)")
    @NotBlank(groups = {CreateUser.class, UpdateUser.class}, message = "Informe o celular")
    private String mobile;

    @Size(groups = {CreateUser.class, UpdateUser.class}, min = 5, max = 30, message = "O bairro deve ter de 5 a 30 caracteres")
    @NotBlank(groups = {CreateUser.class, UpdateUser.class}, message = "Informe o bairro")
    private String street;

    @Pattern(groups = {CreateUser.class, UpdateUser.class}, regexp = "[0-9]{5}-?[0-9]{3}", message = "Informe um CEP válido (ex.: 59000-000)")
    @NotBlank(groups = {CreateUser.class, UpdateUser.class}, message = "Informe o CEP")
    private String postcode;

    @NotBlank(groups = {CreateUser.class, UpdateUser.class}, message = "Informe a cidade")
    private String city;

    /*
     * Só para cliente empresa (CorporateCustomer):
     * */
    @NotBlank(groups = {CreateCorporateCustomer.class, UpdateCorporateCustomer.class}, message = "Informe a empresa")
    private String companyName;

    @Pattern(groups = {CreateCorporateCustomer.class, UpdateCorporateCustomer.class}, regexp = "[0-9]{10}", message = "Informe um número fiscal válido")
    @NotBlank(groups = {CreateCorporateCustomer.class, UpdateCorporateCustomer.class}, message = "Informe o número fiscal")
    private String vatNumber;

    /*
     * Só para estagiário (Provider):
     * */
    @NotNull(groups = {CreateProvider.class, UpdateProvider.class})
    private List<Work> works;


    public UserForm() {
    }

    public UserForm(User user) {
        this.setId(user.getId());
        this.setUserName(user.getUserName());
        this.setFirstName(user.getFirstName());
        this.setLastName(user.getLastName());
        this.setEmail(user.getEmail());
        this.setCity(user.getCity());
        this.setStreet(user.getStreet());
        this.setPostcode(user.getPostcode());
        this.setMobile(user.getMobile());
    }

    public UserForm(Provider provider) {
        this((User) provider);
        this.setWorks(provider.getWorks());
    }

    public UserForm(RetailCustomer retailCustomer) {
        this((User) retailCustomer);
    }

    public UserForm(CorporateCustomer corporateCustomer) {
        this((User) corporateCustomer);
        this.setCompanyName(corporateCustomer.getCompanyName());
        this.setVatNumber(corporateCustomer.getVatNumber());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMatchingPassword() {
        return matchingPassword;
    }

    public void setMatchingPassword(String matchingPassword) {
        this.matchingPassword = matchingPassword;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getVatNumber() {
        return vatNumber;
    }

    public void setVatNumber(String vatNumber) {
        this.vatNumber = vatNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<Work> getWorks() {
        return works;
    }

    public void setWorks(List<Work> works) {
        this.works = works;
    }

}
package com.unnivy.unnivy_app.service.ServicesInterfaces;

public interface IForgotPassword {

    public void forgotPassword(Long user_id);

    public void changePassword(Long id, String password);

    public String encryptPassword(String password);

}

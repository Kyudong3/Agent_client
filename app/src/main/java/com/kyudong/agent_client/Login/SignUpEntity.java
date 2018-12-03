package com.kyudong.agent_client.Login;

/**
 * Created by Kyudong on 2017. 11. 22..
 */

public class SignUpEntity {

    private String id;
    private String pwd;
    private String phone;
    private String addr;

    public SignUpEntity(String id, String pwd, String phone, String addr) {
        this.id = id;
        this.pwd = pwd;
        this.phone = phone;
        this.addr = addr;
    }
}

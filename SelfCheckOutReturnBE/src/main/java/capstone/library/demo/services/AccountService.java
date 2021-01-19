package capstone.library.demo.services;

import capstone.library.demo.dtos.response.LoginResponse;

public interface AccountService {

    LoginResponse checkLogin (String rfid);

}

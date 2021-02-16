package capstone.library.services.impl;

import capstone.library.dtos.request.CreateLibrarianRequest;
import capstone.library.dtos.request.CreatePatronRequest;
import capstone.library.dtos.request.UpdateLibrarianRequest;
import capstone.library.dtos.request.UpdatePatronRequest;
import capstone.library.dtos.response.LibrarianAccountResponse;
import capstone.library.dtos.response.PatronAccountResponse;
import capstone.library.entities.Account;
import capstone.library.entities.PatronType;
import capstone.library.entities.Profile;
import capstone.library.entities.Role;
import capstone.library.enums.RoleIdEnum;
import capstone.library.exceptions.MissingInputException;
import capstone.library.exceptions.ResourceNotFoundException;
import capstone.library.repositories.AccountRepository;
import capstone.library.repositories.PatronTypeRepository;
import capstone.library.repositories.RoleRepository;
import capstone.library.services.AccountService;
import capstone.library.services.MailService;
import capstone.library.util.tools.PasswordUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static capstone.library.util.constants.ConstantUtil.CREATE_SUCCESS;
import static capstone.library.util.constants.ConstantUtil.UPDATE_SUCCESS;

@Service
public class AccountServiceImpl  implements AccountService {

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private PatronTypeRepository patronTypeRepo;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    MailService mailService;


    @Override
    public Account findAccountByEmail(String email) {
        return accountRepo.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Account","cannot find account with email: " +email));
    }

    @Override
    public Page<PatronAccountResponse> findAllPatronAccount(Pageable pageable) {
        Role role = roleRepo.findByName(RoleIdEnum.ROLE_PATRON.name())
                .orElseThrow(() -> new ResourceNotFoundException("Role", "Cannot find role: " + RoleIdEnum.ROLE_PATRON.name()));

        return accountRepo.findAccountsByRoleId(role.getId(), pageable)
                .map(a -> objectMapper.convertValue(a, PatronAccountResponse.class));
    }

    @Override
    public Page<PatronAccountResponse> findPatronByEmail(Pageable pageable, String email) {
        Role role = roleRepo.findByName(RoleIdEnum.ROLE_PATRON.name())
                .orElseThrow(() -> new ResourceNotFoundException("Role", "Cannot find role: " + RoleIdEnum.ROLE_PATRON.name()));

        return accountRepo.findByEmailContainsAndRoleId(email, role.getId(), pageable)
                .map(a -> objectMapper.convertValue(a, PatronAccountResponse.class));
    }

    @Override
    public String activateAccount(int id) {
        Account account = findAccountById(id);
        account.setActive(true);
        accountRepo.save(account);
        return UPDATE_SUCCESS;
    }

    @Override
    public String deactivateAccount(int id) {
        Account account = findAccountById(id);
        account.setActive(false);
        accountRepo.save(account);
        return UPDATE_SUCCESS;
    }

    @Override
    public Page<LibrarianAccountResponse> findAllLibrarianAccount(Pageable pageable) {
        Role role = roleRepo.findByName(RoleIdEnum.ROLE_LIBRARIAN.name())
                .orElseThrow(() -> new ResourceNotFoundException("Role", "Cannot find role: " + RoleIdEnum.ROLE_PATRON.name()));

        return accountRepo.findAccountsByRoleId(role.getId(), pageable)
                .map(a -> objectMapper.convertValue(a, LibrarianAccountResponse.class));
    }

    @Override
    public Page<LibrarianAccountResponse> findLibrarianByEmail(Pageable pageable, String email) {
        Role role = roleRepo.findByName(RoleIdEnum.ROLE_LIBRARIAN.name())
                .orElseThrow(() -> new ResourceNotFoundException("Role", "Cannot find role: " + RoleIdEnum.ROLE_PATRON.name()));

        return accountRepo.findByEmailContainsAndRoleId(email, role.getId(), pageable)
                .map(a -> objectMapper.convertValue(a, LibrarianAccountResponse.class));
    }

    @Override
    @Transactional
    public String createPatronAccount(CreatePatronRequest request) {
        if(request == null){
            throw new MissingInputException("missing create patron account request");
        }
        Role role = roleRepo.findByName(RoleIdEnum.ROLE_PATRON.name())
                .orElseThrow(() -> new ResourceNotFoundException("Role", "Cannot find role: " + RoleIdEnum.ROLE_PATRON.name()));

        Account creator = accountRepo.findById(request.getCreatorId())
                .orElseThrow(() -> new ResourceNotFoundException("Account",
                        "Cannot find account with id: " + request.getCreatorId()));

        PatronType patronType = patronTypeRepo.findById(request.getPatronTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Patron Type",
                        "Cannot find patron type with id: " + request.getPatronTypeId()));

        //generate Password
        String rawPassword = PasswordUtil.generatePassword();
        String encryptedPass = encoder.encode(rawPassword);

        Profile profile = new Profile();
        profile.setFullName(request.getFullName());
        profile.setPhone(request.getPhone());
        profile.setGender(request.getGender());

        Account newAccount = new Account();
        newAccount.setActive(true);
        newAccount.setRfid(request.getRfid());
        newAccount.setCreator(creator);
        newAccount.setUpdater(creator);
        newAccount.setEmail(request.getEmail());
        newAccount.setPassword(encryptedPass);
        newAccount.setAvatar(request.getAvatar());
        newAccount.setPatronType(patronType);
        newAccount.setRole(role);
        newAccount.setProfile(profile);
        profile.setAccount(newAccount);

        accountRepo.save(newAccount);
        mailService.sendAccountPassword(request.getEmail(), rawPassword);
        return CREATE_SUCCESS;
    }

    @Override
    @Transactional
    public String createLibrarianAccount(CreateLibrarianRequest request) {
        if(request == null){
            throw new MissingInputException("missing create patron account request");
        }
        Role role = roleRepo.findByName(RoleIdEnum.ROLE_LIBRARIAN.name())
                .orElseThrow(() -> new ResourceNotFoundException("Role", "Cannot find role: " + RoleIdEnum.ROLE_LIBRARIAN.name()));

        Account creator = accountRepo.findById(request.getCreatorId())
                .orElseThrow(() -> new ResourceNotFoundException("Account",
                        "Cannot find account with id: " + request.getCreatorId()));


        //generate Password
        String rawPassword = PasswordUtil.generatePassword();
        String encryptedPass = encoder.encode(rawPassword);

        Profile profile = new Profile();
        profile.setFullName(request.getFullName());
        profile.setPhone(request.getPhone());
        profile.setGender(request.getGender());

        Account newAccount = new Account();
        newAccount.setRfid(request.getRfid());
        newAccount.setActive(true);
        newAccount.setCreator(creator);
        newAccount.setUpdater(creator);
        newAccount.setEmail(request.getEmail());
        newAccount.setPassword(encryptedPass);
        newAccount.setAvatar(request.getAvatar());
        newAccount.setRole(role);
        newAccount.setProfile(profile);

        profile.setAccount(newAccount);
        accountRepo.save(newAccount);

        mailService.sendAccountPassword(request.getEmail(), rawPassword);

        return CREATE_SUCCESS;
    }

    @Override
    public String updatePatronAccount(UpdatePatronRequest request) {
        if(request == null){
            throw new MissingInputException("missing create patron account request");
        }
        Account updater = accountRepo.findById(request.getUpdaterId())
                .orElseThrow(() -> new ResourceNotFoundException("Account",
                        "Cannot find account with id: " + request.getUpdaterId()));

        PatronType patronType = patronTypeRepo.findById(request.getPatronTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Patron Type",
                        "Cannot find patron type with id: " + request.getPatronTypeId()));
         Account editingPatron = accountRepo
                .findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Account",
                        "Cannot find account with id: " + request.getId()));

         editingPatron.setUpdater(updater);
         editingPatron.setPatronType(patronType);
         editingPatron.setRfid(request.getRfid());
         editingPatron.setAvatar(request.getAvatar());
         editingPatron.getProfile().setFullName(request.getFullName());
         editingPatron.getProfile().setPhone(request.getPhone());
         editingPatron.getProfile().setGender(request.getGender());
         accountRepo.save(editingPatron);
         return UPDATE_SUCCESS;
    }

    @Override
    public String updateLibrarianAccount(UpdateLibrarianRequest request) {
        if(request == null){
            throw new MissingInputException("missing create patron account request");
        }
        Account updater = accountRepo.findById(request.getUpdaterId())
                .orElseThrow(() -> new ResourceNotFoundException("Account",
                        "Cannot find account with id: " + request.getUpdaterId()));

      Account editingPatron = accountRepo
                .findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Account",
                        "Cannot find account with id: " + request.getId()));

        editingPatron.setUpdater(updater);
        editingPatron.setRfid(request.getRfid());
        editingPatron.setAvatar(request.getAvatar());
        editingPatron.getProfile().setFullName(request.getFullName());
        editingPatron.getProfile().setPhone(request.getPhone());
        editingPatron.getProfile().setGender(request.getGender());
        accountRepo.save(editingPatron);

        return UPDATE_SUCCESS;
    }

    private Account findAccountById(int id){
        return  accountRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account",
                        "Cannot find account with id: " + id ));
    }



}

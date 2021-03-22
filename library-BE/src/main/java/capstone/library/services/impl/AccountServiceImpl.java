package capstone.library.services.impl;

import capstone.library.dtos.request.CreateLibrarianRequest;
import capstone.library.dtos.request.CreatePatronRequest;
import capstone.library.dtos.request.UpdateLibrarianRequest;
import capstone.library.dtos.request.UpdatePatronRequest;
import capstone.library.dtos.response.ImportPatronResponse;
import capstone.library.dtos.response.LibrarianAccountResponse;
import capstone.library.dtos.response.PatronAccountResponse;
import capstone.library.entities.Account;
import capstone.library.entities.PatronType;
import capstone.library.entities.Profile;
import capstone.library.entities.Role;
import capstone.library.enums.RoleIdEnum;
import capstone.library.exceptions.ChangePasswordException;
import capstone.library.exceptions.ImportFileException;
import capstone.library.exceptions.MissingInputException;
import capstone.library.exceptions.ResourceNotFoundException;
import capstone.library.repositories.AccountRepository;
import capstone.library.repositories.PatronTypeRepository;
import capstone.library.repositories.RoleRepository;
import capstone.library.security.JwtTokenProvider;
import capstone.library.services.AccountService;
import capstone.library.services.MailService;
import capstone.library.util.tools.ExcelUtil;
import capstone.library.util.tools.PasswordUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;

import java.io.IOException;
import java.util.List;

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

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;



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
    public String activateAccount(int accountId, int auditorId) {
        Account account = findAccountById(accountId);
        Account auditor = findAccountById(auditorId);
        account.setActive(true);
        account.setUpdater(auditor);
        accountRepo.save(account);
        return UPDATE_SUCCESS;
    }

    @Override
    public String deactivateAccount(int accountId, int auditorId) {
        Account account = findAccountById(accountId);
        Account auditor = findAccountById(auditorId);
        account.setActive(false);
        account.setUpdater(auditor);
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

    @Override
    @Transactional
    public String changePassword(int accountId, String oldPass, String newPass) {
        if(oldPass == null || newPass == null){
            throw new MissingInputException("missing oldPass or newPass");
        }
        Account account = findAccountById(accountId);
        if(!encoder.matches(oldPass, account.getPassword())){
            throw new ChangePasswordException("Your current password is wrong");
        }
        if(oldPass.equals(newPass)){
            throw new ChangePasswordException("The new password you entered is the same as your old password");
        }
        account.setPassword(encoder.encode(newPass));
        accountRepo.save(account);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        account.getEmail(),
                        newPass
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        return jwt;
    }

    @Override
    @Transactional
    public ImportPatronResponse importPatron(MultipartFile file, int patronTypeId, int auditorId) {
        //check file;
        ImportPatronResponse rs = new ImportPatronResponse();
        if(!ExcelUtil.hasExcelFormat(file)){
            throw new ImportFileException("Must be excel file format");
        }
        PatronType patronType = patronTypeRepo.findById(patronTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Patron Type",
                        "Cannot find patron type with id: " + patronTypeId));
        Account auditor = accountRepo.findById(auditorId)
                .orElseThrow(() -> new ResourceNotFoundException("Account",
                        "Cannot find account with id: " + auditorId));
        Role role = roleRepo.findByName(RoleIdEnum.ROLE_PATRON.name())
                .orElseThrow(() -> new ResourceNotFoundException("Role",
                        "Cannot find role Patron"));
        try {
            List<Account> accounts = ExcelUtil.excelToAccounts(file.getInputStream());
            for(Account account : accounts){
                account.setPatronType(patronType);
                account.setActive(true);
                account.setCreator(auditor);
                account.setUpdater(auditor);
                account.setRole(role);
                String rawPassword = PasswordUtil.generatePassword();
                String encodedPassword = encoder.encode(rawPassword);
                account.setPassword(encodedPassword);
                ImportPatronResponse.ImportPatron patron = new ImportPatronResponse.ImportPatron(account.getEmail(), rawPassword);
                rs.getImportPatronList().add(patron);
            }
            accountRepo.saveAll(accounts);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open file");
        }
        return rs;
    }

    private Account findAccountById(int id){
        return  accountRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account",
                        "Cannot find account with id: " + id ));
    }


}

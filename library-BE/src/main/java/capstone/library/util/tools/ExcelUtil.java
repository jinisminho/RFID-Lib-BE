package capstone.library.util.tools;

import capstone.library.entities.Account;
import capstone.library.entities.Profile;
import capstone.library.enums.Gender;
import capstone.library.exceptions.ImportFileException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExcelUtil {

    private static final String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String[] HEADERs = {"Email", "RFID", "Full Name", "Phone", "Gender", "Avatar"};
    private static final String SHEET = "Patrons";
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+(\\.[A-Za-z]{2,4}){1,2}$";
    private static final String PHONE_PATTERN = "^(0)[0-9]{9}$";
    private static final String GENDER_PATTERN = "^F|M$";


    public static boolean hasExcelFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType());
    }

    public static List<Account> excelToAccounts(InputStream is) {
        List<Account> accounts = new ArrayList<Account>();
        XSSFWorkbook workbook;
        try {
            workbook = new XSSFWorkbook(is);

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet != null) {
                Iterator<Row> rows = sheet.iterator();
                int rowNumber = 0;
                while (rows.hasNext()) {

                    Row currentRow = rows.next();
                    // skip header
                    if (rowNumber == 0) {
                        //check header
                        if (!checkHeader(currentRow)) {
                            throw new ImportFileException("Header has wrong format, should be: "
                                    + Arrays.stream(HEADERs).sequential().collect(Collectors.joining(",")));
                        }
                        rowNumber++;
                        continue;
                    }

                    Iterator<Cell> cellsInRow = currentRow.iterator();

                    Account account = new Account();
                    Profile profile = new Profile();
                    profile.setAccount(account);
                    account.setProfile(profile);
                    String email;
                    String phone;
                    String rfid;
                    String fullName;
                    String gender;
                    String avatar;
                    int cellIdx = 0;
                    while (cellsInRow.hasNext()) {
                        Cell currentCell = cellsInRow.next();
                        switch (cellIdx) {
                            case 0:
                                email = currentCell.getStringCellValue().trim();
                                System.out.println(email);
                                if (email.isEmpty() || email.length() > 100 || !email.matches(EMAIL_PATTERN)) {
                                    throw new ImportFileException("email at row " + (currentRow.getRowNum() + 1) + " is invalid: required; max: 100; email pattern");
                                }
                                account.setEmail(email);
                                break;

                            case 1:
                                rfid = currentCell.getStringCellValue().trim();
                                if (rfid.isEmpty() || rfid.length() > 80) {
                                    throw new ImportFileException("rfid at row " + (currentRow.getRowNum() + 1) + " is invalid: required, max: 80");
                                }
                                account.setRfid(currentCell.getStringCellValue());
                                break;

                            case 2:
                                fullName = currentCell.getStringCellValue().trim();
                                if (fullName.isEmpty() || fullName.length() > 100) {
                                    throw new ImportFileException("full name at row " + (currentRow.getRowNum() + 1) + " is invalid: required, max: 100");
                                }
                                account.getProfile().setFullName(currentCell.getStringCellValue());
                                break;

                            case 3:
                                if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                                    phone = DoubleFormatter.formatToDecimal(currentCell.getNumericCellValue());
                                }else{
                                    phone = currentCell.getStringCellValue().trim();
                                }
                                if (!phone.matches(PHONE_PATTERN)) {
                                    throw new ImportFileException("phone at row " + (currentRow.getRowNum() + 1) + " is invalid: required; 10 digits");
                                }
                                account.getProfile().setPhone(currentCell.getStringCellValue());
                                break;

                            case 4:
                                gender = currentCell.getStringCellValue().trim();
                                if (!gender.matches(GENDER_PATTERN)) {
                                    throw new ImportFileException("Gender at row " + (currentRow.getRowNum() + 1) + " is invalid: F or M");
                                }
                                account.getProfile().setGender(gender.equals("F") ? Gender.F : Gender.M);
                                break;

                            case 5:
                                avatar = currentCell.getStringCellValue().trim();
                                if (avatar.isEmpty() || avatar.length() > 500) {
                                    throw new ImportFileException("Avatar at row " + (currentRow.getRowNum() + 1) + " is invalid: required; max: 500");
                                }
                                account.setAvatar(currentCell.getStringCellValue());
                                break;

                            default:
                                throw new ImportFileException("Number of column at row: " + (currentRow.getRowNum() + 1) + " must be 6");
                        }

                        cellIdx++;
                    }

                    accounts.add(account);
                }
            }else{
                System.out.println("SHEET is null");
            }
            return accounts;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }

    }

    private static boolean checkHeader(Row header) {
        Iterator<Cell> cellsInRow = header.iterator();
        int cellIdx = 0;
        while (cellsInRow.hasNext()) {
            Cell currentCell = cellsInRow.next();
            switch (cellIdx) {
                case 0:
                    if (!currentCell.getStringCellValue().trim().equals(HEADERs[0])) {
                        return false;
                    }
                    break;
                case 1:
                    if (!currentCell.getStringCellValue().trim().equals(HEADERs[1])) {
                        return false;
                    }
                    break;
                case 2:
                    if (!currentCell.getStringCellValue().trim().equals(HEADERs[2])) {
                        return false;
                    }
                    break;
                case 3:
                    if (!currentCell.getStringCellValue().trim().equals(HEADERs[3])) {
                        return false;
                    }
                    break;
                case 4:
                    if (!currentCell.getStringCellValue().trim().equals(HEADERs[4])) {
                        return false;
                    }
                    break;
                case 5:
                    if (!currentCell.getStringCellValue().trim().equals(HEADERs[5])) {
                        return false;
                    }
                    break;
                default:
                    throw new ImportFileException("Number of column must be 6");

            }
            cellIdx++;

        }
        return true;
    }

}

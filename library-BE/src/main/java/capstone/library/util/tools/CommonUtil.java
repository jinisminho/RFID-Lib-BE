package capstone.library.util.tools;

import capstone.library.dtos.common.FeePolicyDto;
import capstone.library.entities.FeePolicy;
import capstone.library.repositories.FeePolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class CommonUtil {

    @Autowired
    static
    FeePolicyRepository feePolicyRepository;

    public static final Double fineCalc(FeePolicyDto feePolicy, Double price, Integer overdueDays) {
        double fineRate;
        double fine = 0;

        double bookCopyPrice = price;
        fineRate = feePolicy.getOverdueFinePerDay();
        fine = fineRate * overdueDays;
        int maxOverdueFinePercentage = feePolicy.getMaxPercentageOverdueFine();
        double maxOverdueFine = bookCopyPrice * ((double) maxOverdueFinePercentage / 100);
        if (fine >= maxOverdueFine) {
            fine = maxOverdueFine;
        }
        return fine;
    }

    //Tram added
    public static final Double calculateOverdueFine (FeePolicy feePolicy, Double price, Integer overdueDays) {
        double fineRate;
        double fine = 0;

        double bookCopyPrice = price;
        fineRate = feePolicy.getOverdueFinePerDay();
        fine = fineRate * overdueDays;
        int maxOverdueFinePercentage = feePolicy.getMaxPercentageOverdueFine();
        double maxOverdueFine = bookCopyPrice * ((double) maxOverdueFinePercentage / 100);
        if (fine >= maxOverdueFine) {
            fine = maxOverdueFine;
        }
        return fine;
    }
}

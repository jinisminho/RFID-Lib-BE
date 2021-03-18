package capstone.library.demo.util;

import capstone.library.demo.entities.Genre;
import capstone.library.demo.exceptions.MissingInputException;
import org.springframework.http.HttpStatus;

import java.util.List;

public class GenreUtil {

    public static String getGenreFormCallNumber(String callNumber, List<Genre> genres){
        if(callNumber == null){
            throw new MissingInputException("missing callNumber");
        }
        if(callNumber.length() < 3){
            throw new IllegalArgumentException("callNumber is invalid");
        }
        try {
            double callNumberPrefix = Double.parseDouble(callNumber.substring(0, 3));
            if (!genres.isEmpty()) {
                Genre holdingGenre = genres.get(0);
                for (Genre genre : genres) {
                    double tmpDDC = genre.getDdc();
                    if (tmpDDC > callNumberPrefix) {
                        break;
                    }else{
                        holdingGenre = genre;
                    }
                }
                return holdingGenre.getName();
            } else {
                return "N/A";
            }
        }catch (NumberFormatException e){
            throw new RuntimeException("Cannot format call number prefix");
        }
    }
}

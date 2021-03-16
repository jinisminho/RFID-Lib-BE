/**
 * This class provides services to find book through handheld device
 */
package capstone.library.services;

import capstone.library.dtos.request.AddPortableSearchingBooksRequest;
import capstone.library.dtos.response.PortableSearchBookResponse;

import java.util.List;

public interface PortableSearchBookService {

    List<PortableSearchBookResponse> findBookSearchingList(int accountId);

    String addSearchingBooksToFile (AddPortableSearchingBooksRequest request);

    String deleteSearchingFile(int accountId);
}

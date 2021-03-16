package capstone.library.controllers.web;

import capstone.library.dtos.request.AddPortableSearchingBooksRequest;
import capstone.library.dtos.response.PortableSearchBookResponse;
import capstone.library.services.PortableSearchBookService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/portableSearch")
public class PortableSearchBookController {

    @Autowired
    private PortableSearchBookService portableSearchBookService;

    @ApiOperation(value = "This API use add cart or item to searching cart")
    @PostMapping("/add")
    public String addBookToSearchingCart (@RequestBody @Valid AddPortableSearchingBooksRequest request){
        return portableSearchBookService.addSearchingBooksToFile(request);
    }

    @ApiOperation(value = "This API use to delete cart")
    @GetMapping("/delete/{accountId}")
    public String deleteSearchingCart(@PathVariable(name = "accountId")int accountId){
        return portableSearchBookService.deleteSearchingFile(accountId);
    }

    @ApiOperation(value = "This API use to get searching cart")
    @GetMapping("/getCart/{accountId}")
    public List<PortableSearchBookResponse> getSearchingCart(@PathVariable(name = "accountId")int accountId){
        return portableSearchBookService.findBookSearchingList(accountId);
    }


}

package capstone.library.repositories.impl;

import capstone.library.entities.Book;
import capstone.library.repositories.BookRepository;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class BookRepositoryImpl implements BookRepository {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    @SuppressWarnings("unchecked")
    public List<Book> findBooks(String searchValue) {
        SearchSession searchSession = Search.session(entityManager);

        SearchResult<Book> result;

        if (searchValue.length() > 3) {
            result = searchSession.search(Book.class)
                    .where(f -> f.match()
                            .fields("title", "isbn")
                            .matching(searchValue)
                            .fuzzy()
                    )
                    .fetch(20);
        } else {
            result = searchSession.search(Book.class)
                    .where(f -> f.match()
                            .fields("title", "isbn")
                            .matching(searchValue)
                    )
                    .fetch(20);
        }


        List<Book> res = result.hits();

        return res;
    }

    @Override
    public void reindexAll() {
        SearchSession searchSession = Search.session(entityManager);

        // Reindex all
        try {
            searchSession.massIndexer().startAndWait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //
    }
}

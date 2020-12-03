package mobi.cangol.web.pecker.core.repository;

import mobi.cangol.web.pecker.core.model.App;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppRepository extends JpaRepository<App, Long> {

}


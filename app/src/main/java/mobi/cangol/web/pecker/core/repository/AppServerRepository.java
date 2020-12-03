package mobi.cangol.web.pecker.core.repository;

import mobi.cangol.web.pecker.core.model.AppServer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppServerRepository extends JpaRepository<AppServer, Long> {

}


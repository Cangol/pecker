package mobi.cangol.web.pecker.core.service;


import mobi.cangol.web.pecker.core.model.AppServer;
import mobi.cangol.web.pecker.core.repository.AppServerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class AppServerService extends AbstractService<AppServer> {
    @Autowired
    private AppServerRepository appServerRepository;

    protected JpaRepository<AppServer, Long> getRepository() {
        return appServerRepository;
    }

    public AppServer findBy(String host) {
        AppServer t = new AppServer();
        t.setHost(host);
        return getRepository().findOne(Example.of(t)).orElse(null);
    }
}
